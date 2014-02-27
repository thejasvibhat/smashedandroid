package com.example.smashed;
import java.util.ArrayList;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
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
import com.example.smashedin.*;


import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


public class OverHeardActivity extends FragmentActivity  implements OnHeadlineSelectedListener,OnResponseListener {
    private DrawerLayout mDrawerLayout;
    private FragmentActivity OverheardActivty;
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
        setContentView(R.layout.simple_tabs);
        OverheardActivty = this;
        ActionBar actionBar = getActionBar();
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
								if(galleryFragment == null)
								{
									galleryFragment = new GridOverheardFragment();
									((GridOverheardFragment) galleryFragment).AddFragment();
									((GridOverheardFragment) galleryFragment).SetUrl("http://www.smashed.in/api/oh/list?offset=0&limit=100");
								}
								android.app.FragmentManager fragmentManager = getFragmentManager();
								fragmentManager.beginTransaction()
										.replace(R.id.frame_container_oh, galleryFragment).addToBackStack( "main" ).commit();

								
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
							Login("overheard");
							return;
						}
						if(myUploadFragment == null)
						{
							myUploadFragment = new GridOverheardFragment();
							((GridOverheardFragment) myUploadFragment).AddFragment();
							((GridOverheardFragment) myUploadFragment).SetUrl("http://www.smashed.in/api/oh/list?mode=private&offset=0&limit=100");

						}
						android.app.FragmentManager fragmentManager = getFragmentManager();
						fragmentManager.beginTransaction()
								.replace(R.id.frame_container_oh, myUploadFragment).addToBackStack( "oh" ).commit();

						
					}
					
					@Override
					public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
						// TODO Auto-generated method stub
						
					}
				});
        actionBar.addTab(tab1);
        
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
		mDrawerList.setItemChecked(2, true);
		mDrawerList.setSelection(2);


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
		if(m_OhFragment == null)
        	m_OhFragment = new OverHeardFragment();

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
		  if (fragment instanceof OverHeardFragment) {
			   getFragmentManager().popBackStack();
			   getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			   Singleton.getInstance().m_bShareMenuItem = true;
			   Singleton.getInstance().m_bSearchMenuItem = false;
			   this.invalidateOptionsMenu();
		   }
		   else
		   {
			  // super.onBackPressed();
			   doExit();
		   }
		   
	}
	private void doExit() {

	    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

	    alertDialog.setPositiveButton("Yes", new OnClickListener() {

	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	        	
	            finish();
	        	Intent intent = new Intent("exit-event");
	      	  	LocalBroadcastManager.getInstance(OverheardActivty).sendBroadcast(intent);

	        }
	    });

	    alertDialog.setNegativeButton("No", null);

	    alertDialog.setMessage("Do you want to exit?");
	    alertDialog.setTitle("SmashedIn");
	    alertDialog.show();
	}
    private void displayView(int position) {
    	if(position == 2)
    	{
    		mDrawerLayout.closeDrawers();
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
	@Override
	public void OnResponse(String response) {
		if(oPd != null)
			oPd.dismiss();
		Singleton.getInstance().loggedIn = true;
		if(Singleton.getInstance().m_oType == "overheard")
		{
			if(myUploadFragment == null)
			{
				myUploadFragment = new GridOverheardFragment();
				((GridOverheardFragment) myUploadFragment).AddFragment();
				((GridOverheardFragment) myUploadFragment).SetUrl("http://www.smashed.in/api/oh/list?mode=private&offset=0&limit=100");

			}
			android.app.FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container_oh, myUploadFragment).addToBackStack( "oh" ).commit();

		}
		
	}
	
}
