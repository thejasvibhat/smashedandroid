package com.smashedin.smashed;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;

import com.smashedin.smashedin.R;
import com.smashedin.async.SmashedAsyncClient;
import com.smashedin.async.SmashedAsyncClient.OnResponseListener;
import com.smashedin.reviews.ReviewData;
import com.smashedin.smashedin.*;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

public class HomeFragment extends android.support.v4.app.Fragment implements OnResponseListener{
	private static final String[] CONTENT = new String[] { "INFO", "REVIEWS","OVERHEARDS", "PHOTOS" };
	String m_StrResponse;
	android.support.v4.app.Fragment m_cuFragment;
	private ArrayList<String> m_ohpromolist;
	int currentPage = 0;
	int currentPageReviews = 0;
	int NUM_PAGES = 0;
	int NUM_PAGES_REVIEWS = 0;
	private Menu optionsMenu;
	public HomeFragment(){}
	private SearchView searchView;
	private String type = "oh";
	private ArrayList<ReviewData> m_arrReviews = new ArrayList<ReviewData>();
    FragmentPagerAdapter adapter;

    ViewPager pager;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        
        m_cuFragment = this;
        setHasOptionsMenu(true);
     /*   ImageView img = (ImageView) rootView.findViewById(R.id.searchFs);
        img.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				showSearch();
				
			}
		});
       */ 		
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
		type = "oh";
		SmashedAsyncClient oAsyncClient = new SmashedAsyncClient();
		oAsyncClient.Attach(this);
		oAsyncClient.SetPersistantStorage(getActivity());
		oAsyncClient.MakeCall("http://www.smashed.in/api/oh/list?offset=0&limit=5");   
	
	}
	private void GetLatestReviews()
	{
		type = "rev";
		SmashedAsyncClient oAsyncClient = new SmashedAsyncClient();
		oAsyncClient.Attach(this);
		oAsyncClient.SetPersistantStorage(getActivity());
		oAsyncClient.MakeCall("http://www.smashed.in/api/b/fslatestcomments");   
		
	}
	  @Override
	  public void onResume() {

			  GetPromoOh();
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
	            return CONTENT[position % CONTENT.length];
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
	    class ReviewPromoAdapter extends FragmentPagerAdapter {

	        public ReviewPromoAdapter(android.support.v4.app.FragmentManager fm) {
	            super(fm);
	        }

	        @Override
	        public android.support.v4.app.Fragment getItem(int position) {
	        	return ReviewPagerFragment.newInstance(CONTENT[position % CONTENT.length],m_arrReviews.get(position)); 
	        }
	       
	        @Override
	        public CharSequence getPageTitle(int position) {
	            return CONTENT[position % CONTENT.length];
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
			m_ohpromolist = new ArrayList<String>();
			for(int i=0 ; i < skelThumbs.getLength(); i++)
			{
				Node url = skelUrls.item(i);
				m_ohpromolist.add(url.getTextContent());
				
			}
			GetLatestReviews();
			NUM_PAGES = m_ohpromolist.size();
	        adapter = new OhPromoAdapter(getActivity().getSupportFragmentManager());

	        pager = (ViewPager)getActivity().findViewById(R.id.ohpager);
	        pager.setAdapter(adapter);
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
		private void ParseJson(String response) throws JSONException
		{
			JSONObject jsonObj ;
			try {
				jsonObj	= (JSONObject) new JSONTokener(response).nextValue();	
			} catch (Exception e) {
				return;
			}
			
			
			JSONArray items = (JSONArray) jsonObj.getJSONArray("reviews");		
			int length			= items.length();
			
			if (length > 0) {
				for (int i = 0; i < length; i++) {
					ReviewData orev = new ReviewData();
					JSONObject revs = (JSONObject)items.get(i);
					orev.name = revs.getString("username");
					orev.barname = revs.getString("barname");
					orev.rating = revs.getString("rating");
					orev.review = revs.getString("review");
					
					m_arrReviews.add(orev);
				}
			}
			NUM_PAGES_REVIEWS = m_arrReviews.size();
			FragmentPagerAdapter adapter1 = new ReviewPromoAdapter(getActivity().getSupportFragmentManager());

	        final ViewPager pager1 = (ViewPager)getActivity().findViewById(R.id.reviewpager);
	        pager1.setAdapter(adapter1);
	        pager1.setOnPageChangeListener(new OnPageChangeListener() {
				
				@Override
				public void onPageSelected(int arg0) {
					//currentPage = arg0;
					
				}
				
				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
					currentPageReviews = arg0;
					
				}
				
				@Override
				public void onPageScrollStateChanged(int arg0) {
				//	currentPage = arg0;
					
				}
			});
	        final Handler handler = new Handler();

	        final Runnable Update = new Runnable() {
	            public void run() {
	                if (currentPageReviews == NUM_PAGES_REVIEWS - 1) {
	                	currentPageReviews = 0;
	                }
	                pager1.setCurrentItem(currentPageReviews++, true);
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
	    	if(type == "rev")
	    	{
	    		try {
					ParseJson(response);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		return;
	    	}
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
