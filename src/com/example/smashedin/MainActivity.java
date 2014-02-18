package com.example.smashedin;

import com.example.async.SmashedAsyncClient;
import com.example.async.SmashedAsyncClient.OnResponseListener;
import com.example.facebook.HelloFacebookSampleActivity;
import com.example.smashed.*;
import com.example.smashed.GridOverheardSkeletonFragment.OnHeadlineSelectedListener;


import java.util.ArrayList;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.app.FragmentManager.BackStackEntry;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements OnHeadlineSelectedListener,OnResponseListener {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private static final String[] CONTENT = new String[] { "Recent", "Artists", "Albums", "Songs", "Playlists", "Genres" };
	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;
	private ListView m_oListView;
	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	private ReviewListAdapter m_oRevAdapter;
	private MainActivity m_oMainACtivity;
	private Menu m_oMenu;
	ArrayList<ReviewItem> itemsList = null;
	
	private ProgressDialog oPd;
	private Fragment m_oCreateOverHeard;
	private String m_oType = "init";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		m_oMainACtivity = this;
		super.onCreate(savedInstanceState);
		Singleton.getInstance().SetApplicationContext(getApplicationContext());

		setContentView(R.layout.activity_main);
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
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1),true,"22"));
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
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);

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
		int data= getIntent().getIntExtra("typeindex", 0);
		if (savedInstanceState == null) {
			// on first time display view for first nav item
			displayView(data);
		}
	}
	
	

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		MenuItem m_oGalleryMenuItem = menu.findItem(R.id.gallery);
		if(Singleton.getInstance().m_bGalleryMenuItem == true)
			m_oGalleryMenuItem.setVisible(false);
		MenuItem m_oCameraMenuItem = menu.findItem(R.id.camera);
		if(Singleton.getInstance().m_bCameraMenuItem == true)
			m_oCameraMenuItem.setVisible(false);
		MenuItem m_oRowAddMenuItem = menu.findItem(R.id.rowadd);
		if(Singleton.getInstance().m_bRowAddMenuItem == true)
			m_oRowAddMenuItem.setVisible(false);
		MenuItem m_oSaveMenuItem = menu.findItem(R.id.saveoh);
		if(Singleton.getInstance().m_bSaveMenuItem == true)
			m_oSaveMenuItem.setVisible(false);
		MenuItem m_oShareMenuItem = menu.findItem(R.id.shareoh);
		if(Singleton.getInstance().m_bShareMenuItem == true)
			m_oShareMenuItem.setVisible(false);
		MenuItem m_oSaveOhTextMenuItem = menu.findItem(R.id.saveohtext);
		if(Singleton.getInstance().m_bSaveOhTextMenuItem == true)
			m_oSaveOhTextMenuItem.setVisible(false);		
		MenuItem m_oSearchMenuItem = menu.findItem(R.id.search);
		if(Singleton.getInstance().m_bSearchMenuItem == true)
			m_oSearchMenuItem.setVisible(false);
		MenuItem m_oSearchOhSkelMenuItem = menu.findItem(R.id.searchoverskel);
		if(Singleton.getInstance().m_bSearchOverheardSkel == true)
			m_oSearchOhSkelMenuItem.setVisible(false);
		
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
	public void onBackPressed() {
		   Fragment fragment = getFragmentManager().findFragmentById(R.id.frame_container);
		   if (fragment instanceof CreateOverHeardFragment) {
		          
				  Singleton.getInstance().m_bCameraMenuItem = true;
				  Singleton.getInstance().m_bGalleryMenuItem = true;
				  Singleton.getInstance().m_bRowAddMenuItem = false;
				  Singleton.getInstance().m_bSaveMenuItem = false;
				  Singleton.getInstance().m_bShareMenuItem = true;
				  Singleton.getInstance().m_bSaveOhTextMenuItem = false;
				  Singleton.getInstance().m_bSearchMenuItem = true;
				  Singleton.getInstance().m_bSearchOverheardSkel = true;
				  this.invalidateOptionsMenu();

		   }
		   if(fragment instanceof GridOverheardSkeletonFragment)
		   {
				  Singleton.getInstance().m_bCameraMenuItem = true;
				  Singleton.getInstance().m_bGalleryMenuItem = true;
				  Singleton.getInstance().m_bRowAddMenuItem = false;
				  Singleton.getInstance().m_bSaveMenuItem = false;
				  Singleton.getInstance().m_bShareMenuItem = true;
				  Singleton.getInstance().m_bSaveOhTextMenuItem = true;
				  Singleton.getInstance().m_bSearchMenuItem = true;
				  Singleton.getInstance().m_bSearchOverheardSkel = true;
				  this.invalidateOptionsMenu();
			   
		   }
		   if(fragment instanceof EachOhTextView)
		   {
				  Singleton.getInstance().m_bCameraMenuItem = true;
				  Singleton.getInstance().m_bGalleryMenuItem = true;
				  Singleton.getInstance().m_bRowAddMenuItem = false;
				  Singleton.getInstance().m_bSaveMenuItem = false;
				  Singleton.getInstance().m_bShareMenuItem = true;
				  Singleton.getInstance().m_bSaveOhTextMenuItem = true;
				  Singleton.getInstance().m_bSearchMenuItem = true;
				  Singleton.getInstance().m_bSearchOverheardSkel = true;
				  this.invalidateOptionsMenu();
			   
		   }		   
		   super.onBackPressed();
		}
	private void Login(String type)
	{
		m_oType = type;
		String accessToken = Singleton.getInstance().getAccessToken(); 
		if(accessToken == "NOT_FOUND")
		{
            Intent intent = new Intent(m_oMainACtivity, HelloFacebookSampleActivity.class);
            startActivity(intent);
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
	private void ShowCreateOverheard()
	{
		Singleton.getInstance().loggedIn = true;
		Fragment fragment = null;
		Singleton.getInstance().m_bCameraMenuItem = true;
		Singleton.getInstance().m_bGalleryMenuItem = true;
		Singleton.getInstance().m_bRowAddMenuItem = false;
		Singleton.getInstance().m_bSaveMenuItem = false;
		//Singleton.getInstance().m_bShareMenuItem = true;
		Singleton.getInstance().m_bSaveOhTextMenuItem = true;
		Singleton.getInstance().m_bSearchMenuItem = true;
		if(m_oCreateOverHeard == null)
		{
			fragment = new CreateOverHeardFragment();
			m_oCreateOverHeard = fragment;
		}
		else
		{
			fragment = m_oCreateOverHeard;
		}
		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).addToBackStack( "main" ).commit();

		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}


	}
	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	private void displayView(int position) {
		// update the main content by replacing fragments
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
			fragment = new HomeFragment();
			/*Toast.makeText(getBaseContext(),
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
                        	ResponseParser oParser = new ResponseParser(aResponse,m_oMainACtivity);
                        	oParser.Parse();
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
            background.start(); */ //After call start method thread called run Methods
			break;
		case 2:
			mDrawerLayout.closeDrawer(mDrawerList);
			mDrawerList.setItemChecked(0, true);
			mDrawerList.setSelection(0);

			m_oType = "oh";
            Intent intent = new Intent(m_oMainACtivity, OverHeardActivity.class);
            startActivity(intent);

			return;
			//break;
		case 3:
			if(Singleton.getInstance().loggedIn != true)
			{
				Login("create");
			}		
			else
			{
				ShowCreateOverheard();
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
		}
	}
	public void onArticleSelected(String id,String url) {
    	FragmentManager fragmentManager = getFragmentManager();
    	Fragment ofrag = fragmentManager.findFragmentByTag("main");
		fragmentManager.popBackStack();

		((CreateOverHeardFragment) m_oCreateOverHeard).UpdateSkel(id,url);
    }
	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */
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
			DisplayBar(position);
		}
	}
	private void DisplayBar(int position)
	{
		String url = "http://www.smashed.in/"+itemsList.get(position).getId();
		Fragment fragment = new EachReview();
		((EachReview) fragment).SetUrl(url);

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {;
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
	@Override
    protected void onResume() {		
        super.onResume();
  	  if(m_oType == "create") {
	    	ShowCreateOverheard();
	    } 

    }

	@Override
	public void OnResponse(String response) {
		if(oPd != null)
			oPd.dismiss();
		Singleton.getInstance().loggedIn = true;
		if(m_oType == "create")
		{
			ShowCreateOverheard();
		}
		
	}
	
	
}