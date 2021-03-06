package com.smashedin.reviews;
import com.google.android.gms.drive.internal.o;
import com.smashedin.smashedin.MainActivity;
import com.smashedin.smashedin.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import android.view.View.OnClickListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.smashedin.async.SmashedAsyncClient;
import com.smashedin.async.SmashedAsyncClient.OnResponseListener;
import com.smashedin.common.NavDrawerItem;
import com.smashedin.common.NavDrawerListAdapter;
import com.smashedin.search.SampleRecentSuggestionsProvider;
import com.smashedin.smashed.ReviewItem;
import com.smashedin.smashed.ReviewListAdapter;
import com.smashedin.smashed.Singleton;
import com.smashedin.smashed.GridOverheardFragment.OnHeadlineSelectedListener;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.SearchRecentSuggestions;
import android.provider.Settings;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View;
import android.view.View.OnFocusChangeListener;


import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.SearchView.OnCloseListener;
import android.widget.TextView;



public class ReviewActivity extends FragmentActivity  implements OnHeadlineSelectedListener,OnResponseListener {
	public boolean isVisible = true;
	private NotificationManager mNotificationManager;
	private NavDrawerItem mFollowDrawerItem;
	private NavDrawerItem mGroupFollowItem;
	private boolean m_bFollowing = false;
	private boolean m_bMyGroups = false;
	SearchView searchView;
	private Menu optionsMenu = null;
	private LocationManager locationManager = null;    
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
	Location m_olocation = null;
	private ArrayList<NavDrawerItem> navDrawerItems;
	private ReviewListAdapter m_oRevAdapter;
	private NavDrawerListAdapter adapterlist;
	ArrayList<ReviewItem> itemsList = null;
	android.app.Fragment m_OhFragment;
	private FragmentActivity m_oRevActivity;
	private ArrayList<ReviewData> m_bkupFsqVenues = new ArrayList<ReviewData>();
	private String m_query = "";
	private ArrayList<ReviewData> venueList;
	String mainStuff = "false";
	LocationListener locationListener = null;
	String localArea = null;
	private CountDownTimer oTimer;
	AtomicInteger msgId = new AtomicInteger();
	 Builder dialog   = null;
	 AlertDialog dialogAlert = null;
	private Handler serviceHandler;
	private ArrayList<DialogId> m_arrDialogs = new ArrayList<DialogId>();
	private ArrayList<String> bids;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bids = MyGroupDataSingleton.getInstance().Repopulate();
        m_oRevActivity = this;
        Singleton.getInstance().m_bShareMenuItem = true;
        setContentView(R.layout.reviewmain);
        if(gAdapter == null)
        	gAdapter = new GridReviewsAdapter(getApplicationContext());
        String query = getIntent().getStringExtra(SearchManager.QUERY);
        mainStuff = getIntent().getStringExtra("MAINSTUFF");
        if(query == null)
        {
	        if(gAdapter.FsqVenues.size() != 0)
	        {
	    		SetGridItems((GridView) findViewById(R.id.reviewsGrid));
	        }
	        
        }
        else
        {
        	m_query = query;
        	getActionBar().setTitle("Search results for '"+query+"'");
        }
        if(this.optionsMenu != null)
        {
        	 MenuItem oSearchMenu = optionsMenu.findItem(R.id.searchoverskel);
             oSearchMenu.collapseActionView();
        }
        
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
//		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		// Find People
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
		// Photos
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
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
		mDrawerList.setItemChecked(0, true);
		mDrawerList.setSelection(0);

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		//getActionBar().setHomeButtonEnabled(true);
		//getActionBar().setHomeAsUpIndicator(R.id.buttonLoginLogout);

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
    	
    	GetTagClouds();
    	LocalBroadcastManager.getInstance(this).registerReceiver(mGroupAcceptReceiver,
  		      new IntentFilter("group-accept-event"));
    	
        if(bids.size() > 0)
        {
    		String url 	= "https://api.foursquare.com/v2/venues/"+bids.remove(bids.size() - 1)+"?client_id=5MZNWHVUBAKSAYIOD3QZZ5X2IDLCGWKM5DV4P0UJ3PFLM5P2&client_secret=XSZAZ5XHDOEBBGJ331T4UNVGY5S2MHU0XJVEETV2SC5RWERC&v=20140305&section=drinks&venuePhotos=1&offset=0&limit=50";
        	SmashedAsyncClient oAsyncClient = new SmashedAsyncClient();
        	oAsyncClient.Attach(this);
        	oAsyncClient.SetPersistantStorage(getApplicationContext());
        	oAsyncClient.MakeCallWithTag(url,"venuefill");        	
        	
        }



    }
    private void sendNotification(String msg,int id) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.notifylargeicon);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.notifysmallicon)
        .setLargeIcon(largeIcon)
        .setContentTitle("Instants Received")
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg))
        .setContentText(msg);
       // mBuilder.setLights(Color.BLUE, 500, 500);
        //long[] pattern = {500,500,500,500,500,500,500,500,500};
        //mBuilder.setVibrate(pattern);
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setAutoCancel(true);
        Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notify);
        mBuilder.setSound(sound);
        mNotificationManager.notify(id, mBuilder.build());
    }	

	private BroadcastReceiver mGroupAcceptReceiver = new BroadcastReceiver() {
		  @Override
		  public void onReceive(Context context, Intent intent) {
			  AlertDialog.Builder builder = new AlertDialog.Builder(m_oRevActivity);
			  String uniqueid = intent.getStringExtra("uniqueid");
    		String bname = intent.getStringExtra("bname");
    		String username = intent.getStringExtra("username");
    		if(((ReviewActivity)m_oRevActivity).isVisible == false)
    		{
    			Singleton.getInstance().m_bRequestGroupNotification = true;
    			sendNotification("Instant Request",MainActivity.NOTIFICATION_ID_REQUEST);
    		}
    		// 2. Chain together various setter methods to set the dialog characteristics
    		builder.setMessage(username+" has requested you to join instants at "+bname)
    		       .setTitle("Instant request");
    		builder.setPositiveButton("Join",new DialogInterface.OnClickListener() {
    			
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    				// TODO Auto-generated method stub
    				try {
    					DialogId newDlg = m_arrDialogs.get(0);
    					String uniqueid = newDlg.id;
        				  MyGroupDataSingleton.getInstance().acceptGroupData.uniqueId = uniqueid;
        				  String url = "http://www.smashed.in/api/b/gcm/groupconfirm?state=in&regid="+Singleton.getInstance().regid+"&uniqueid="+uniqueid;
        		        	SmashedAsyncClient oAsyncClient = new SmashedAsyncClient();
        		        	oAsyncClient.Attach(m_oRevActivity);
        		        	oAsyncClient.SetPersistantStorage(getApplicationContext());
        		        	oAsyncClient.MakeCallWithTag(url, "accept");        



        					dialog.cancel();

    				} catch (Exception e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
    			}
    		});
    		builder.setNegativeButton("No",new DialogInterface.OnClickListener() {
    			
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    				// TODO Auto-generated method stub
    				try {
    				} catch (Exception e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
    			}
    		});
    	               // User cancelled the dialog

    		// 3. Get the AlertDialog from create()
    		AlertDialog dialog = builder.create();
    		DialogId newDlg = new DialogId();
    		newDlg.id = uniqueid;
    		newDlg.dialog = dialog;
    		m_arrDialogs.add(newDlg);
    		  Window window = dialog.getWindow();
    	        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
    	                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
    	        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    	        dialog.show();
		  }

	};

    public void GetTagClouds()
    {
    	if(Singleton.getInstance().m_arrTags.size() == 0)
    	{
    		String url = "http://www.smashed.in/api/oh/gettaglist";
    		SmashedAsyncClient oAsyncClient = new SmashedAsyncClient();
        	oAsyncClient.Attach(this); 
        	oAsyncClient.SetPersistantStorage(getApplicationContext());
        	oAsyncClient.MakeCallWithTag(url,"tagcloud");
    	}
    }
    private void StartAlarmGcm()
    {
    	if(oTimer != null)
    		return;
    	// every 5 minutes refresh socket
    	oTimer = new CountDownTimer(60000*5,60000*5) {
			
			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onFinish() {
				//oTimer.start();
		    	new AsyncTask<Void, Void, String>() {
		            @Override
		            protected String doInBackground(Void... params) {
		                String msg = "";
		                String id = Integer.toString(msgId.incrementAndGet());
						Bundle data = new Bundle();
						data.putString("my_message", "Hello World");
						try {
							Singleton.getInstance().gcm.send(Singleton.getInstance().SENDER_ID + "@gcm.googleapis.com", id, data);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						msg = "Sent message";
		                return msg;
		            }

		            @Override
		            protected void onPostExecute(String msg) {
		            	oTimer.start();
		            }
		        }.execute(null, null, null);
				
			}
		};
		oTimer.start();
    }
    private void FillTheMyGroupData()
    {
    	if(MyGroupDataSingleton.getInstance().m_arrPrivateGroups.size() > 0)
    	{
			
			if(m_bMyGroups)
			{
				return;
			}
			if(mGroupFollowItem == null)
				mGroupFollowItem = new NavDrawerItem("My Groups", R.drawable.reviews);
			adapterlist.navDrawerItems.add(mGroupFollowItem);
			adapterlist.notifyDataSetChanged();
    		m_bMyGroups = true;
    		
    	}
    }
    private void FillTheFollowingBarData()
    {
    	if(Singleton.getInstance().mRevData != null)
    	{
    		ReviewData lData = Singleton.getInstance().mRevData;
    		if(lData.m_bfollow == true)
    		{
    			
    			if(m_bFollowing)
    			{
    				adapterlist.navDrawerItems.remove(adapterlist.navDrawerItems.indexOf(mFollowDrawerItem));
        			adapterlist.notifyDataSetChanged();    				
    			}
    			m_bFollowing = true;
    			if(mFollowDrawerItem == null)
    				mFollowDrawerItem = new NavDrawerItem(lData.name, R.drawable.reviews);
    			mFollowDrawerItem.setTitle(lData.name);
    			mFollowDrawerItem.setIcon(R.drawable.reviews);
    			adapterlist.navDrawerItems.add(mFollowDrawerItem);
    			adapterlist.notifyDataSetChanged();
    		}
    		else
    		{
    			if(m_bFollowing)
    			{
    				adapterlist.navDrawerItems.remove(adapterlist.navDrawerItems.indexOf(mFollowDrawerItem));
        			adapterlist.notifyDataSetChanged();    				
    			}
    		}
    	}
		
    }
    public boolean checkforgps()
    {
    	boolean gps_enabled = false;
    	try{
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
           }catch(Exception ex){}
    	return gps_enabled;
	     
    }
    public boolean checkfornetwork()
    {
    	boolean network_enabled = false;
    	try{
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            }catch(Exception ex){}
    	return network_enabled;
    }
    public boolean CheckForLocation()
    {

       if(checkfornetwork() || checkforgps()){
    	   return true;
       }
       return false;
    }
    private void ShowNoLocationInfo()
    {
    	//whatever case
		TextView oText = (TextView) findViewById(R.id.locationText);
		oText.setText("No Location");
		ProgressBar oP= (ProgressBar) findViewById(R.id.progressImage);
		oP.setVisibility(View.GONE);
		ProgressBar oPg= (ProgressBar) findViewById(R.id.progressImageGrid);
		oPg.setVisibility(View.GONE);

		ImageView oV= (ImageView) findViewById(R.id.refreshLocation);
		oV.setVisibility(View.VISIBLE);
		Toast.makeText(
                this,
                "Unable to list nearby Places as Location service is disabled. You can continue using Search ",
                Toast.LENGTH_LONG).show();
		super.onResume();
    }
    @Override
    protected void onPause() {
      super.onPause();
      isVisible = false;
    }
    @Override
    public void onResume() 
    {
		 NotificationManager mNotificationManager = (NotificationManager)
	                this.getSystemService(Context.NOTIFICATION_SERVICE);
		 mNotificationManager.cancel(MainActivity.NOTIFICATION_ID_REQUEST);

    	isVisible = true;
    	if(dialogAlert != null)
    	{
    		dialogAlert.dismiss();
    		dialogAlert = null;
    		if(CheckForLocation() == false)
    		{
    			ShowNoLocationInfo();
    			return;
    		}
    		
    	}
		mDrawerList.setSelection(0);
    	FillTheFollowingBarData();
    	FillTheMyGroupData();
    	Singleton.getInstance().restoreUserDetails();
    	Singleton.getInstance().m_bAppHidden = false;
    	if(gAdapter != null)
    	{
    		
        	if(Singleton.getInstance().m_bGcmMessages == true)
        	{
        		Singleton.getInstance().m_bGcmMessages = false;
        	
        		FromNotification("navdrawer");
        		gAdapter.FsqVenues.addAll(Singleton.getInstance().FsVenues);
        		super.onResume();
        		return;
        	}
        	if(Singleton.getInstance().m_bFromMyGroups == true)
        	{
        		Singleton.getInstance().m_bFromMyGroups = false;
        	
        		FromNotification("groupdrawer");
        		gAdapter.FsqVenues.addAll(Singleton.getInstance().FsVenues);
        		super.onResume();
        		return;
        	}
        	
        	GetMyLocation();
    	}
    	else
    	{
    		GetMyLocation();
    	}
    	super.onResume();
    }
    private void GetMyLocation()
    {
    	// Acquire a reference to the system Location Manager
    	if(locationManager == null)
    		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    	    
	       if(CheckForLocation() == false){
	    	   Singleton.getInstance().m_bLocationOn = false;
	    	   Builder dialog = new AlertDialog.Builder(this);
	            dialogAlert = dialog.create();
	            dialog.setMessage("SmashedIn uses your current location to suggest nearby Places.It seems location services are disabled on your device.");
	            dialog.setPositiveButton("Enable", new DialogInterface.OnClickListener() {

	                @Override
	                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
	                    // TODO Auto-generated method stub
	                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS );
	                    startActivity(myIntent);
	                    //get gps
	                }
	            });
	            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

	                @Override
	                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
	                    // TODO Auto-generated method stub
	                	Singleton.getInstance().m_bLocationOn = false;
	                	ShowNoLocationInfo();

	                }
	            });
	            dialog.show();
	            return;

	        }
	       Singleton.getInstance().m_bLocationOn = true;
		ProgressBar oP= (ProgressBar) findViewById(R.id.progressImage);
		oP.setVisibility(View.VISIBLE);
		ImageView oV= (ImageView) findViewById(R.id.refreshLocation);
		oV.setVisibility(View.GONE);
		ProgressBar oPG= (ProgressBar) findViewById(R.id.progressImageGrid);
		oPG.setVisibility(View.VISIBLE);
		

    	// Define a listener that responds to location updates
		if(locationListener == null)
		{
	    	locationListener = new LocationListener() {
	    	    public void onLocationChanged(Location olocation) {
	    	    	Singleton.getInstance().m_livelocation = olocation;
	    	    	if(m_olocation == null)
	    	    	{
		    	    	m_olocation = olocation;
		    	      // Called when a new location is found by the network location provider.
		    	      makeUseOfNewLocation(olocation);
						ProgressBar oP= (ProgressBar) findViewById(R.id.progressImage);
						oP.setVisibility(View.GONE);
						ImageView oV= (ImageView) findViewById(R.id.refreshLocation);
						oV.setVisibility(View.VISIBLE);
	    	    	}
	
	    	    }
	
	    	    
	
				public void onStatusChanged(String provider, int status, Bundle extras) {}
	
	    	    public void onProviderEnabled(String provider) {}
	
	    	    public void onProviderDisabled(String provider) {}
	    	  };
		}
		if(m_olocation != null)
		{
			oP.setVisibility(View.GONE);
	    	float distance = m_olocation.distanceTo(Singleton.getInstance().m_livelocation);
	    	if((distance < 200)&&(gAdapter.FsqVenues.size() != 0))
	    	{
	    		oPG.setVisibility(View.GONE);
	    		oV.setVisibility(View.VISIBLE);
				oV.setVisibility(View.VISIBLE);
	    		SetGridItems((GridView) findViewById(R.id.reviewsGrid));
	    		return;
	    	}
	    	else
	    	{
    	    	m_olocation = Singleton.getInstance().m_livelocation;
	    	      // Called when a new location is found by the network location provider.
	    	      makeUseOfNewLocation(Singleton.getInstance().m_livelocation);
	
	    	}
		}
		else
		{
			if(checkfornetwork())
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 6000, 200, locationListener);
			else if(checkforgps())
			{
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000, 200, locationListener);
				serviceHandler = new Handler();
	            serviceHandler.postDelayed( new timer(),60000L );
			}
		}
    	// Register the listener with the Location Manager to receive location updates
    	
    }
    private void stoplistening()
    {
    	if(locationManager != null)
    		locationManager.removeUpdates(locationListener);
    	
    }
    class timer implements Runnable {
        public void run() {
              stoplistening();
              if(m_olocation == null)
            	  ShowNoLocationInfo();
          }
  }
    private void makeUseOfNewLocation(Location location) {
		// TODO Auto-generated method stub
    	Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
    	StringBuilder builder = new StringBuilder();
    	
    	try {
    	    List<Address> address = geoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
    	    int maxLines = address.get(0).getMaxAddressLineIndex();
    	    localArea = null;
    	    for (int i=0; i<maxLines; i++) {
    	    String addressStr = address.get(0).getAddressLine(i);
    	    String[] locations = addressStr.split(",");
    	    if(localArea == null)
    	    	localArea = locations[locations.length - 1];
    	    builder.append(addressStr);
    	    builder.append(" ");
    	    }
    	    
    	} catch (IOException e) {
    		localArea = "Undetermined";
    	}
    	  catch (NullPointerException e) {
    		  localArea = "Undetermined";
    	  }
    	
		String url 	= "https://api.foursquare.com/v2/venues/explore?client_id=5MZNWHVUBAKSAYIOD3QZZ5X2IDLCGWKM5DV4P0UJ3PFLM5P2&client_secret=XSZAZ5XHDOEBBGJ331T4UNVGY5S2MHU0XJVEETV2SC5RWERC&v=20140305&section=drinks&ll="+location.getLatitude()+","+location.getLongitude()+"&sortByDistance=1&&venuePhotos=1&offset=0&limit=50";
		if(m_query != "")
			url = url + "&query="+m_query;

		TextView oText = (TextView) findViewById(R.id.locationText);
		oText.setText(localArea);
    	SmashedAsyncClient oAsyncClient = new SmashedAsyncClient();
    	oAsyncClient.Attach(this);
    	oAsyncClient.SetPersistantStorage(getApplicationContext());
    	oAsyncClient.MakeCallWithTag(url,"all");        	

	}

    private void displayView(int position) {
    	NavDrawerItem oItem = adapterlist.navDrawerItems.get(position);
    	if(position == 0)
    	{
    		mDrawerLayout.closeDrawers();
    		return;
    	}
    	else if(oItem == mFollowDrawerItem)
    	{
    		mDrawerLayout.closeDrawers();    		
            Intent intent = new Intent(getApplicationContext(), SmashedReview.class);
            Bundle b = new Bundle();
            b.putInt("position",0);
            b.putString("bid", "navdrawer");
            intent.putExtras(b);
            startActivity(intent);

    		return;
    	}
    	else if(oItem == mGroupFollowItem)
    	{
    		mDrawerLayout.closeDrawers();    		
            Intent intent = new Intent(getApplicationContext(), ListGroupsFriends.class);
            Bundle b = new Bundle();
            b.putInt("position",0);
            b.putString("bid", "groupdrawer");

            intent.putExtras(b);
            startActivity(intent);

    		return;
    	}

    	Intent intent = new Intent("my-event");
    	  // add data
    	  intent.putExtra("position", position);
    	  LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        //Intent intent = new Intent(this, MainActivity.class);
        //intent.putExtra("typeindex", position);
        //startActivity(intent);
        finish();
        return;
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
		getMenuInflater().inflate(R.menu.listrevmain, menu);
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.searchoverskel).getActionView();
        MenuItem oSearchMenu = menu.findItem(R.id.searchoverskel);
        oSearchMenu.collapseActionView();
        this.optionsMenu = menu;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
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
				if(m_bkupFsqVenues.size() > 0)
            	{
	        		gAdapter.FsqVenues.clear();
					gAdapter.FsqVenues.addAll(m_bkupFsqVenues);
	        		ProgressBar oP= (ProgressBar) findViewById(R.id.progressImageGrid);
	        		oP.setVisibility(View.GONE);
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
            	m_bkupFsqVenues.clear();
            	m_bkupFsqVenues.addAll(gAdapter.FsqVenues);
            	gAdapter.FsqVenues.clear();
            	SetGridItems((GridView) findViewById(R.id.reviewsGrid));
                // this is your adapter that will be filtered
            	String url 	= "https://api.foursquare.com/v2/venues/explore?client_id=5MZNWHVUBAKSAYIOD3QZZ5X2IDLCGWKM5DV4P0UJ3PFLM5P2&client_secret=XSZAZ5XHDOEBBGJ331T4UNVGY5S2MHU0XJVEETV2SC5RWERC&ll="+m_olocation.getLatitude()+","+m_olocation.getLongitude()+"&v=20140305&venuePhotos=1&offset=0&limit=50&query="+query;
            	SmashedAsyncClient oAsyncClient = new SmashedAsyncClient();
            	oAsyncClient.Attach(m_oRevActivity);
            	//oAsyncClient.SetPersistantStorage(getApplicationContext());
            	oAsyncClient.MakeCallWithTag(url,"search");
        		ProgressBar oP= (ProgressBar) findViewById(R.id.progressImageGrid);
        		oP.setVisibility(View.VISIBLE);

            	searchView.clearFocus();
    	        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getApplicationContext(),
    	                SampleRecentSuggestionsProvider.AUTHORITY, SampleRecentSuggestionsProvider.MODE);
    	        suggestions.saveRecentQuery(query, null);
    	       

                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);
       
			
			

		return true;
	}
		@Override
		public void onBackPressed() {	
			if(m_query == "")
				doExit();
			else if(mainStuff.equals("true"))
			{
				m_query = "";
				GetMyLocation();
				getActionBar().setTitle("Reviews");
			}
			else
				super.onBackPressed();
		}
		private void doExit() {
			if(m_bkupFsqVenues.size() > 0)
        	{
        		gAdapter.FsqVenues.clear();
				gAdapter.FsqVenues.addAll(m_bkupFsqVenues);
        		ProgressBar oP= (ProgressBar) findViewById(R.id.progressImageGrid);
        		oP.setVisibility(View.GONE);
        	}
			Singleton.getInstance().FsVenues.addAll(gAdapter.FsqVenues);
	        	Intent intent = new Intent("exit-event");
	      	  	LocalBroadcastManager.getInstance(m_oRevActivity).sendBroadcast(intent);

/*		    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		    alertDialog.setPositiveButton("Yes", new android.content.DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
				//	 finish();
					if(m_bkupFsqVenues.size() > 0)
	            	{
		        		gAdapter.FsqVenues.clear();
						gAdapter.FsqVenues.addAll(m_bkupFsqVenues);
		        		ProgressBar oP= (ProgressBar) findViewById(R.id.progressImageGrid);
		        		oP.setVisibility(View.GONE);
	            	}
					Singleton.getInstance().FsVenues.addAll(gAdapter.FsqVenues);
			        	Intent intent = new Intent("exit-event");
			      	  	LocalBroadcastManager.getInstance(m_oRevActivity).sendBroadcast(intent);
					
				}
			});

		    alertDialog.setNegativeButton("No", null);

		    alertDialog.setMessage("Do you want to exit?");
		    alertDialog.setTitle("SmashedIn");
		    alertDialog.show();*/
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
		venueList = new ArrayList<ReviewData>();//.getInstance().venueList;
		JSONObject jsonObj 	= (JSONObject) new JSONTokener(response).nextValue();
		
		JSONArray groups	= (JSONArray) jsonObj.getJSONObject("response").getJSONArray("groups");
		JSONObject group = (JSONObject) groups.get(0);
		JSONArray items = group.getJSONArray("items");
		
		int length			= items.length();
		
		if (length > 0) {
			for (int i = 0; i < length; i++) {
					JSONObject item = ((JSONObject) items.get(i)).getJSONObject("venue");
					venueList.add(ParseJsonObjectItem(item));
			}
		}
		gAdapter.FsqVenues.clear();
		gAdapter.FsqVenues = venueList;
		SetGridItems((GridView) findViewById(R.id.reviewsGrid));
	}
	private void ParseJsonBid(String response) throws JSONException
	{
		venueList = new ArrayList<ReviewData>();//.getInstance().venueList;
		JSONObject jsonObj 	= (JSONObject) new JSONTokener(response).nextValue();
		
		JSONObject groups	= (JSONObject) jsonObj.getJSONObject("response");
		JSONObject item = (JSONObject)groups.getJSONObject("venue");
		ReviewData oRevData = ParseJsonObjectItem(item);
		Singleton.getInstance().mRevData = oRevData;
        Intent intent = new Intent(getApplicationContext(), SmashedReview.class);
        Bundle b = new Bundle();
        b.putInt("position",0);
        b.putString("bid", oRevData.id);
        intent.putExtras(b);
        startActivity(intent);
        
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
		ProgressBar oPG= (ProgressBar) findViewById(R.id.progressImageGrid);
		oPG.setVisibility(View.GONE);

		gridView.setAdapter(gAdapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int location,
					long arg3) {
				ListReviewDataSingleton.getInstance().venueList = gAdapter.FsqVenues;
	            Intent intent = new Intent(getApplicationContext(), SmashedReview.class);
	            Bundle b = new Bundle();
	            b.putInt("position", location);
	            b.putString("bid", "");
	            intent.putExtras(b);
	            startActivity(intent);
	            StartAlarmGcm();
				
				
			}
		});
	}
	public void FromNotification(String type)
	{
        Intent intent = new Intent(getApplicationContext(), SmashedReview.class);
        Bundle b = new Bundle();
        b.putInt("position",0);
        b.putString("bid", type);
        intent.putExtras(b);
        startActivity(intent);
	}
	private void ParseJsonFriends(String response) throws JSONException
	{
		JSONObject jsonObj 	= (JSONObject) new JSONTokener(response).nextValue();
		JSONArray items = (JSONArray) jsonObj.getJSONArray("friends");		
		for (int i = 0; i < items.length(); i++) {
			JSONObject tagItem = (JSONObject)items.get(i);
			FacebookFriendsData oFriend = new FacebookFriendsData();
			oFriend.name = tagItem.getString("name");
			oFriend.avatar_url = tagItem.getString("avatar");
			MyGroupDataSingleton.getInstance().acceptGroupData.m_arrParticipants.add(oFriend);
		}

		String bid = jsonObj.getString("bid");
		String url 	= "https://api.foursquare.com/v2/venues/"+bid+"?client_id=5MZNWHVUBAKSAYIOD3QZZ5X2IDLCGWKM5DV4P0UJ3PFLM5P2&client_secret=XSZAZ5XHDOEBBGJ331T4UNVGY5S2MHU0XJVEETV2SC5RWERC&v=20140305&section=drinks&venuePhotos=1&offset=0&limit=50";
    	SmashedAsyncClient oAsyncClient = new SmashedAsyncClient();
    	oAsyncClient.Attach(this);
    	oAsyncClient.SetPersistantStorage(getApplicationContext());
    	oAsyncClient.MakeCallWithTag(url,"venue");        	

	}
	private void ParseJsonTags(String response) throws JSONException
	{
		JSONObject jsonObj 	= (JSONObject) new JSONTokener(response).nextValue();
		JSONArray items = (JSONArray) jsonObj.getJSONArray("tags");		
		for (int i = 0; i < items.length(); i++) {
			JSONObject tagItem = (JSONObject)items.get(i);
			Singleton.getInstance().m_arrTags.add(tagItem.getString("tag"));
		}

	}
	private void ParseJsonIndpendent(String response) throws JSONException
	{
		JSONObject jsonObj 	= (JSONObject) new JSONTokener(response).nextValue();		
		JSONObject groups	= (JSONObject) jsonObj.getJSONObject("response");
		JSONObject item = (JSONObject)groups.getJSONObject("venue");
		ReviewData oRevData = ParseJsonObjectItem(item);
		MyGroupDataSingleton.getInstance().acceptGroupData.mRevData = oRevData;
		MyGroupDataSingleton.getInstance().acceptGroupData.m_bMine = false;
		MyGroupDataSingleton.getInstance().m_arrPrivateGroups.add(MyGroupDataSingleton.getInstance().acceptGroupData);
		FillTheMyGroupData();
	}
	private void ParseJsonIndpendentData(String response) throws JSONException
	{
		JSONObject jsonObj 	= (JSONObject) new JSONTokener(response).nextValue();		
		JSONObject groups	= (JSONObject) jsonObj.getJSONObject("response");
		JSONObject item = (JSONObject)groups.getJSONObject("venue");
		ReviewData oRevData = ParseJsonObjectItem(item);
		MyGroupDataSingleton.getInstance().RefreshPlace(oRevData);
		FillTheMyGroupData();
		 if(bids.size() > 0)
	        {
	    		String url 	= "https://api.foursquare.com/v2/venues/"+bids.remove(bids.size() - 1)+"?client_id=5MZNWHVUBAKSAYIOD3QZZ5X2IDLCGWKM5DV4P0UJ3PFLM5P2&client_secret=XSZAZ5XHDOEBBGJ331T4UNVGY5S2MHU0XJVEETV2SC5RWERC&v=20140305&section=drinks&venuePhotos=1&offset=0&limit=50";
	        	SmashedAsyncClient oAsyncClient = new SmashedAsyncClient();
	        	oAsyncClient.Attach(this);
	        	oAsyncClient.SetPersistantStorage(getApplicationContext());
	        	oAsyncClient.MakeCallWithTag(url,"venuefill");        	
	        	
	        }
	}

	@Override
	public void OnResponse(String response,String tag,Object obj) {
	
		if(tag.equals("tagcloud"))
		{
			try {
				ParseJsonTags(response);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		if(tag.equals("accept"))
		{
			try {
				ParseJsonFriends(response);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return;
		}
		if(tag.equals("venue"))
		{
			try {
				ParseJsonIndpendent(response);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return;
		}
		if(tag.equals("venuefill"))
		{
			try {
				ParseJsonIndpendentData(response);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return;
		}
		try {
			ParseJson(response);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void OnFailure() {
		if(oPd != null)
			oPd.dismiss();
		ProgressBar oPG= (ProgressBar) findViewById(R.id.progressImageGrid);
		oPG.setVisibility(View.GONE);
		
	}
	
}
