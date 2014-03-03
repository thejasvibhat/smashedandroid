package com.example.smashed;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;

import com.example.async.SmashedAsyncClient;
import com.example.async.SmashedAsyncClient.OnResponseListener;
import com.example.reviews.SmashedReview;
import com.example.search.SampleRecentSuggestionsProvider;
import com.example.smashedin.*;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

public class HomeFragment extends android.support.v4.app.Fragment implements OnResponseListener{
	private static final String[] CONTENT = new String[] { "INFO", "REVIEWS","OVERHEARDS", "PHOTOS" };
	String m_StrResponse;
	android.support.v4.app.Fragment m_cuFragment;
	private ArrayList<String> m_ohpromolist;
	int currentPage = 0;
	int NUM_PAGES = 0;
	private Menu optionsMenu;
	public HomeFragment(){}
	private SearchView searchView;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        GetPromoOh();
        m_cuFragment = this;
        setHasOptionsMenu(true);
        ImageView img = (ImageView) rootView.findViewById(R.id.searchFs);
        img.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				showSearch();
				
			}
		});
        		
    	ProgressBar oProg = (ProgressBar) rootView.findViewById(R.id.progressHome);
    	oProg.setVisibility(View.VISIBLE);

        return rootView;
    }
	@Override
	public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
		this.optionsMenu = menu;
		SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        MenuItem oSearchMenu = menu.findItem(R.id.search);
        oSearchMenu.collapseActionView();
        this.optionsMenu = menu;
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setIconifiedByDefault(false);   
          
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

            	searchView.clearFocus();
            	Intent intent = new Intent("search-event");
          	  intent.putExtra("query", query);
          	  LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);


    	       /* SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getActivity().getApplicationContext(),
    	                SampleRecentSuggestionsProvider.AUTHORITY, SampleRecentSuggestionsProvider.MODE);
    	        suggestions.saveRecentQuery(query, null);*/
    	       

                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);
		super.onCreateOptionsMenu(menu,inflater);
	}
	public void showSearch()
	{
		
		 MenuItem oSearchMenu = optionsMenu.findItem(R.id.search);
		 
	        oSearchMenu.expandActionView();
	       // searchView.requestFocus();
	      //  InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	       // imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);
	}
	@Override 
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	  
	    case R.id.searchoverskel:
	    	break;
	    case R.id.search:
	    	break;
	    }
		return super.onOptionsItemSelected(item);
	}
private void GetPromoOh()
{
	SmashedAsyncClient oAsyncClient = new SmashedAsyncClient();
	oAsyncClient.Attach(this);
	oAsyncClient.SetPersistantStorage(getActivity());
	oAsyncClient.MakeCall("http://www.smashed.in/api/oh/list?offset=0&limit=5");   

}
	  @Override
	  public void onResume() {
		  if(Singleton.getInstance().m_oType == "home")
		  {
			  Singleton.getInstance().ClearAllOptionMenus();
			  Singleton.getInstance().m_bCameraMenuItem = false;
			  Singleton.getInstance().m_bGalleryMenuItem = false;
			  getActivity().invalidateOptionsMenu();
		  }
	     super.onResume();
	  }
	    class OhPromoAdapter extends FragmentPagerAdapter {

	        public OhPromoAdapter(android.support.v4.app.FragmentManager fm) {
	            super(fm);
	        }

	        @Override
	        public android.support.v4.app.Fragment getItem(int position) {
	        	return OhPagerFragment.newInstance(CONTENT[position % CONTENT.length],m_ohpromolist.get(position)); 
	        }
	       
	        @Override
	        public CharSequence getPageTitle(int position) {
	            return CONTENT[position % CONTENT.length].toUpperCase();
	        }

	        @Override
	        public int getCount() {
	            return CONTENT.length;
	        }
	        @Override
	        public void destroyItem(ViewGroup container, int position, Object object) {
	            super.destroyItem(container, position, object);
	        }

	    }
		public void ReturnResponseDocument(Document n_oDocument)
		{
			NodeList skelThumbs = n_oDocument.getElementsByTagName("thumburl");
			NodeList skelUrls = n_oDocument.getElementsByTagName("icon");
			NodeList skelIds = n_oDocument.getElementsByTagName("id");
			m_ohpromolist = new ArrayList<String>();
			for(int i=0 ; i < skelThumbs.getLength(); i++)
			{
				Node thumburl = skelThumbs.item(i);
				Node url = skelUrls.item(i);
				Node id = skelIds.item(i);
				m_ohpromolist.add(url.getTextContent());
				
			}
			NUM_PAGES = m_ohpromolist.size();
	        FragmentPagerAdapter adapter1 = new OhPromoAdapter(getActivity().getSupportFragmentManager());

	        final ViewPager pager = (ViewPager)getActivity().findViewById(R.id.ohpager);
	        pager.setAdapter(adapter1);
	        pager.setOnPageChangeListener(new OnPageChangeListener() {
				
				@Override
				public void onPageSelected(int arg0) {
					//currentPage = arg0;
					
				}
				
				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
					currentPage = arg0;
					
				}
				
				@Override
				public void onPageScrollStateChanged(int arg0) {
				//	currentPage = arg0;
					
				}
			});
	        final Handler handler = new Handler();

	        final Runnable Update = new Runnable() {
	            public void run() {
	                if (currentPage == NUM_PAGES - 1) {
	                    currentPage = 0;
	                }
	                pager.setCurrentItem(currentPage++, true);
	            }
	        };

	        Timer swipeTimer = new Timer();
	        swipeTimer.schedule(new TimerTask() {

	            @Override
	            public void run() {
	                handler.post(Update);
	            }
	        }, 5000, 5000);
			
		}

		@Override
		public void OnResponse(String response) {
			ProgressBar oProg = (ProgressBar) getActivity().findViewById(R.id.progressHome);
	    	oProg.setVisibility(View.GONE);
			m_StrResponse = response;
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
	                    	oParser.SetFragment(m_cuFragment);
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

			
		}

}
