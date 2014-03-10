package com.smashedin.smashedin;



import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.smashedin.smashedin.R;
import com.smashedin.async.SmashedAsyncClient;
import com.smashedin.async.SmashedAsyncClient.OnResponseListener;
import com.smashedin.common.NavDrawerItem;
import com.smashedin.common.NavDrawerListAdapter;
import com.smashedin.facebook.HelloFacebookSampleActivity;
import com.smashedin.reviews.ReviewActivity;
import com.smashedin.smashed.*;
import com.smashedin.smashed.GridOverheardSkeletonFragment.OnHeadlineSelectedListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


public class MainActivity extends FragmentActivity implements OnHeadlineSelectedListener,OnResponseListener {
    private Intent m_ohIntent;
    private Intent m_reviewIntent;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	// nav drawer title
	private CharSequence mDrawerTitle;
	private static int m_oPosition = 0;
	private String bid;
	// used to store app title
	private CharSequence mTitle;
	private ListView m_oListView;
	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;
	private int currentPosition = 0;
	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	private ReviewListAdapter m_oRevAdapter;
	private MainActivity m_oMainACtivity;
	ArrayList<ReviewItem> itemsList = null;
	
	private ProgressDialog oPd;
	private Fragment m_oCreateOverHeard;
	android.support.v4.app.Fragment homefragment = null;	
	
	public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "APA91bFV1y_1rYpLaw_yPWSrm7PqMdw5emsZsslqG54nxNwgE2CiEguIL2j7KfoClF9gO3eoX-9Z0_rGDlcnPkVWSkRlNFi7X0GyMCno81UBIfyyb7vz3pGJNix2-yvGF4sHcRjruQwvJ4GNrf1Vd_ba5O8hxv4SP0HyY-YowjCaCMcPgOAdKdA";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    String SENDER_ID = "849208174002";

    /**
     * Tag used on log messages.
     */
    static final String TAG = "GCM Demo";

    TextView mDisplay;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    Context context;

    String regid;

    
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
		//Reviews
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
		//Overheards
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
		//Create
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
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
		

		if (savedInstanceState == null) {
			displayHome();
		}
		// Register mMessageReceiver to receive messages.
		  LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
		      new IntentFilter("my-event"));
		  LocalBroadcastManager.getInstance(this).registerReceiver(mExitMessageReceiver,
			      new IntentFilter("exit-event"));
		  LocalBroadcastManager.getInstance(this).registerReceiver(mSearchReviewMessageReceiver,
			      new IntentFilter("search-event"));
		  
		  context = getApplicationContext();

	        // Check device for Play Services APK. If check succeeds, proceed with GCM registration.
	        if (checkPlayServices()) {
	            gcm = GoogleCloudMessaging.getInstance(this);
	            regid = getRegistrationId(context);

	            if (regid.isEmpty()) {
	                registerInBackground();
	            }
	            else
	            {
	            	Singleton.getInstance().regid = regid;
	            }
	        } else {
	            Log.i(TAG, "No valid Google Play Services APK found.");
	        }
	}
	
	private void displayHome()
	{
		homefragment = new HomeFragment();
		if (homefragment != null) {
			android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, homefragment).addToBackStack( "main" ).commit();

			// update selected item and title, then close the drawer
			setTitle("Home");
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
		Singleton.getInstance().ClearAllOptionMenus();
		Singleton.getInstance().m_bSearchMenuItem = false;
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
		MenuItem m_oLoginMenuItem = menu.findItem(R.id.login);
		if(Singleton.getInstance().m_bHideLoginMenuItem == true)
			m_oLoginMenuItem.setVisible(false);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if(Singleton.getInstance().m_bDrawerClosed == false)
		{			
			if (mDrawerToggle.onOptionsItemSelected(item)) {
				return true;
			}
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		case R.id.login:
			Login(Singleton.getInstance().m_strType);
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

    	mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
		   Fragment fragment = getFragmentManager().findFragmentById(R.id.frame_container);
		   if (fragment instanceof CreateOverHeardFragment) {
		    
				  doExit();

		   }
		   else if(fragment instanceof GridOverheardSkeletonFragment)
		   {
			   Singleton.getInstance().ClearAllOptionMenus();
				if(Singleton.getInstance().loggedIn == true)
				{
					Singleton.getInstance().m_bHideLoginMenuItem = true;
					   Singleton.getInstance().m_bSaveMenuItem = false;
					   Singleton.getInstance().m_bShareMenuItem = true;
				}
				else
				{
					Singleton.getInstance().m_bHideLoginMenuItem = false;
					   Singleton.getInstance().m_bSaveMenuItem = true;
					   Singleton.getInstance().m_bShareMenuItem = false;
				}

			   Singleton.getInstance().m_bRowAddMenuItem = false;

			   this.invalidateOptionsMenu();
		    	FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.popBackStack();
				getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

		   }
		   else if(fragment instanceof EachOhTextView)
		   {
			   Singleton.getInstance().ClearAllOptionMenus();
			   if(Singleton.getInstance().loggedIn == true)
				{
					Singleton.getInstance().m_bHideLoginMenuItem = true;
					   Singleton.getInstance().m_bSaveMenuItem = false;
					   Singleton.getInstance().m_bShareMenuItem = true;
				}
				else
				{
					Singleton.getInstance().m_bHideLoginMenuItem = false;
					   Singleton.getInstance().m_bSaveMenuItem = true;
					   Singleton.getInstance().m_bShareMenuItem = false;
				}

				  Singleton.getInstance().m_bRowAddMenuItem = false;
				  Singleton.getInstance().m_bSaveMenuItem = false;
				  this.invalidateOptionsMenu();
				  FragmentManager fragmentManager = getFragmentManager();
					fragmentManager.popBackStack();

		   }	
		   else
		   {
			doExit();
		   }
		   //super.onBackPressed();
		}
	private void doExit()
	{
	    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

	    alertDialog.setPositiveButton("Yes", new OnClickListener() {

	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	        	
	            finish();

	        }
	    });

	    alertDialog.setNegativeButton("No", null);

	    alertDialog.setMessage("Do you want to exit?");
	    alertDialog.setTitle("SmashedIn");
	    alertDialog.show();

	}
	private void Login(String type)
	{
		Singleton.getInstance().m_oType = type;
		String accessToken = Singleton.getInstance().getAccessToken(); 
		if(accessToken == "NOT_FOUND")
		{
            Intent intent = new Intent(m_oMainACtivity, HelloFacebookSampleActivity.class);
            startActivity(intent);
            return;
		}
		else
		{
			String provider = Singleton.getInstance().GetProvider(); 
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
        	if(provider.equals("google") == true)
        	{
            	url = "http://www.smashed.in/auth/post/google?access_token="+accessToken;
        	}
        	SmashedAsyncClient oAsyncClient = new SmashedAsyncClient();
        	oAsyncClient.Attach(this);
        	oAsyncClient.SetPersistantStorage(getApplicationContext());
        	oAsyncClient.MakeCall(url);        	
		}
	}
	private void ShowCreateOverheard()
	{
		if(Singleton.getInstance().m_bnevermind == true)
		{
			Singleton.getInstance().m_bHideLoginMenuItem = false;
			Singleton.getInstance().loggedIn = false;
		}
		else
		{
			Singleton.getInstance().m_bHideLoginMenuItem = true;
			Singleton.getInstance().loggedIn = true;
		}
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
			if(fragmentManager.getBackStackEntryCount() == 0)
				fragmentManager.beginTransaction()
					.add(R.id.frame_container, fragment).addToBackStack( "main" ).commit();

		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}


	}
	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	private void displayView(int position) {
		currentPosition = position;
		// update the main content by replacing fragments
		Singleton.getInstance().ClearAllOptionMenus();
		Singleton.getInstance().m_bSearchMenuItem = false;
		homefragment.getView().setVisibility(View.INVISIBLE);
		switch (position) {		
		case 0:
			mDrawerLayout.closeDrawer(mDrawerList);
			mDrawerList.setItemChecked(0, true);
			mDrawerList.setSelection(0);

			Singleton.getInstance().m_oType = "reviews";
			if(m_reviewIntent == null)
				m_reviewIntent = new Intent(m_oMainACtivity, ReviewActivity.class);
            startActivity(m_reviewIntent);

			return;
		case 1:
			mDrawerLayout.closeDrawer(mDrawerList);
			mDrawerList.setItemChecked(0, true);
			mDrawerList.setSelection(0);

			Singleton.getInstance().m_oType = "oh";
			if(m_ohIntent == null)
				m_ohIntent = new Intent(m_oMainACtivity, OverHeardActivity.class);
            startActivity(m_ohIntent);

			return;
			//break;
		case 2:
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

		default:
			break;
		}

	}
	public void onArticleSelected(String id,String url) {
    	FragmentManager fragmentManager = getFragmentManager();
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
    	checkPlayServices();
	    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);        
	    if(m_reviewIntent != null)
	    	m_reviewIntent.removeExtra(SearchManager.QUERY);
        super.onResume(); 
        
        if(Singleton.getInstance().m_oType.equals("create") == true) {
	    	ShowCreateOverheard();
	    } 
        else
        {
        	if(currentPosition != m_oPosition)
        		displayView(m_oPosition);
        }

    }

	@Override
	public void OnResponse(String response) {
		if(Singleton.getInstance().m_oType == "push")
		{
			Singleton.getInstance().m_oType = "oh";
			return;
		}
		if(oPd != null)
			oPd.dismiss();
		Singleton.getInstance().loggedIn = true;
		Singleton.getInstance().parseJsonUserDetails(response);
		if(Singleton.getInstance().m_oType.equals("create") == true)
		{
			ShowCreateOverheard();
		}
		
	}
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		  @Override
		  public void onReceive(Context context, Intent intent) {
		    // Extract data included in the Intent
		    m_oPosition = intent.getIntExtra("position", 0);
		    //if(m_oCreateOverHeard == null)
		    	//m_oPosition = m_oPosition + 1;
		    bid = intent.getStringExtra("bid");
		    if(bid != null)
		    	Singleton.getInstance().bid = bid;
		    else
		    	Singleton.getInstance().bid = "";
		  }

	};
	private BroadcastReceiver mExitMessageReceiver = new BroadcastReceiver() {
		  @Override
		  public void onReceive(Context context, Intent intent) {
			  finish();
		  }

	};
	private BroadcastReceiver mSearchReviewMessageReceiver = new BroadcastReceiver() {
		  @Override
		  public void onReceive(Context context, Intent intent) {
			  String query = intent.getStringExtra("query");
			  if(m_reviewIntent == null)
					m_reviewIntent = new Intent(m_oMainACtivity, ReviewActivity.class);
			  m_reviewIntent.putExtra(SearchManager.QUERY, query);
			  m_reviewIntent.putExtra("MAINSTUFF","true");
			  displayView(0);
		  }

	};
	@Override
	public void OnFailure() {
		if(oPd != null)
			oPd.dismiss();
		
	}
	 /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device will send
                    // upstream messages to a server that echo back the message using the
                    // 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
             //   mDisplay.append(msg + "\n");
            }
        }.execute(null, null, null);
    }

    // Send an upstream message.
    public void onClick(final View view) {

        if (true){//view == findViewById(R.id.send)) {
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    String msg = "";
                    try {
                        Bundle data = new Bundle();
                        data.putString("my_message", "Hello World");
                        data.putString("my_action", "com.google.android.gcm.demo.app.ECHO_NOW");
                        String id = Integer.toString(msgId.incrementAndGet());
                        gcm.send(SENDER_ID + "@gcm.googleapis.com", id, data);
                        msg = "Sent message";
                    } catch (IOException ex) {
                        msg = "Error :" + ex.getMessage();
                    }
                    return msg;
                }

                @Override
                protected void onPostExecute(String msg) {
                    mDisplay.append(msg + "\n");
                }
            }.execute(null, null, null);
        } 
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }
    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send
     * messages to your app. Not needed for this demo since the device sends upstream messages
     * to a server that echoes back the message using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
      /*Singleton.getInstance().m_oType = "push";
  	String url = "http://www.smashed.in/api/b/register?id="+regid;
	SmashedAsyncClient oAsyncClient = new SmashedAsyncClient();
	oAsyncClient.Attach(this);
	oAsyncClient.SetPersistantStorage(getApplicationContext());
	oAsyncClient.MakeCall(url); */
		Singleton.getInstance().regid = regid;

    }
	
}
