package com.smashedin.smashed;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.smashedin.smashedin.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.smashedin.async.SmashedAsyncClient;
import com.smashedin.async.SmashedAsyncClient.OnResponseListener;
import com.smashedin.search.SampleRecentSuggestionsProvider;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.SearchRecentSuggestions;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.MenuItem.OnActionExpandListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class GridOverheardFragment extends Fragment implements OnResponseListener{
	private String m_strTag = "";
	SearchView searchView;
	private static final int SELECT_PICTURE = 1;
	String path = "";
	public ArrayList<String> m_strSkeletonUrls = new ArrayList<String>();
	public ArrayList<String> m_strSkeletonIds = new ArrayList<String>();
	OnHeadlineSelectedListener mCallback;
	private String m_StrUrl;
	private ArrayList<String> m_strBackUpSkeletonUrls = new ArrayList<String>();
	private ArrayList<String> m_bkupThumbIds = new ArrayList<String>();
	private String m_StrResponse;
    // Container Activity must implement this interface
    public interface OnHeadlineSelectedListener {
        public void onArticleSelected(String id,String url);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnHeadlineSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

	public GridOverheardFragment(){}
	public void AddFragment()
	{
		m_curFragment = this;
	}
	public void AddArgument(Fragment oCreateFragment){
        m_curFragment = this;
	}
	private Fragment m_curFragment = null;
	private GridImageSkelAdapter gAdapter = null;
	private Menu optionsMenu = null;
	private View MainView;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
        View rootView = inflater.inflate(R.layout.fragment_skelview, container, false);
        MainView = rootView;
        String query = getActivity().getIntent().getStringExtra(SearchManager.QUERY);
        if(query == null)
        {
	        if(m_strSkeletonUrls.size() != 0)
	        {
	        	SetGridItems((PullToRefreshGridView) rootView.findViewById(R.id.grid_view_skels));
	        }
	        else
	        	GetSkeletonData("");
        }
        else
        {
        	getActivity().getActionBar().setTitle("Search results for '"+query+"'");
        	GetSkeletonData(query);
        }
        if(this.optionsMenu != null)
        {
        	 MenuItem oSearchMenu = optionsMenu.findItem(R.id.searchoverskel);
             oSearchMenu.collapseActionView();
        }
        setHasOptionsMenu(true);
        return rootView;
    }
	@Override
	public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.searchoverskel).getActionView();
        MenuItem oSearchMenu = menu.findItem(R.id.searchoverskel);
        oSearchMenu.collapseActionView();
        this.optionsMenu = menu;
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setIconifiedByDefault(false);   
            oSearchMenu.setOnActionExpandListener(new OnActionExpandListener() {
    			
    			@Override
    			public boolean onMenuItemActionExpand(MenuItem item) {
    				// TODO Auto-generated method stub
    				return true;
    			}
    			
    			@Override
    			public boolean onMenuItemActionCollapse(MenuItem item) {
    				// TODO Auto-generated method stub
    				if(m_strBackUpSkeletonUrls.size() > 0)
                	{
    	        		m_strSkeletonUrls.clear();
    	        		gAdapter.mThumbIds.clear();
    	            	m_strSkeletonUrls.addAll(m_strBackUpSkeletonUrls);
    					gAdapter.mThumbIds.addAll(m_bkupThumbIds);
    					gAdapter.notifyDataSetChanged();
                	}
    				return true;
    			}
    		});
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() 
        {
            @Override
            public boolean onQueryTextChange(String newText) 
            {
                // this is your adapter that will be filtered
            	
                return true;
            }
            @Override
            public boolean onQueryTextSubmit(String query) 
            {
                // this is your adapter that will be filtered
            	m_strBackUpSkeletonUrls.clear();
            	m_bkupThumbIds.clear();
            	m_strBackUpSkeletonUrls.addAll(m_strSkeletonUrls);
            	m_strSkeletonUrls.clear();
            	m_bkupThumbIds.addAll(gAdapter.mThumbIds);
            	gAdapter.mThumbIds.clear();
            	GetSkeletonData(query);
            	searchView.clearFocus();
    	        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getActivity(),
    	                SampleRecentSuggestionsProvider.AUTHORITY, SampleRecentSuggestionsProvider.MODE);
    	        suggestions.saveRecentQuery(query, null);

                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);
		super.onCreateOptionsMenu(menu, inflater);
	}
	@Override 
	public void onResume()
	{
		if(this.optionsMenu != null)
        {
        	 MenuItem oSearchMenu = optionsMenu.findItem(R.id.searchoverskel);
             oSearchMenu.collapseActionView();
        }
		  Singleton.getInstance().m_bShareMenuItem = true;
		  Singleton.getInstance().m_bSearchMenuItem = false;
		  getActivity().invalidateOptionsMenu();
		super.onResume();
	}
	@Override 
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.camera:
	    	AddFromCamera();
	    	break;
	    case R.id.gallery:
	    	AddFromGallery();
	    	break;
	    case R.id.searchoverskel:
	    	break;
	    }
		return super.onOptionsItemSelected(item);
	}

	public void ReturnResponseDocument(Document n_oDocument)
	{
		displayhide(true);
		NodeList skelThumbs = n_oDocument.getElementsByTagName("thumburl");
		NodeList downloadUrls = n_oDocument.getElementsByTagName("downloadurl");
		//NodeList ohCreatorname = n_oDocument.getElementsByTagName("id");
		if(gAdapter == null)
			gAdapter = new GridImageSkelAdapter(getActivity());
		for(int i=0 ; i < skelThumbs.getLength(); i++)
		{
			Node thumburl = skelThumbs.item(i);
			gAdapter.mThumbIds.add(thumburl.getTextContent());
			Node url = downloadUrls.item(i);
			//Node id = skelIds.item(i);
			m_strSkeletonUrls.add(url.getTextContent());
			//m_strSkeletonIds.add(id.getTextContent());
		}
		if(getView() != null)
			SetGridItems((PullToRefreshGridView) getView().findViewById(R.id.grid_view_skels));

	}
	
	private void SetGridItems(PullToRefreshGridView gridView)
	{
		
	    gridView.setAdapter(gAdapter);
	    gridView.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {	        	
	        	mCallback.onArticleSelected("",m_strSkeletonUrls.get(position));
	        }
	    });
	    gridView.setOnRefreshListener(new OnRefreshListener<GridView>() {
			@Override
			public void onRefresh(PullToRefreshBase<GridView> refreshView) {
				GetSkeletonData("");
				displayhide(true);
				
			}
		});
	}
	private void displayhide(boolean bool)
	{
		PullToRefreshGridView oGridView = (PullToRefreshGridView) MainView.findViewById(R.id.grid_view_skels);
		ProgressBar oProgress = (ProgressBar) MainView.findViewById(R.id.progressImageGridSkel);
		if(bool == true)
		{
			oGridView.setVisibility(View.VISIBLE);
			oProgress.setVisibility(View.GONE);
		}
		else
		{
			oGridView.setVisibility(View.GONE);
			oProgress.setVisibility(View.VISIBLE);
			
		}
	}
	private void GetSkeletonData(String tag)
	{
		m_strTag = tag;
		displayhide(false);
		/*
		Toast.makeText(getActivity(),
                "Please wait, connecting to server.",
                Toast.LENGTH_SHORT).show();
        */
        if(m_strTag != "")
        {
        	 m_StrUrl = m_StrUrl + "&tag=" + m_strTag;
        }

		SmashedAsyncClient oAsyncClient = new SmashedAsyncClient();
    	oAsyncClient.Attach(this);
    	oAsyncClient.SetPersistantStorage(getActivity());
    	oAsyncClient.MakeCallWithTag(m_StrUrl,"public");

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
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1) {
        	Uri selectedImageUri;
            if (requestCode == SELECT_PICTURE) {
				selectedImageUri = data.getData();
            }
            else
            {
            	selectedImageUri = Uri.parse(path);
            }
            mCallback.onArticleSelected("","uritheju"+selectedImageUri.toString());
            	
        	FragmentManager fragmentManager = getFragmentManager();
        	fragmentManager.popBackStack();
        }
    }
	 public void takePhoto()
    {
         Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
         File folder = new File(Environment.getExternalStorageDirectory() + "/Overheards");

         if(!folder.exists())
         {
             folder.mkdir();
         }        
         final Calendar c = Calendar.getInstance();
         String new_Date= c.get(Calendar.DAY_OF_MONTH)+"-"+((c.get(Calendar.MONTH))+1)   +"-"+c.get(Calendar.YEAR) +" " + c.get(Calendar.HOUR) + "-" + c.get(Calendar.MINUTE)+ "-"+ c.get(Calendar.SECOND);
         path=String.format(Environment.getExternalStorageDirectory() +"/Overheards/%s.png","Overheards("+new_Date+")");
         File photo = new File(path);
         intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(photo));
         startActivityForResult(intent, 2);
    }
	    
    public String getPath(Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, filePathColumn, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

	public void UpdateTab(String string) {
		// TODO Auto-generated method stub
		
	}

	public void SetUrl(String string) {
		m_StrUrl = string; 
		
	}

	@Override
	public void OnResponse(String response,String tag,Object obj) {
		m_StrResponse = response;
		if(getView() != null)
			((PullToRefreshGridView) getView().findViewById(R.id.grid_view_skels)).onRefreshComplete();
        // Create Inner Thread Class
        Thread background = new Thread(new Runnable() {
             
            // After call for background.start this run method call
            public void run() {
                try {
                    threadMsg(m_StrResponse);

                } catch (Throwable t) {
                    // just end the background thread
                    Log.i("Animation", "Thread  exception " + t);
                }
            }

            private void threadMsg(String msg) {

                if (!msg.equals(null) && !msg.equals("")) {
                    Message msgObj = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("message", msg);
                    msgObj.setData(b);
                    handler.sendMessage(msgObj);
                }
            }

            // Define the Handler that receives messages from the thread and update the progress
            private final Handler handler = new Handler() {

                public void handleMessage(Message msg) {
                    String aResponse = msg.getData().getString("message");

                    if ((null != aResponse)) {
                    	ResponseParser oParser = new ResponseParser(aResponse,getActivity());
                    	oParser.SetFragment(m_curFragment);
                    	oParser.Parse();
                    }
                    else
                    {

                            // ALERT MESSAGE
                            Toast.makeText(
                                    getActivity(),
                                    "Not Got Response From Server.",
                                    Toast.LENGTH_SHORT).show();
                    }   

                }
            };

        });
        // Start Thread
        background.start();  //After call start method thread called run Methods
		
	}

	@Override
	public void OnFailure() {
		if(getView() != null)
		{
			((PullToRefreshGridView) getView().findViewById(R.id.grid_view_skels)).onRefreshComplete();
		}
		
	}

}
