package com.example.smashed;
import com.example.async.SmashedAsyncClient;
import com.example.async.SmashedAsyncClient.OnResponseListener;
import com.example.config.OverheardData;
import com.example.smashed.NumberPicker.OnChangedListener;
import com.example.smashedin.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;

import com.example.smashedin.*;
import com.loopj.android.http.RequestParams;
import com.loopj.android.image.SmartImageView;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract.Document;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class CreateOverHeardFragment extends Fragment implements OnResponseListener {
	public Fragment m_createFragment;
	private GridImageAdapter gAdapter = null;
	private EachOhTextView m_fEachTextView;
	public CreateOverHeardFragment(){
		m_createFragment = this;
	}
	private static final int SELECT_PICTURE = 1;
	private String selectedImagePath;
	private SmartImageView selectedImage;
	private int selectedPosition = 0;
	private Fragment gridSkelView = null;
	String path = "";
	private Dialog dialog;
	public Menu optionsMenu;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		Singleton.getInstance().m_bCameraMenuItem = true;
		Singleton.getInstance().m_bGalleryMenuItem = true;
		Singleton.getInstance().m_bRowAddMenuItem = false;
		Singleton.getInstance().m_bSaveMenuItem = false;
		Singleton.getInstance().m_bShareMenuItem = true;
		Singleton.getInstance().m_bSaveOhTextMenuItem = true;
		Singleton.getInstance().m_bSearchMenuItem = true;
		Singleton.getInstance().m_bSearchOverheardSkel = true;
		getActivity().invalidateOptionsMenu();
        View rootView = inflater.inflate(R.layout.fragment_createoverheard, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.grid_view);
        // Instance of ImageAdapter Class
        if(gAdapter == null)
        {
        	gAdapter = new GridImageAdapter(getActivity());
        	gAdapter.AddArgument(m_createFragment);
        }
		if(gAdapter.m_overheardData.mThumbIds.size() == 0)
			gAdapter.m_overheardData.AddLocalOverheard();
	    gridView.setAdapter(gAdapter);
	    gridView.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        	selectedImage = (SmartImageView)((RelativeLayout)((FrameLayout)v).getChildAt(0)).getChildAt(0);
	        	selectedPosition = position;
	        	if(gridSkelView == null)
	        	{
	        		gridSkelView = new GridOverheardSkeletonFragment();	    
	        		((GridOverheardSkeletonFragment) gridSkelView).AddArgument(m_createFragment);
	        		((GridOverheardSkeletonFragment) gridSkelView).SetUrl("http://www.smashed.in/api/oh/skel-list?offset=0&limit=100");
	        	}
	        	Singleton.getInstance().m_bCameraMenuItem = false;
				Singleton.getInstance().m_bGalleryMenuItem = false;
				Singleton.getInstance().m_bRowAddMenuItem = true;
				Singleton.getInstance().m_bSaveMenuItem = true;
				Singleton.getInstance().m_bShareMenuItem = true;
				Singleton.getInstance().m_bSaveOhTextMenuItem = false;
				Singleton.getInstance().m_bSearchMenuItem = true;
				Singleton.getInstance().m_bSearchOverheardSkel = false;
				getActivity().invalidateOptionsMenu();
	        	FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.add(R.id.frame_container, gridSkelView).addToBackStack( "tag" ).commit();
				
            
	        }
	    });
	    setHasOptionsMenu(true);
        return rootView;
    }
	@Override
	public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
		this.optionsMenu = menu;
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override 
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.saveoh:
	    	setRefreshActionButtonState(true);
	    	try {
				UploadOh();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				setRefreshActionButtonState(false);
			}
	    	break;
	    case R.id.shareoh:
	    	ShareOverheard();
	    	break;
	    case R.id.rowadd:
	    	AddRowDialog();
	    	break;
	    default:
	    	return super.onOptionsItemSelected(item);
	    }
		return true;
	}
	public void setRefreshActionButtonState(final boolean refreshing) {
	    if (optionsMenu != null) {
	        final MenuItem refreshItem = optionsMenu.findItem(R.id.saveoh);
	        if (refreshItem != null) {
	            if (refreshing) {
	                refreshItem.setActionView(R.layout.actionbar_indefinite_progress);
	            } else {
	                refreshItem.setActionView(null);
	                Singleton.getInstance().m_bShareMenuItem = false;
	                getActivity().invalidateOptionsMenu();
	            }
	        }
	    }
	}
	public void AddOverheardText(int position,int width,int height)
	{
		m_fEachTextView = new EachOhTextView();
		m_fEachTextView.setArguments(m_createFragment,position,gAdapter.m_overheardData,width,height);
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.add(R.id.frame_container, m_fEachTextView).addToBackStack( "frameview" ).commit();
	}
	public void ReturnText(String text1,String text2,int position)
	{
		Singleton.getInstance().m_bRowAddMenuItem = false;
		Singleton.getInstance().m_bSaveMenuItem = false;
		//Singleton.getInstance().m_bShareMenuItem = true;
		Singleton.getInstance().m_bSaveOhTextMenuItem = true;
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.popBackStack();
		gAdapter.m_overheardData.AddTexts(text1,text2,position);
		GridView gridView = (GridView) getActivity().findViewById(R.id.grid_view);
	    gridView.setAdapter(gAdapter);
	}
	private void UploadOh() throws Exception
	{
		OverheardData oData = OverheardData.getInstance();
		org.w3c.dom.Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
	    // create root: <record>
	    Element root = doc.createElement("root");
	    doc.appendChild(root);
	    int oHeight = (int) (Singleton.dipToPixels(getActivity(), 400)/oData.mThumbIds.size());
	    root.setAttribute("width", String.valueOf(Singleton.dipToPixels(getActivity(), 350)));
	    root.setAttribute("height", String.valueOf(Singleton.dipToPixels(getActivity(), 400)));
	    root.setAttribute("type", "conversation");
	    root.setAttribute("rows", String.valueOf(oData.mThumbIds.size()));
	    root.setAttribute("columns", "1");
	    GridView mGridView = (GridView) getActivity().findViewById(R.id.grid_view);
	    ArrayList<CSmartImageView> oImageViews = new ArrayList<CSmartImageView>();
	    final int size = mGridView.getChildCount();
	    for(int i = 0; i < size; i++) {
	      ViewGroup gridChild = (ViewGroup) mGridView.getChildAt(i);
	      int childSize = gridChild.getChildCount();
	      for(int k = 0; k < childSize; k++) {
	    	  if(gridChild.getChildAt(k) instanceof RelativeLayout)
	    	  {
		    	  RelativeLayout rel = (RelativeLayout) gridChild.getChildAt(k) ;
		    	  
		        if( rel.getChildAt(0) instanceof CSmartImageView ) {
		          oImageViews.add((CSmartImageView) rel.getChildAt(0));
		        }
	    	  }
	      }
	    }
	    for(int i=0; i < oData.mThumbIds.size(); i++)
	    {
		    // create: <study>
		    Element imageview = doc.createElement("imagebase");
		    root.appendChild(imageview);
		    // add attr: id =
		    imageview.setAttribute("id",oData.mResIds.get(i));
		    CSmartImageView oImg = oImageViews.get(i);
		    LayoutParams oParams = oImg.getLayoutParams();
		    int imgHeight = oParams.height;
		    int imgWidth = oParams.width;
		    if(oHeight < oParams.height)
		    {
		    	imgHeight = oHeight;
		    	imgWidth = imgHeight*oParams.width/oParams.height;
		    }
		    imageview.setAttribute("width",String.valueOf(imgWidth));
		    imageview.setAttribute("height",String.valueOf(imgHeight));
		    		    
		 
		    Element tt = doc.createElement("toptext");
		    imageview.appendChild(tt);
		    // add attr: id =
		    tt.setAttribute("text",oData.mTopTexts.get(i));

		    Element bt = doc.createElement("bottomtext");
		    imageview.appendChild(bt);
		    // add attr: id =
		    bt.setAttribute("text",oData.mBottomTexts.get(i));
	    	
	    }
	    
	    // create Transformer object
	    Transformer transformer = TransformerFactory.newInstance().newTransformer();
	    StringWriter writer = new StringWriter();
	    StreamResult result = new StreamResult(writer);
	    transformer.transform(new DOMSource(doc), result);
	 
	    // return XML string
	    String upString =  writer.toString();
	    UploadOhString(upString);
	    
	}
	private void UploadOhString(String data)
	{
		SmashedAsyncClient oAsyncClient = new SmashedAsyncClient();
    	oAsyncClient.Attach(this);
    	oAsyncClient.SetPersistantStorage(getActivity());
    	RequestParams oParams = new RequestParams();
    	oParams.put("data",data);
    	oAsyncClient.MakePostCall("http://www.smashed.in/api/oh/savemobile?mode=private",oParams);   
	}
	public void RefreshRows(int numRows)
	{
		gAdapter.m_overheardData.Clear();
		for(int i=0; i < numRows;i++)
		{
			gAdapter.m_overheardData.AddLocalOverheard();
		}
		GridView gridView = (GridView) getActivity().findViewById(R.id.grid_view);
        // Instance of ImageAdapter Class
	    gridView.setAdapter(gAdapter);
	}
	public void AddRowDialog()
	{
		if(dialog == null)
		{
			dialog = new Dialog(getActivity());
			NumberPicker oNumPicker = new NumberPicker(getActivity());
			oNumPicker.setMaxValue(4);
			oNumPicker.setMinValue(1);
			oNumPicker.setValue(gAdapter.m_overheardData.mThumbIds.size());
			oNumPicker.setOnValueChangedListener(new OnValueChangeListener() {
				
				@Override
				public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
					 RefreshRows(newVal);
					
				}
			});
			
			dialog.setContentView(oNumPicker);
			dialog.setTitle("Add Rows");
			dialog.setCancelable(true);
		}
		dialog.show();
		
	}
    @Override
	public void onResume() {
		  Singleton.getInstance().m_bCameraMenuItem = true;
		  Singleton.getInstance().m_bGalleryMenuItem = true;
		  Singleton.getInstance().m_bRowAddMenuItem = false;
		  Singleton.getInstance().m_bSaveMenuItem = false;
		  Singleton.getInstance().m_bSaveOhTextMenuItem = true;
		  Singleton.getInstance().m_bSearchMenuItem = true;
		  Singleton.getInstance().m_bSearchOverheardSkel = true;

		  getActivity().invalidateOptionsMenu();
	     super.onResume();
	  }
	public void UpdateSkel(String id, String url) {
		if(gAdapter.m_overheardData.mThumbIds.get(selectedPosition) != null)
			gAdapter.m_overheardData.Remove(selectedPosition);
		gAdapter.m_overheardData.AddImage(selectedPosition,url);
		gAdapter.m_overheardData.AddImageId(selectedPosition,id);
		Singleton.getInstance().m_bCameraMenuItem = true;
		Singleton.getInstance().m_bGalleryMenuItem = true;
		Singleton.getInstance().m_bRowAddMenuItem = false;
		Singleton.getInstance().m_bSaveMenuItem = false;
		Singleton.getInstance().m_bShareMenuItem = true;
		Singleton.getInstance().m_bSaveOhTextMenuItem = true;
		Singleton.getInstance().m_bSearchMenuItem = true;
		Singleton.getInstance().m_bSearchOverheardSkel = true;
		getActivity().invalidateOptionsMenu();
        GridView gridView = (GridView) getActivity().findViewById(R.id.grid_view);
        // Instance of ImageAdapter Class
        if(gAdapter == null)
        {
        	gAdapter = new GridImageAdapter(getActivity());
        	gAdapter.AddArgument(m_createFragment);
        }
		if(gAdapter.m_overheardData.mThumbIds.size() == 0)
			gAdapter.m_overheardData.AddLocalOverheard();
	    gridView.setAdapter(gAdapter);
	    gridView.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        	selectedImage = (SmartImageView)((RelativeLayout)((FrameLayout)v).getChildAt(0)).getChildAt(0);
	        	selectedPosition = position;
	        	if(gridSkelView == null)
	        	{
	        		gridSkelView = new GridOverheardSkeletonFragment();	    
	        		((GridOverheardSkeletonFragment) gridSkelView).AddArgument(m_createFragment);
	        	}
	        	Singleton.getInstance().m_bCameraMenuItem = false;
				Singleton.getInstance().m_bGalleryMenuItem = false;
				Singleton.getInstance().m_bRowAddMenuItem = true;
				Singleton.getInstance().m_bSaveMenuItem = true;
				Singleton.getInstance().m_bShareMenuItem = true;
				Singleton.getInstance().m_bSaveOhTextMenuItem = true;
				getActivity().invalidateOptionsMenu();
	        	FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.add(R.id.frame_container, gridSkelView).addToBackStack( "skel" ).commit();
				
            
	        }
	    });
	    CheckAndUpdateActionBar();
	}
	public void CheckAndUpdateActionBar()
	{
		boolean lExists = false;
		for(String url:gAdapter.m_overheardData.mThumbIds)
		{
			if(url.contains("uri"))
			{
				lExists = true;
				break;
			}
		}
		if(lExists == true)
		{
			if(Singleton.getInstance().m_bShareMenuItem == true)
			{
				Singleton.getInstance().m_bShareMenuItem = false;
				getActivity().invalidateOptionsMenu();
			}
		}
		else
		{
			if(Singleton.getInstance().m_bShareMenuItem == false)
			{
				Singleton.getInstance().m_bShareMenuItem = true;
				getActivity().invalidateOptionsMenu();
			}
		}
	}
	private void ShareOverheard()
	{
		View u = getActivity().findViewById(R.id.grid_view);
	    GridView mGridView = (GridView) getActivity().findViewById(R.id.grid_view);
	    ArrayList<ImageView> oImageViews = new ArrayList<ImageView>();
	    final int size = mGridView.getChildCount();
	    for(int i = 0; i < size; i++) {
	      ViewGroup gridChild = (ViewGroup) mGridView.getChildAt(i);
	      int childSize = gridChild.getChildCount();
	      for(int k = 0; k < childSize; k++) {
	    	  if(gridChild.getChildAt(k) instanceof ImageView)
	    	  {
		          oImageViews.add((ImageView) gridChild.getChildAt(k));
		      }
	    	}
	    }
	    for(ImageView img:oImageViews)
	    {
	    	img.setVisibility(ImageView.INVISIBLE); 
	    }
        u.setDrawingCacheEnabled(true);                                                
        int totalHeight = u.getHeight();
        int totalWidth = u.getWidth();
        u.layout(0, 0, totalWidth, totalHeight);    
        u.buildDrawingCache(true);
        Bitmap b = Bitmap.createBitmap(u.getDrawingCache());             
        u.setDrawingCacheEnabled(false);
        String localAbsoluteFilePath = saveImageLocally(b);
        if (localAbsoluteFilePath!=null && localAbsoluteFilePath!="") {

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            Uri phototUri = Uri.parse(localAbsoluteFilePath);

            File file = new File(phototUri.getPath());


            if(file.exists()) {
                // file create success

            } else {
                // file create fail
            }
            shareIntent.setData(phototUri);
            shareIntent.setType("image/png");
            shareIntent.putExtra(Intent.EXTRA_STREAM, phototUri);
            startActivity(Intent.createChooser(Intent.createChooser(shareIntent, "Share Via"), "Share Image"));

        }
        for(ImageView img:oImageViews)
	    {
	    	img.setVisibility(ImageView.VISIBLE); 
	    }
	}
	@Override
	public void OnResponse(String response) {
		// TODO Auto-generated method stub
		setRefreshActionButtonState(false);
        
	}
	 private String saveImageLocally(Bitmap _bitmap) {
	        File outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
	        File outputFile = null;
	        try {
	            outputFile = File.createTempFile("tmp", ".png", outputDir);
	        } catch (IOException e1) {
	            // handle exception
	        }

	        try {
	            FileOutputStream out = new FileOutputStream(outputFile);
	            _bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
	            out.close();

	        } catch (Exception e) {
	            // handle exception
	        }

	        return outputFile.getAbsolutePath();
	    }
}
