package com.example.smashed;
import com.example.smashed.NumberPicker.OnChangedListener;
import com.example.smashedin.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.database.Cursor;

import com.example.smashedin.*;
import com.loopj.android.image.SmartImageView;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class CreateOverHeardFragment extends Fragment {
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

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		Singleton.getInstance().m_bCameraMenuItem = true;
		Singleton.getInstance().m_bGalleryMenuItem = true;
		Singleton.getInstance().m_bRowAddMenuItem = false;
		Singleton.getInstance().m_bSaveMenuItem = false;
		Singleton.getInstance().m_bSearchMenuItem = true;
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
	        	}
	        	Singleton.getInstance().m_bCameraMenuItem = false;
				Singleton.getInstance().m_bGalleryMenuItem = false;
				Singleton.getInstance().m_bRowAddMenuItem = true;
				Singleton.getInstance().m_bSaveMenuItem = true;
				Singleton.getInstance().m_bSearchMenuItem = false;
				getActivity().invalidateOptionsMenu();
	        	FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.add(R.id.frame_container, gridSkelView).addToBackStack( "tag" ).commit();
				
            
	        }
	    });
	    
        return rootView;
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
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.popBackStack();
		gAdapter.m_overheardData.AddTexts(text1,text2,position);
		GridView gridView = (GridView) getActivity().findViewById(R.id.grid_view);
	    gridView.setAdapter(gAdapter);
	}
	public void AddFromCamera()
	{
		takePhoto();
	}
	public void AddFromGallery()
	{
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
      	startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE);
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
	
			com.example.smashed.NumberPicker oNumPicker = new com.example.smashed.NumberPicker(getActivity().getApplicationContext());
			oNumPicker.setOnChangeListener(new OnChangedListener() {
				
				@Override
				public void onChanged(com.example.smashed.NumberPicker picker, int oldVal,
						int newVal) {
					 RefreshRows(newVal);
					
				}
			});
			oNumPicker.mStart = 1;
			oNumPicker.mEnd = 4;
			oNumPicker.mCurrent = 1;
			dialog.setContentView(oNumPicker);
			dialog.setTitle("Add Rows");
			dialog.setCancelable(true);
		}
		dialog.show();
		
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1) {
        	Uri selectedImageUri;
            if (requestCode == SELECT_PICTURE) {
	        	FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, m_createFragment).commit();
				selectedImageUri = data.getData();
            }
            else
            {
            	selectedImageUri = Uri.parse(path);
            }
            if(gAdapter.m_overheardData.mThumbIds.get(selectedPosition) != null)
    			gAdapter.m_overheardData.Remove(selectedPosition);
    		gAdapter.m_overheardData.AddImage(selectedPosition,"uritheju"+selectedImageUri.toString());

        	FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, m_createFragment).commit();

        }
    }
	 public void takePhoto()
    {
         Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
         File folder = new File(Environment.getExternalStorageDirectory() + "/LoadImg");

         if(!folder.exists())
         {
             folder.mkdir();
         }        
         final Calendar c = Calendar.getInstance();
         String new_Date= c.get(Calendar.DAY_OF_MONTH)+"-"+((c.get(Calendar.MONTH))+1)   +"-"+c.get(Calendar.YEAR) +" " + c.get(Calendar.HOUR) + "-" + c.get(Calendar.MINUTE)+ "-"+ c.get(Calendar.SECOND);
         path=String.format(Environment.getExternalStorageDirectory() +"/LoadImg/%s.png","LoadImg("+new_Date+")");
         File photo = new File(path);
         intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(photo));
         startActivityForResult(intent, 2);
    }
	    
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, filePathColumn, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    @Override
	public void onResume() {
		  Singleton.getInstance().m_bCameraMenuItem = true;
		  Singleton.getInstance().m_bGalleryMenuItem = true;
		  Singleton.getInstance().m_bRowAddMenuItem = false;
		  Singleton.getInstance().m_bSaveMenuItem = false;
		  Singleton.getInstance().m_bSearchMenuItem = true;
		  getActivity().invalidateOptionsMenu();
	     super.onResume();
	  }
	public void UpdateSkel(String id, String url) {
		if(gAdapter.m_overheardData.mThumbIds.get(selectedPosition) != null)
			gAdapter.m_overheardData.Remove(selectedPosition);
		gAdapter.m_overheardData.AddImage(selectedPosition,url);
		Singleton.getInstance().m_bCameraMenuItem = true;
		Singleton.getInstance().m_bGalleryMenuItem = true;
		Singleton.getInstance().m_bRowAddMenuItem = false;
		Singleton.getInstance().m_bSaveMenuItem = false;
		Singleton.getInstance().m_bSearchMenuItem = true;
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
				getActivity().invalidateOptionsMenu();
	        	FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.add(R.id.frame_container, gridSkelView).addToBackStack( "tag" ).commit();
				
            
	        }
	    });
	   
	}
}
