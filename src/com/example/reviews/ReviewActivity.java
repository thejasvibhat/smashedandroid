package com.example.reviews;
import com.example.smashedin.*;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.example.async.SmashedAsyncClient;
import com.example.async.SmashedAsyncClient.OnResponseListener;
import com.example.common.NavDrawerItem;
import com.example.common.NavDrawerListAdapter;
import com.example.facebook.HelloFacebookSampleActivity;
import com.example.search.SampleRecentSuggestionsProvider;
import com.example.smashed.GridOverheardFragment.OnHeadlineSelectedListener;
import com.example.smashed.ReviewItem;
import com.example.smashed.ReviewListAdapter;
import com.example.smashed.Singleton;
import com.example.smashedin.*;


import android.app.ActionBar;
import android.app.ProgressDialog;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class ReviewActivity extends FragmentActivity  implements OnHeadlineSelectedListener,OnResponseListener {
    private LocationManager locationManager;
    private GridReviewsAdapter gAdapter = null;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	// nav drawer title
	private CharSequence mDrawerTitle;
	private ProgressDialog oPd;
	// used to store app title
	private CharSequence mTitle;
	private ListView m_oListView;
	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	private ReviewListAdapter m_oRevAdapter;
	private NavDrawerListAdapter adapterlist;
	private android.app.Fragment galleryFragment;
	private android.app.Fragment myUploadFragment;
	ArrayList<ReviewItem> itemsList = null;
	android.app.Fragment m_OhFragment;
	private Tab m_galTab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Singleton.getInstance().m_bShareMenuItem = true;
        setContentView(R.layout.reviewmain);
        if(gAdapter == null)
        	gAdapter = new GridReviewsAdapter(getApplicationContext());
       /* ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        //actionBar.setDisplayShowTitleEnabled(false);

        Tab tab = actionBar.newTab()
                           .setText("Gallery")
                           .setTabListener(new TabListener() {
							
							@Override
							public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public void onTabSelected(Tab arg0, FragmentTransaction arg1) {
								
							}
							
							@Override
							public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
								// TODO Auto-generated method stub
								
							}
						});
        actionBar.addTab(tab);
        m_galTab = tab;
        Tab tab1 = actionBar.newTab()
                .setText("My Overheards")
                .setTabListener(new TabListener() {
					
					@Override
					public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onTabSelected(Tab arg0, FragmentTransaction arg1) {
						if(Singleton.getInstance().loggedIn != true)
						{
							Login("reviews");
							return;
						}
						
					}
					
					@Override
					public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
						// TODO Auto-generated method stub
						
					}
				});
        actionBar.addTab(tab1);*/
        
		mTitle = mDrawerTitle = getTitle();

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		navDrawerItems = new ArrayList<NavDrawerItem>();

		// adding nav drawer items to array
		// Home
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		// Find People
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
		// Photos
//		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1),true,"22"));
		// Communities, Will add a counter here
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
		// Pages
		//navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
		// What's hot, We  will add a counter here
		//navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1), true, "50+"));
		

		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapterlist = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapterlist);

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, //nav menu toggle icon
				R.string.app_name, // nav drawer open - description for accessibility
				R.string.app_name // nav drawer close - description for accessibility
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
    }
    @Override
    public void onResume()
    {
    	final ImageView oV = (ImageView) findViewById(R.id.refreshLocation);
    	oV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				ProgressBar oP= (ProgressBar) findViewById(R.id.progressImage);
				oP.setVisibility(View.VISIBLE);
				
				oV.setVisibility(View.GONE);
				GetMyLocation();
			}
		});
    	GetMyLocation();
    }
    private void GetMyLocation()
    {
    	// Acquire a reference to the system Location Manager
    	locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

    	// Define a listener that responds to location updates
    	LocationListener locationListener = new LocationListener() {
    	    public void onLocationChanged(Location location) {
    	      // Called when a new location is found by the network location provider.
    	      makeUseOfNewLocation(location);
				ProgressBar oP= (ProgressBar) findViewById(R.id.progressImage);
				oP.setVisibility(View.GONE);
				ImageView oV= (ImageView) findViewById(R.id.refreshLocation);
				oV.setVisibility(View.VISIBLE);

    	    }

    	    

			public void onStatusChanged(String provider, int status, Bundle extras) {}

    	    public void onProviderEnabled(String provider) {}

    	    public void onProviderDisabled(String provider) {}
    	  };

    	// Register the listener with the Location Manager to receive location updates
    	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 50000, 50000, locationListener);
    }
    private void makeUseOfNewLocation(Location location) {
		// TODO Auto-generated method stub
    	Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
    	StringBuilder builder = new StringBuilder();
    	String localArea = null;
    	try {
    	    List<Address> address = geoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
    	    int maxLines = address.get(0).getMaxAddressLineIndex();
    	    for (int i=0; i<maxLines; i++) {
    	    String addressStr = address.get(0).getAddressLine(i);
    	    if(localArea == null)
    	    {
	    	    String[] locations = addressStr.split(",");
	    	    localArea = locations[locations.length - 1];
    	    }
    	    builder.append(addressStr);
    	    builder.append(" ");
    	    }
    	    
    	String fnialAddress = builder.toString(); //This is the complete address.
    	} catch (IOException e) {}
    	  catch (NullPointerException e) {}
    	
		String url 	= "https://api.foursquare.com/v2/venues/explore?client_id=5MZNWHVUBAKSAYIOD3QZZ5X2IDLCGWKM5DV4P0UJ3PFLM5P2&client_secret=XSZAZ5XHDOEBBGJ331T4UNVGY5S2MHU0XJVEETV2SC5RWERC&v=20130815&ll="+location.getLatitude()+","+location.getLongitude()+"&venuePhotos=1&offset=0&limit=50";
    	//String url 	= "https://api.foursquare.com/v2/venues/explore?client_id=5MZNWHVUBAKSAYIOD3QZZ5X2IDLCGWKM5DV4P0UJ3PFLM5P2&client_secret=XSZAZ5XHDOEBBGJ331T4UNVGY5S2MHU0XJVEETV2SC5RWERC&v=20130815&ll=12.97,77.64&venuePhotos=1&offset=0&limit=50";

		TextView oText = (TextView) findViewById(R.id.locationText);
		oText.setText(localArea);
    	SmashedAsyncClient oAsyncClient = new SmashedAsyncClient();
    	oAsyncClient.Attach(this);
    	oAsyncClient.SetPersistantStorage(getApplicationContext());
    	oAsyncClient.MakeCall(url);        	

	}
    private void Login(String type)
	{
		Singleton.getInstance().m_oType = type;
		String accessToken = Singleton.getInstance().getAccessToken(); 
		if(accessToken == "NOT_FOUND")
		{
            Intent intent = new Intent(getApplicationContext(), HelloFacebookSampleActivity.class);
            startActivity(intent);
            getActionBar().selectTab(m_galTab);
            return;
		}
		else
		{
			if(oPd == null)
			{
				oPd = new ProgressDialog(this);
				oPd.setTitle("Trying to get Smashed...");
				oPd.setMessage("Please wait.");
				oPd.setIndeterminate(true);
				oPd.setCancelable(false);
				oPd.show();
			}

        	String url = "http://www.smashed.in/auth/post/facebook?access_token="+accessToken;
        	SmashedAsyncClient oAsyncClient = new SmashedAsyncClient();
        	oAsyncClient.Attach(this);
        	oAsyncClient.SetPersistantStorage(getApplicationContext());
        	oAsyncClient.MakeCall(url);        	
		}
	}
	@Override
	public void onBackPressed() {
		
		   android.app.Fragment fragment = getFragmentManager().findFragmentById(R.id.frame_container_oh);
		  if (1==1){//fragment instanceof OverHeardFragment) {
			   getFragmentManager().popBackStack();
			   getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			   Singleton.getInstance().m_bShareMenuItem = true;
			   Singleton.getInstance().m_bSearchMenuItem = false;
			   this.invalidateOptionsMenu();
		   }
		   else
		   {
			   super.onBackPressed();
		   }
		   
	}

    private void displayView(int position) {
    	if(position == 2)
    		position = 3;
    	Intent intent = new Intent("my-event");
    	  // add data
    	  intent.putExtra("position", position);
    	  LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        //Intent intent = new Intent(this, MainActivity.class);
        //intent.putExtra("typeindex", position);
        //startActivity(intent);
        finish();
        return;
/*    		// update the main content by replacing fragments
    		Singleton.getInstance().m_bCameraMenuItem = false;
    		Singleton.getInstance().m_bGalleryMenuItem = false;
    		Singleton.getInstance().m_bRowAddMenuItem = true;
    		Singleton.getInstance().m_bSaveMenuItem = true;
    		Singleton.getInstance().m_bShareMenuItem = true;
    		Singleton.getInstance().m_bSaveOhTextMenuItem = true;
    		Singleton.getInstance().m_bSearchMenuItem = false;
    		android.support.v4.app.Fragment fragment = null;
    		switch (position) {
    		case 0:
    			
    			//m_oRowAddMenuItem.setVisible(false);
    			//m_oSaveMenuItem.setVisible(false);
    			fragment = new HomeFragment();
    			break;
    		case 1:
    			Singleton.getInstance().m_bRowAddMenuItem = true;
    			Singleton.getInstance().m_bSaveMenuItem = true;
    			Singleton.getInstance().m_bShareMenuItem = true;
    			Singleton.getInstance().m_bSaveOhTextMenuItem = true;
    			//fragment = new Reviews();
    			Toast.makeText(getBaseContext(),
                        "Please wait, connecting to server.",
                        Toast.LENGTH_SHORT).show();


                // Create Inner Thread Class
                Thread background = new Thread(new Runnable() {
                     
                    private final HttpClient Client = new DefaultHttpClient();
                    private String URL = "http://www.smashed.in/api/b/list";
                     
                    // After call for background.start this run method call
                    public void run() {
                        try {

                            String SetServerString = "";
                            HttpGet httpget = new HttpGet(URL);
                            ResponseHandler<String> responseHandler = new BasicResponseHandler();
                            SetServerString = Client.execute(httpget, responseHandler);
                            threadMsg(SetServerString);

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
                            	//ResponseParser oParser = new ResponseParser(aResponse,m_oMainACtivity);
                            	//oParser.Parse();
                            }
                            else
                            {

                                    // ALERT MESSAGE
                                    Toast.makeText(
                                            getBaseContext(),
                                            "Not Got Response From Server.",
                                            Toast.LENGTH_SHORT).show();
                            }   

                        }
                    };

                });
                // Start Thread
                background.start();  //After call start method thread called run Methods
    			break;
    		case 2:
    			return;
    			//break;
    		case 3:
    			if(Singleton.getInstance().loggedIn != true)
    			{
    				//getApplication().Login("create");
    			}		
    			else
    			{
    				//getApplication().ShowCreateOverheard();
    			}
    			// update selected item and title, then close the drawer
    			mDrawerList.setItemChecked(position, true);
    			mDrawerList.setSelection(position);
    			setTitle(navMenuTitles[position]);
    			mDrawerLayout.closeDrawer(mDrawerList);
    			return;

    		case 4:
    		//	fragment = new PagesFragment();
    			break;
    		case 5:
    		//	fragment = new WhatsHotFragment();
    			break;

    		default:
    			break;
    		}

    		if (fragment != null) {
    			android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
    			fragmentManager.beginTransaction()
    					.replace(R.id.frame_container, fragment).addToBackStack( "main" ).commit();

    			// update selected item and title, then close the drawer
    			mDrawerList.setItemChecked(position, true);
    			mDrawerList.setSelection(position);
    			setTitle(navMenuTitles[position]);
    			mDrawerLayout.closeDrawer(mDrawerList);
    		} else {
    			// error in creating fragment
    			Log.e("MainActivity", "Error in creating fragment");
    		}*/
    	}
	private class SlideMenuClickListener implements
	ListView.OnItemClickListener {
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// display view for selected nav drawer item
		displayView(position);
			}
	}
	public void ReturnResponseDocument(Document n_oDocument)
	{
		Log.e("ReturnData", "Data");
		m_oListView = (ListView) findViewById(R.id.m_oReviewsList);
		itemsList = new ArrayList<ReviewItem>();

		// adding nav drawer items to array
		// Home
		NodeList reviews = n_oDocument.getElementsByTagName("review");
		for(int i=0 ; i < reviews.getLength(); i++)
		{
			Node review = reviews.item(i);
			Element fElement = (Element)review;
			NodeList name = fElement.getElementsByTagName("name");
			NodeList icon = fElement.getElementsByTagName("icon");
			NodeList id = fElement.getElementsByTagName("url");
			itemsList.add(new ReviewItem(name.item(0).getTextContent(), "http://www.smashed.in"+icon.item(0).getTextContent(),id.item(0).getTextContent()));
		}
		

		m_oListView.setOnItemClickListener(new ReviewItemClickListener());

		// setting the nav drawer list adapter
		m_oRevAdapter = new ReviewListAdapter(getApplicationContext(),
				itemsList);
		m_oListView.setAdapter(m_oRevAdapter);

	}
	private class ReviewItemClickListener implements
		ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			//DisplayBar(position);
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.ohmain, menu);
		MenuItem m_oShare = menu.findItem(R.id.shareoh);
		if(Singleton.getInstance().m_bShareMenuItem == true)
			m_oShare.setVisible(false);
		MenuItem m_oSearch = menu.findItem(R.id.searchoverskel);
		if(Singleton.getInstance().m_bSearchMenuItem == true)
			m_oSearch.setVisible(false);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}
	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}
	@Override
	public void onArticleSelected(String id, String url) {
		//((GridOverheardFragment) galleryFragment).ShowOh(id,url);
		Bundle bundle = new Bundle();
        bundle.putString("url", url);
        m_OhFragment.setArguments(bundle);
		android.app.FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.add(R.id.frame_container_oh, m_OhFragment).addToBackStack( "new" ).commit();

		
	}
	private void ParseJson(String response) throws JSONException
	{
		ArrayList<ReviewData> venueList = ListReviewDataSingleton.getInstance().venueList;
		JSONObject jsonObj 	= (JSONObject) new JSONTokener(response).nextValue();
		
		JSONArray groups	= (JSONArray) jsonObj.getJSONObject("response").getJSONArray("groups");
		JSONObject group = (JSONObject) groups.get(0);
		JSONArray items = group.getJSONArray("items");
		
		int length			= items.length();
		
		if (length > 0) {
			for (int i = 0; i < length; i++) {
				ReviewData oRevData = new ReviewData();
				//JSONObject group 	= (JSONObject) groups.get(i);
				//JSONArray items 	= (JSONArray) group.getJSONArray("items");
				
				//int ilength 		= items.length();
				
				//for (int j = 0; j < ilength; j++) {
					JSONObject item = ((JSONObject) items.get(i)).getJSONObject("venue");
					venueList.add(ParseJsonObjectItem(item));
/*					FsqVenue venue 	= new FsqVenue();
					
					venue.id 		= item.getString("id");
					venue.name		= item.getString("name");
					
					JSONObject location = (JSONObject) item.getJSONObject("location");
					
					Location loc 	= new Location(LocationManager.GPS_PROVIDER);
					
					loc.setLatitude(Double.valueOf(location.getString("lat")));
					loc.setLongitude(Double.valueOf(location.getString("lng")));
					
					venue.location	= loc;
					try {
						venue.address	= location.getString("address");	
					} catch (Exception e) {
						venue.address	= "Not Available";
					}
					
					venue.distance	= location.getInt("distance");
					venue.herenow	= item.getJSONObject("hereNow").getInt("count");
					venue.type		= "ads";//item.getString("type");
					
					venueList.add(venue);
				//}
*/			}
		}
		
		gAdapter.FsqVenues = venueList;
		SetGridItems((GridView) findViewById(R.id.reviewsGrid));
	}
	private ReviewData ParseJsonObjectItem(JSONObject item) throws JSONException
	{
		ReviewData venue = new ReviewData();
					
		venue.id 		= item.getString("id");
		venue.name		= item.getString("name");
		try {
			venue.rating    = item.getString("rating");	
		} catch (Exception e) {
			venue.rating    = "1.0";
		}
		
		JSONObject location = (JSONObject) item.getJSONObject("location");
		venue.location = new ReviewLocation();
		venue.location.PopulateData(location);
		try {
			venue.contact = item.getJSONObject("contact").getString("formattedPhone");	
		} catch (Exception e) {
			venue.contact = "";
		} 
		
		try {
			JSONArray photosUrls	= (JSONArray)((JSONObject)((JSONArray) item.getJSONObject("photos").getJSONArray("groups")).get(0)).getJSONArray("items");
			for(int i = 0; i < photosUrls.length(); i++)
			{
				JSONObject pata = (JSONObject)photosUrls.get(i);
				venue.photo = pata.getString("prefix")+"100x100"+pata.getString("suffix");
			}
			
		} catch (Exception e) {
			
		}
		try {
			venue.categories = "";
			JSONArray cats = (JSONArray)item.getJSONArray("categories");
			for(int i = 0; i < cats.length(); i++)
			{
				JSONObject pata = (JSONObject)cats.get(i);
				venue.categories = venue.categories + " " + pata.getString("name");
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		return venue;
	}

	private void SetGridItems(GridView gridView)
	{
		gridView.setAdapter(gAdapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int location,
					long arg3) {
				
	            Intent intent = new Intent(getApplicationContext(), SmashedReview.class);
	            Bundle b = new Bundle();
	            b.putInt("position", location);
	            intent.putExtras(b);
	            startActivity(intent);
				
				
			}
		});
	}
	@Override
	public void OnResponse(String response) {
		if(oPd != null)
			oPd.dismiss();
		Singleton.getInstance().loggedIn = true;
		if(Singleton.getInstance().m_oType == "reviews")
		{
			try {
				ParseJson(response);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
}
