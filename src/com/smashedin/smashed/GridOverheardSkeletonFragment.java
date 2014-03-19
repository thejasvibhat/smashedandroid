package com.smashedin.smashed;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import com.smashedin.config.SkeletonData;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
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

public class GridOverheardSkeletonFragment extends Fragment implements OnResponseListener {
	private String m_strTag = "";
	private String m_strMode = "";
	SearchView searchView;
	private static final int SELECT_PICTURE = 1;
	String path = "";
	private SkeletonData m_galSkeletons = new SkeletonData();
	private SkeletonData m_privSkeletons = new SkeletonData();
	public ArrayList<String> m_strbkupSkeletonUrls = new ArrayList<String>();
	public ArrayList<String> m_strbkupSkeletonIds = new ArrayList<String>();
	public ArrayList<String> m_bkupThumbIds = new ArrayList<String>();
	private Menu optionsMenu;
	OnHeadlineSelectedListener mCallback;
	private String m_StrUrl;
	private String m_StrResponse;
	private String selectedTab = "gallery";
	View m_grootView;
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

	public GridOverheardSkeletonFragment(){}
	public void AddFragment()
	{
		m_curFragment = this;
	}
	public void AddArgument(Fragment oCreateFragment){
        m_curFragment = this;
	}
	private Fragment m_curFragment = null;
	private GridImageSkelAdapter gAdapter = null;
	private View MainView;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
        View rootView = inflater.inflate(R.layout.fragment_skelview, container, false);
        MainView = rootView;
        setHasOptionsMenu(true);
       SetTabs(rootView);
        return rootView;
    }
	public void SetTabs(View orootView)
	{
		m_grootView = orootView;
		  final ActionBar actionBar = getActivity().getActionBar();
		  actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		  if(actionBar.getTabCount() == 0)
		  {
		        Tab tab = actionBar.newTab()
		                           .setText("Gallery")
		                           .setTabListener(new TabListener() {
									
									@Override
									public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
										// TODO Auto-generated method stub
										
									}
									
									@Override
									public void onTabSelected(Tab arg0, FragmentTransaction arg1) {
										onResume();
										selectedTab = "gallery";
										if(m_galSkeletons.m_strSkeletonIds.size() != 0)
								        {
								        	SetGridItems((PullToRefreshGridView) m_grootView.findViewById(R.id.grid_view_skels));
								        }
								        else
								        	GetSkeletonData("public","");
									}
									
									@Override
									public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
										// TODO Auto-generated method stub
										
									}
								});
		        actionBar.addTab(tab);
		        Tab tab1 = actionBar.newTab()
		                .setText("My Uploads")
		                .setTabListener(new TabListener() {
							
							@Override
							public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public void onTabSelected(Tab arg0, FragmentTransaction arg1) {
								if(Singleton.getInstance().loggedIn != true)
								{
									actionBar.selectTab(actionBar.getTabAt(0));
									/*
									Toast.makeText(getActivity(),
							                "Please Login before you can access your uploads.",
							                Toast.LENGTH_SHORT).show();
							        */

									return;
								}
								selectedTab = "private";
								if(m_privSkeletons.m_strSkeletonIds.size() != 0)
						        {
						        	SetGridItems((PullToRefreshGridView) m_grootView.findViewById(R.id.grid_view_skels));
						        }
						        else
						        	GetSkeletonData("private","");
							}
							
							@Override
							public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
								// TODO Auto-generated method stub
								
							}
						});
		        actionBar.addTab(tab1);
		  }
		  else
		  {
			  int length = actionBar.getTabCount();
			  for (int i=0; i < length; i++)
			  {
				  //actionBar.addTab(actionBar.getTabAt(i));
			  }
		  }
	}
	
	@Override 
	public void onResume()
	{
		  ActionBar actionBar1 = getActivity().getActionBar();
		  if(actionBar1.getNavigationMode() != ActionBar.NAVIGATION_MODE_TABS)
			  actionBar1.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

	       DrawerLayout mDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
	       mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);        
	       
		Singleton.getInstance().ClearAllOptionMenus();
		if(Singleton.getInstance().loggedIn != true)
		{
			Singleton.getInstance().m_bHideLoginMenuItem = false;
		}
		else
		{
			Singleton.getInstance().m_bHideLoginMenuItem = true;
		}

    	Singleton.getInstance().m_bCameraMenuItem = false;
		Singleton.getInstance().m_bGalleryMenuItem = false;
		Singleton.getInstance().m_bSearchOverheardSkel = false;
		Singleton.getInstance().m_bDrawerClosed = true;
		getActivity().invalidateOptionsMenu();
		super.onResume();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
		optionsMenu = menu;
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        MenuItem oSearchMenu = menu.findItem(R.id.searchoverskel);
        oSearchMenu.collapseActionView();
        searchView = (SearchView) menu.findItem(R.id.searchoverskel).getActionView();

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
    				if(m_strbkupSkeletonIds.size() > 0)
                	{
    	        		m_galSkeletons.m_strSkeletonIds.clear();
    	        		m_galSkeletons.m_strSkeletonUrls.clear();
    	        		gAdapter.mThumbIds.clear();
    	        		m_galSkeletons.m_strSkeletonUrls.addAll(m_strbkupSkeletonUrls);
    	        		m_galSkeletons.m_strSkeletonIds.addAll(m_strbkupSkeletonIds);
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
            	m_strbkupSkeletonUrls.clear();
            	m_strbkupSkeletonIds.clear();
            	m_bkupThumbIds.clear();
            	m_strbkupSkeletonUrls.addAll(m_galSkeletons.m_strSkeletonUrls);
            	m_strbkupSkeletonIds.addAll(m_galSkeletons.m_strSkeletonIds);
            	m_galSkeletons.m_strSkeletonUrls.clear();
            	m_galSkeletons.m_strSkeletonIds.clear();
            	m_bkupThumbIds.addAll(gAdapter.mThumbIds);
            	gAdapter.mThumbIds.clear();
            	GetSkeletonData(selectedTab,query);
            	searchView.clearFocus();
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);
		super.onCreateOptionsMenu(menu, inflater);
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
		return true;
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
	public void ReturnResponseDocument(Document n_oDocument)
	{
		displayhide(true);
		NodeList skelThumbs = n_oDocument.getElementsByTagName("thumburl");
		NodeList skelUrls = n_oDocument.getElementsByTagName("url");
		NodeList skelIds = n_oDocument.getElementsByTagName("id");
		if(gAdapter == null && getActivity() != null)
			gAdapter = new GridImageSkelAdapter(getActivity());
		if(selectedTab == "gallery")
		{
			m_galSkeletons.mThumbIds.clear();
			m_galSkeletons.m_strSkeletonUrls.clear();
			m_galSkeletons.m_strSkeletonIds.clear();

		}
		else
		{
			m_privSkeletons.mThumbIds.clear();
			m_privSkeletons.m_strSkeletonUrls.clear();
			m_privSkeletons.m_strSkeletonIds.clear();

		}
		for(int i=0 ; i < skelThumbs.getLength(); i++)
		{
			Node thumburl = skelThumbs.item(i);
			Node url = skelUrls.item(i);
			Node id = skelIds.item(i);
			if(selectedTab == "gallery")
			{
				m_galSkeletons.mThumbIds.add(thumburl.getTextContent());
				m_galSkeletons.m_strSkeletonUrls.add(url.getTextContent());
				m_galSkeletons.m_strSkeletonIds.add(id.getTextContent());
			}
			else
			{
				m_privSkeletons.mThumbIds.add(thumburl.getTextContent());
				m_privSkeletons.m_strSkeletonUrls.add(url.getTextContent());
				m_privSkeletons.m_strSkeletonIds.add(id.getTextContent());
			}

			
		}
		if(getView() == null)
			return;
		if(selectedTab == "gallery")
			gAdapter.mThumbIds.addAll(m_galSkeletons.mThumbIds);
		else
			gAdapter.mThumbIds.addAll(m_privSkeletons.mThumbIds);
		
			SetGridItems((PullToRefreshGridView) getView().findViewById(R.id.grid_view_skels));

	}
	
	private void SetGridItems(PullToRefreshGridView gridView)
	{
		if(gAdapter == null)
			gAdapter = new GridImageSkelAdapter(getActivity());
		gAdapter.mThumbIds.clear();
		if(selectedTab == "private")
		{
			gAdapter.mThumbIds.addAll(m_privSkeletons.mThumbIds);
		}
		else
			gAdapter.mThumbIds.addAll(m_galSkeletons.mThumbIds);
	    gridView.setAdapter(gAdapter);
	    gridView.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {	        	
            	MenuItem searchItem = optionsMenu.findItem(R.id.searchoverskel);
            	searchItem.collapseActionView();
            	
            	if(selectedTab == "private")
            		mCallback.onArticleSelected(m_privSkeletons.m_strSkeletonIds.get(position),m_privSkeletons.m_strSkeletonUrls.get(position));
            	else
            		mCallback.onArticleSelected(m_galSkeletons.m_strSkeletonIds.get(position),m_galSkeletons.m_strSkeletonUrls.get(position));
	        }
	    });
	    gridView.setOnRefreshListener(new OnRefreshListener<GridView>() {
			@Override
			public void onRefresh(PullToRefreshBase<GridView> refreshView) {
				if(selectedTab == "gallery")
				{
					GetSkeletonData("public", "");
				}
				else
				{
					GetSkeletonData("private", "");
				}
				displayhide(true);
				
			}
		});
	}
	private void GetSkeletonData(String mode,String tag)
	{
		m_strTag = tag;
		m_strMode = mode;
		displayhide(false);
		Toast.makeText(getActivity(),
                "Please wait, connecting to server.",
                Toast.LENGTH_SHORT).show();
		String URL = m_StrUrl;

         if(m_strTag != "")
         {
         	 URL = URL + "&tag=" + m_strTag;
         }
         if(m_strMode == "private")
         {
         	 URL = URL + "&mode=mine";
         }
		SmashedAsyncClient oAsyncClient = new SmashedAsyncClient();
    	oAsyncClient.Attach(this);
    	oAsyncClient.SetPersistantStorage(getActivity());
    	oAsyncClient.MakeCall(URL);   

       
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
            mCallback.onArticleSelected("disk","uritheju"+selectedImageUri.toString());
            	
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
		((PullToRefreshGridView) getView().findViewById(R.id.grid_view_skels)).onRefreshComplete();
		
	}

}
