package com.smashedin.reviews;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.smashedin.smashedin.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.smashedin.async.SmashedAsyncClient;
import com.smashedin.async.SmashedAsyncClient.OnResponseListener;
import com.smashedin.smashed.GridImageSkelAdapter;
import com.smashedin.smashed.OverHeardFragment;
import com.smashedin.smashed.ResponseParser;
import com.smashedin.smashed.Singleton;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public final class ReviewFragment extends Fragment implements OnResponseListener {
    private static final String KEY_CONTENT = "TestFragment:Content";
    private GridImageSkelAdapter gAdapter = null;
    private GridOverheardReviewAdapter gOhAdapter = null;
    private GridFsReviewsAdapter gReviewsAdapter = null;
    private LiveFeedAdapter gLiveFeedAdaper = null;
    private PullToRefreshGridView oGrid;
    private PullToRefreshGridView ohGrid;
    private PullToRefreshListView reviewsGrid;
    private ListView livefeedList;
    private LatLng KIEL = new LatLng(53.551, 9.993);
    private String m_oType = "";
    private String m_StrResponse;
    private GoogleMap map;
    private Fragment m_curFragment;
    private View photosview;
    private View infoview;
    private View ohview;
    private View reviewview;
    private View liveView;
    Fragment m_OhFragment;
    AtomicInteger msgId = new AtomicInteger();
    public static ReviewFragment newInstance(String content,ReviewData oRevData) {
        ReviewFragment fragment = new ReviewFragment();

        fragment.mContent = content;
        fragment.mRevData = oRevData;
        return fragment;
    }

    private String mContent = "???";
    private ReviewData mRevData = null;
    private Fragment mainFragment; 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainFragment = this;
        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
            mContent = savedInstanceState.getString(KEY_CONTENT);
        }
		  LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
			      new IntentFilter("bidoh"));
		  
		  setHasOptionsMenu(true);
    }
    @Override
    public void onResume()
    {
    	super.onResume();
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
    	super.onCreateOptionsMenu(menu, inflater);
    }
	@Override 
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	  
	    case R.id.followinstant:
	    		FollowThread();
	    		break;
	    case R.id.unfollowinstant:
    			UnFollowThread();
    			break;

	    }
		return super.onOptionsItemSelected(item);
	}
	public void FollowThread()
	{
		Singleton.getInstance().m_bUnFollowMenuItem = false;
		Singleton.getInstance().m_bCreateFollowMenuItem = true;
		getActivity().invalidateOptionsMenu();
		if(Singleton.getInstance().mRevData != null)
		{
			if(Singleton.getInstance().mRevData.m_bfollow == true)
			{
				Singleton.getInstance().SetUnFollowBid(Singleton.getInstance().mRevData.id);
				Singleton.getInstance().mRevData.m_bfollow = false;
			}
		}

		mRevData.m_bfollow = true;
		String id = mRevData.id;
		Singleton.getInstance().SetFollowBid(id);
		Singleton.getInstance().mRevData = mRevData;
        Toast.makeText(
                getActivity(),
                "Following "+mRevData.name,
                Toast.LENGTH_SHORT).show();

	}
	public void UnFollowThread()
	{
		Singleton.getInstance().m_bUnFollowMenuItem = true;
		Singleton.getInstance().m_bCreateFollowMenuItem = false;
		getActivity().invalidateOptionsMenu();
		Singleton.getInstance().mRevData.m_bfollow = false;
		mRevData.m_bfollow = false;
		Singleton.getInstance().SetUnFollowBid(mRevData.id);
        Toast.makeText(
                getActivity(),
                "Stopped following "+mRevData.name,
                Toast.LENGTH_SHORT).show();

	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        m_curFragment = this;
        getActivity().getActionBar().setTitle(mRevData.name);
    	if(mRevData != null)
    	{
    		return GetView(inflater);
    	}
    	else
    	{
	        TextView text = new TextView(getActivity());
	        text.setGravity(Gravity.CENTER);
	        text.setText(mContent);
	        text.setTextSize(20 * getResources().getDisplayMetrics().density);
	        text.setPadding(20, 20, 20, 20);
	
	        LinearLayout layout = new LinearLayout(getActivity());
	        layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	        layout.setGravity(Gravity.CENTER);
	        layout.addView(text);
	
	        return layout;
    	}
    }
    
    private View GetView(LayoutInflater inflater) {
		// TODO Auto-generated method stub
    	if(mContent == "INFO")
    	{
    		return GetViewForInfo(inflater);
    	}
    	else if(mContent == "PHOTOS")
    	{
    		return GetViewForPhotos(inflater);
    	}
    	else if(mContent == "OVERHEARDS")
    	{
    		return GetViewForOverheards(inflater);
    	}
    	else if(mContent == "REVIEWS")
    	{
    		return GetViewForReviews(inflater);
    	}
    	else if(mContent == "INSTANTS")
    	{
    		return GetViewForLiveFeed(inflater);
    	}
    	else
    	{
    		TextView text = new TextView(getActivity());
	        text.setGravity(Gravity.CENTER);
	        text.setText(mContent);
	        text.setTextSize(20 * getResources().getDisplayMetrics().density);
	        text.setPadding(20, 20, 20, 20);
	
	        LinearLayout layout = new LinearLayout(getActivity());
	        layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	        layout.setGravity(Gravity.CENTER);
	        layout.addView(text);
	
	        return layout;	
    	}
		
	}
    private View GetViewForReviews(LayoutInflater inflater)
    {
    	if(reviewview != null)
    		return reviewview;
    	reviewview = inflater.inflate(R.layout.fragment_reviews,null);
		reviewsGrid = (PullToRefreshListView) reviewview.findViewById(R.id.m_oReviewsList);
		if(gReviewsAdapter == null)
			gReviewsAdapter = new GridFsReviewsAdapter(getActivity());
		if(mRevData.reviews != null)
		{
			gReviewsAdapter.mReviews.addAll(mRevData.reviews);
			reviewsGrid.setAdapter(gReviewsAdapter);
		}
		else
		{
			GetLatestReviews();
		}
		reviewsGrid.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				GetLatestReviews();
			}
		});

		return reviewview;
    }
    private View GetViewForLiveFeed(LayoutInflater inflater)
    {
    	LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mGcmMessageReceiver,
  		      new IntentFilter("push-event"));
/*    	new AsyncTask<Void, Void, String>() {
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
                
            }
        }.execute(null, null, null);
*/    	
    	if(liveView != null)
    	{
    		if(Singleton.getInstance().mRevData != null)
    		{
    			if(Singleton.getInstance().mRevData.id.equals(mRevData.id) == true)
    			{
    				if(Singleton.getInstance().mRevData.livefeeds.size() > mRevData.livefeeds.size())
    				{
    					gLiveFeedAdaper.mLiveFeeds.clear();
    					gLiveFeedAdaper.mLiveFeeds.addAll(Singleton.getInstance().mRevData.livefeeds);
    					gLiveFeedAdaper.notifyDataSetChanged();
    				}
    			}
    		}
    		return liveView;
    	}
    	liveView = inflater.inflate(R.layout.fragment_livefeed,null);
		livefeedList =  (ListView) liveView.findViewById(R.id.m_oLiveList);
		
		if(gLiveFeedAdaper == null)
			gLiveFeedAdaper = new LiveFeedAdapter(getActivity());
		if(mRevData.livefeeds != null)
		{
			gLiveFeedAdaper.mLiveFeeds.addAll(mRevData.livefeeds);
			livefeedList.setAdapter(gLiveFeedAdaper);
			livefeedList.setSelection(gLiveFeedAdaper.mLiveFeeds.size() - 1);
		}
		else
		{
			if(Singleton.getInstance().loggedIn == true)
			{
				ProgressBar oP= (ProgressBar) liveView.findViewById(R.id.progressImagelive);
				oP.setVisibility(View.VISIBLE);
				
				livefeedList.setVisibility(View.GONE);
				GetLatestLiveFeed();
			}
			else
			{
				mRevData.livefeeds = new ArrayList<LiveData>();
				gLiveFeedAdaper.mLiveFeeds = new ArrayList<LiveData>();
				livefeedList.setAdapter(gLiveFeedAdaper);
			}
		}
		Button sendStatus = (Button)liveView.findViewById(R.id.sendStatus);
		sendStatus.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				EditText oStatusText = (EditText)liveView.findViewById(R.id.textStatus);
				String message = oStatusText.getText().toString(); 
				try {
					message = URLEncoder.encode(message,"utf-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				float distance = mRevData.location.m_location.distanceTo(Singleton.getInstance().m_livelocation);
				
				 oStatusText.setText("");
				 LiveData oLive = new LiveData();
				 oLive.mine = false;				
				 oLive.message = message;
				 oLive.username = Singleton.getInstance().username;
				 if(distance < 200)
				 {
					 oLive.atplace = "true";
				 }
				 oLive.mine = true;
				 mRevData.livefeeds.add(oLive);
				 gLiveFeedAdaper.mLiveFeeds.add(oLive);
				 gLiveFeedAdaper.notifyDataSetChanged();
				 livefeedList.setSelection(gLiveFeedAdaper.mLiveFeeds.size() - 1);
				 UpdateDataGCM(oLive);
			}
		});
		if(Singleton.getInstance().loggedIn == false)
		{
			FrameLayout oLayout = (FrameLayout) liveView.findViewById(R.id.livefeedparent);
			TextView oTextView = new TextView(getActivity());
			oTextView.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
			oTextView.setTextColor(0xffffffff);
			oTextView.setTextSize(32);
			oTextView.setShadowLayer(2,1,1,Color.BLACK);
			oTextView.setText("Please login to follow live feeds at this place. Click to login");
			oLayout.addView(oTextView);
			Button oBut = (Button) oLayout.findViewById(R.id.sendStatus);
			oBut.setEnabled(false);

			oTextView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					FrameLayout oLayout = (FrameLayout) getActivity().findViewById(R.id.livefeedparent);
					Button oBut = (Button) getActivity().findViewById(R.id.sendStatus);
					oBut.setEnabled(true);
					for(int i=0; i < oLayout.getChildCount(); i++)
					{
						if(oLayout.getChildAt(i) instanceof TextView)
						{
							oLayout.removeViewAt(i);
							break;
						}
					}

					Intent loginintent = new Intent("custom-login-event");
					LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(loginintent);

					
				}
			});


		}
		return liveView;
    }
    private void UpdateDataGCM(LiveData oLive)
    {
    	Singleton.getInstance().m_oType = "pushGet";
    	String url = "http://www.smashed.in/api/b/gcm?bid="+mRevData.id+"&bname="+mRevData.name+"&message="+oLive.message+"&regid="+Singleton.getInstance().regid+"&atplace="+oLive.atplace;
    	SmashedAsyncClient oAsyncClient = new SmashedAsyncClient();
    	oAsyncClient.Attach(this);
    	oAsyncClient.SetPersistantStorage(getActivity().getApplicationContext());
    	oAsyncClient.MakeCallWithTag(url,"pushGet"); 
    	
    }
    private void GetLatestLiveFeed() {
    	mRevData.livefeeds = new ArrayList<LiveData>();
    	Singleton.getInstance().m_oType = "push";
    	String url = "http://www.smashed.in/api/b/gcm/register?regid="+Singleton.getInstance().regid+"&bid="+mRevData.id;
    	SmashedAsyncClient oAsyncClient = new SmashedAsyncClient();
    	oAsyncClient.Attach(this);
    	oAsyncClient.SetPersistantStorage(getActivity().getApplicationContext());
    	oAsyncClient.MakeCallWithTag(url,"push"); 
		

	}

	private void GetLatestReviews()
    {
    	m_oType = "fsreviews";
		mRevData.reviews = new ArrayList<SmashedFsReviewsData>();
		String url = "http://www.smashed.in/api/b/fscomments?fsbid="+mRevData.id;
		SmashedAsyncClient oAsyncClient = new SmashedAsyncClient();
    	oAsyncClient.Attach(mainFragment);
    	oAsyncClient.SetPersistantStorage(getActivity());
    	oAsyncClient.MakeCallWithTag(url,"fsreviews");   

    }
    private View GetViewForOverheards(LayoutInflater inflater)
    {
    	if(ohview != null)
    		return ohview;
        ohview = inflater.inflate(R.layout.fragment_skelview,null);
		ohGrid = (PullToRefreshGridView) ohview.findViewById(R.id.grid_view_skels);
		ohGrid.setOnRefreshListener(new OnRefreshListener<GridView>() {

			@Override
			public void onRefresh(PullToRefreshBase<GridView> refreshView) {
				m_oType = "oh";
				mRevData.ohdata = new OverheardData();
				mRevData.ohdata.id = mRevData.id;
				String url = "http://www.smashed.in/api/b/fsoverheards?fsbid="+mRevData.ohdata.id;
				SmashedAsyncClient oAsyncClient = new SmashedAsyncClient();
		    	oAsyncClient.Attach(mainFragment);
		    	oAsyncClient.SetPersistantStorage(getActivity());
		    	oAsyncClient.MakeCallWithTag(url, "oh");   

				
			}
		});
		ohGrid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if(m_OhFragment == null)
					m_OhFragment = new FsOverHeardFragment();
				 Bundle bundle = new Bundle();
                 bundle.putString("url", mRevData.ohdata.ohUrl.get(arg2));
				 m_OhFragment.setArguments(bundle);
     			android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
     			fragmentManager.beginTransaction()
     					.replace(R.id.frame_container_each, m_OhFragment).addToBackStack( "new" ).commit();
				
			}
		});
		if(gOhAdapter == null)
			gOhAdapter = new GridOverheardReviewAdapter(getActivity());
		if((mRevData.ohdata != null)&&(mRevData.ohdata.ohUrl != null))
		{
			gOhAdapter.mThumbIds.addAll(mRevData.ohdata.ohUrl);
			ohGrid.setAdapter(gOhAdapter);
		}
		else
		{
			m_oType = "oh";
			mRevData.ohdata = new OverheardData();
			mRevData.ohdata.id = mRevData.id;
			String url = "http://www.smashed.in/api/b/fsoverheards?fsbid="+mRevData.ohdata.id;
			SmashedAsyncClient oAsyncClient = new SmashedAsyncClient();
	    	oAsyncClient.Attach(mainFragment);
	    	oAsyncClient.SetPersistantStorage(getActivity());
	    	oAsyncClient.MakeCallWithTag(url,"oh");   

		}
		return ohview;

    }
    
	private View GetViewForInfo(LayoutInflater inflater) {

		if(infoview != null)
			return infoview;
        infoview = inflater.inflate(R.layout.infoview,null);
        ((TextView)infoview.findViewById(R.id.revinfoname)).setText(mRevData.name);
        ((TextView)infoview.findViewById(R.id.revinfoaddress)).setText(mRevData.location.address);
        ((TextView)infoview.findViewById(R.id.revinfocitystate)).setText(mRevData.location.city+","+mRevData.location.state);
        ((TextView)infoview.findViewById(R.id.revinfodist)).setText(mRevData.location.distance);
        ((TextView)infoview.findViewById(R.id.revinfocontact)).setText(mRevData.contact);
        ((TextView)infoview.findViewById(R.id.revinfocats)).setText(mRevData.categories);
    	KIEL = new LatLng(Double.parseDouble(mRevData.location.lat), Double.parseDouble(mRevData.location.lng));
        map = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
	    map.addMarker(new MarkerOptions()
	        .position(KIEL)
	        .title(mRevData.name)
	        .snippet(mRevData.name)
	        .icon(BitmapDescriptorFactory
	            .fromResource(R.drawable.ic_action_location_place)));
	    map.moveCamera(CameraUpdateFactory.newLatLngZoom(KIEL, 15));
		return infoview;
	}
	private View GetViewForPhotos(LayoutInflater inflater)
	{
		if(photosview != null)
			return photosview;
        photosview = inflater.inflate(R.layout.fragment_skelview,null);
		if(gAdapter == null)
			gAdapter = new GridImageSkelAdapter(getActivity());
		GetPhotos(mRevData.id);
		oGrid = (PullToRefreshGridView) photosview.findViewById(R.id.grid_view_skels);
		if(mRevData.photos != null)
		{
			gAdapter.mThumbIds.addAll(mRevData.photos);
			oGrid.setAdapter(gAdapter);			
		}
		
		oGrid.setAdapter(gAdapter);
		oGrid.setOnRefreshListener(new OnRefreshListener<GridView>() {

			@Override
			public void onRefresh(PullToRefreshBase<GridView> refreshView) {
				GetPhotos(mRevData.id);
			}
		});
		return photosview;
		
	}
	private void GetPhotos(String id)
	{	
		m_oType = "fs";
		String url 	= "https://api.foursquare.com/v2/venues/"+id+"/photos?client_id=5MZNWHVUBAKSAYIOD3QZZ5X2IDLCGWKM5DV4P0UJ3PFLM5P2&client_secret=XSZAZ5XHDOEBBGJ331T4UNVGY5S2MHU0XJVEETV2SC5RWERC&v=20130815";
		SmashedAsyncClient oAsyncClient = new SmashedAsyncClient();
		oAsyncClient.Attach(mainFragment);
		oAsyncClient.MakeCallWithTag(url,"fs");        	
	}
	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, mContent);
    }
	public void UpdateRevData(ReviewData oRevData) {
        this.mRevData = oRevData;
		
	}
	private void ParseJsonLiveFeed(String response) throws JSONException
	{
		gLiveFeedAdaper.mLiveFeeds.clear();
		JSONObject jsonObj 	= (JSONObject) new JSONTokener(response).nextValue();
		JSONArray items = (JSONArray) jsonObj.getJSONArray("messages");	
		for (int i = 0; i < items.length(); i++) {
			LiveData oData = new LiveData();
			
			JSONObject liveItem = (JSONObject)items.get(i);
			oData.message = liveItem.getString("message");
			oData.username = liveItem.getString("username");
			oData.atplace = liveItem.getString("atplace");
			mRevData.livefeeds.add(oData);

		}
		gLiveFeedAdaper.mLiveFeeds.addAll(mRevData.livefeeds);
		livefeedList =  (ListView) liveView.findViewById(R.id.m_oLiveList);
		ProgressBar oP= (ProgressBar) liveView.findViewById(R.id.progressImagelive);
		oP.setVisibility(View.GONE);			
		livefeedList.setVisibility(View.VISIBLE);
		livefeedList.setAdapter(gLiveFeedAdaper);
		livefeedList.setSelection(gLiveFeedAdaper.mLiveFeeds.size() - 1);
	}
	private void ParseJsonReviews(String response) throws JSONException
	{
		gReviewsAdapter.mReviews.clear();
		JSONObject jsonObj 	= (JSONObject) new JSONTokener(response).nextValue();
		JSONArray items = (JSONArray) jsonObj.getJSONArray("reviews");		
		for (int i = 0; i < items.length(); i++) {
			SmashedFsReviewsData oRevs = new SmashedFsReviewsData();
			JSONObject reviewItem = (JSONObject)items.get(i);
			oRevs.rating = reviewItem.getString("rating");
			oRevs.username = reviewItem.getString("username");
			oRevs.review = reviewItem.getString("review");
			mRevData.reviews.add(oRevs);
		}
		gReviewsAdapter.mReviews.addAll(mRevData.reviews);
		reviewsGrid.setAdapter(gReviewsAdapter);
		if(mRevData.reviews.size() == 0)
		{
			FrameLayout oLayout = (FrameLayout) getActivity().findViewById(R.id.reviewparent);
			TextView oTextView = new TextView(getActivity());
			oTextView.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
			oTextView.setTextColor(0xffffffff);
			oTextView.setTextSize(32);
			oTextView.setShadowLayer(2,1,1,Color.BLACK);
			oTextView.setText("No user reviews found. Click to add one");
			oLayout.addView(oTextView);
			oTextView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					FrameLayout oLayout = (FrameLayout) getActivity().findViewById(R.id.reviewparent);
					for(int i=0; i < oLayout.getChildCount(); i++)
					{
						if(oLayout.getChildAt(i) instanceof TextView)
						{
							TextView oText = (TextView) oLayout.getChildAt(i);
							oText.setText("Thank you for reviewing. I am sure it will help");
							//oLayout.removeViewAt(i);
							break;
						}
					}

					CreateReviewForBar();
					
				}
			});

		}
		else
		{
			FrameLayout oLayout = (FrameLayout) getActivity().findViewById(R.id.reviewparent);
			for(int i=0; i < oLayout.getChildCount(); i++)
			{
				if(oLayout.getChildAt(i) instanceof TextView)
				{
					oLayout.removeViewAt(i);
					return;
				}
			}
		}
	}

	private void ParseJson(String response) throws JSONException
	{
		mRevData.photos = new ArrayList<String>();
		JSONObject jsonObj 	= (JSONObject) new JSONTokener(response).nextValue();
		
		JSONArray items = (JSONArray) jsonObj.getJSONObject("response").getJSONObject("photos").getJSONArray("items");		
		int length			= items.length();
		
		if (length > 0) {
			for (int i = 0; i < length; i++) {
				JSONObject pata = (JSONObject)items.get(i);
				String photo = pata.getString("prefix")+"200x200"+pata.getString("suffix");

				mRevData.photos.add(photo);
			}
		}
		gAdapter.mThumbIds.clear();
		gAdapter.mThumbIds.addAll(mRevData.photos);
		oGrid.setAdapter(gAdapter);
	}
	public void ReturnResponseDocumentWithKey(Document n_oDocument,String type)
	{
		if(type == "oh")
		{
			NodeList skelThumbs = n_oDocument.getElementsByTagName("icon");
			if(gOhAdapter == null)
				gOhAdapter = new GridOverheardReviewAdapter(getActivity());
			for(int i=0 ; i < skelThumbs.getLength(); i++)
			{
				Node thumburl = skelThumbs.item(i);
				String iconUrl = "http://www.smashed.in"+thumburl.getTextContent();
				mRevData.ohdata.ohUrl.add(iconUrl);
			}
			gOhAdapter.mThumbIds.clear();
			gOhAdapter.mThumbIds.addAll(mRevData.ohdata.ohUrl);
			ohGrid.setAdapter(gOhAdapter);
			if(skelThumbs.getLength() == 0)
			{
				FrameLayout oLayout = (FrameLayout) getActivity().findViewById(R.id.gridparent);
				TextView oTextView = new TextView(getActivity());
				oTextView.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
				oTextView.setTextColor(0xffffffff);
				oTextView.setTextSize(32);
				oTextView.setShadowLayer(2,1,1,Color.BLACK);
				oTextView.setText("No Overheards found. Click to Create one");
				oLayout.addView(oTextView);
				oTextView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						FrameLayout oLayout = (FrameLayout) getActivity().findViewById(R.id.gridparent);
						for(int i=0; i < oLayout.getChildCount(); i++)
						{
							if(oLayout.getChildAt(i) instanceof TextView)
							{
								oLayout.removeViewAt(i);
								break;
							}
						}

						CreateOverheardForBar();
						
					}
				});

			}
			else
			{
				FrameLayout oLayout = (FrameLayout) getActivity().findViewById(R.id.gridparent);
				for(int i=0; i < oLayout.getChildCount(); i++)
				{
					if(oLayout.getChildAt(i) instanceof TextView)
					{
						oLayout.removeViewAt(i);
						return;
					}
				}
			}
		}
		


	}
	private void CreateReviewForBar()
	{
		Intent intent = new Intent("review-event");
		  // add data
		intent.putExtra("type", "review");
		  intent.putExtra("bid", mRevData.id);
		LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
		
	}
	private void CreateOverheardForBar()
	{
		Intent intent = new Intent("review-event");
		  // add data
		intent.putExtra("type", "oh");
		  intent.putExtra("bid", mRevData.id);
		LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);

	}

	@Override
	public void OnResponse(String response,String tag) {
		if(tag == "push")
		{
			Singleton.getInstance().m_oType = "";
			if(liveView == null)
				return;
			
			try {
				ParseJsonLiveFeed(response);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		if(tag == "pushGet")
		{
			Singleton.getInstance().m_oType = "";
			return;
		}

		if(tag == "fs")
			oGrid.onRefreshComplete();
		if(tag == "oh")
		{
			ohGrid.onRefreshComplete();
			m_StrResponse = response;

	        // Create Inner Thread Class
	        Thread background = new Thread(new Runnable() {
	             
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
	                    	oParser.SetFragmentWithKey(m_curFragment,m_oType);
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
		else if(tag == "fsreviews")
		{
			reviewsGrid.onRefreshComplete();
			try {
				ParseJsonReviews(response);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			try {
				ParseJson(response);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		
	}
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		  @Override
		  public void onReceive(Context context, Intent intent) {
		    // Extract data included in the Intent
			  if(intent.getAction() == "bidoh")
			  {
				  String url = intent.getStringExtra("iconurl");
				  mRevData.ohdata.ohUrl.add(url);
				  if(gOhAdapter != null)
				  {					  
					  gOhAdapter.mThumbIds.add(url);
					  ohGrid.setAdapter(gOhAdapter);
				  }

			  }		   
		  }

	};
	@Override
	public void OnFailure() {
		if(m_oType == "fs")
			oGrid.onRefreshComplete();
		if(m_oType == "oh")
		{
			ohGrid.onRefreshComplete();
		}
		
	}

	public void RefreshReviews(SmashedFsReviewsData oRev) {
		mRevData.reviews.add(oRev);
		gReviewsAdapter.mReviews.add(oRev);
		reviewsGrid.setAdapter(gReviewsAdapter);
		FrameLayout oLayout = (FrameLayout) getActivity().findViewById(R.id.reviewparent);
		for(int i=0; i < oLayout.getChildCount(); i++)
		{
			if(oLayout.getChildAt(i) instanceof TextView)
			{
				oLayout.removeViewAt(i);
				break;
			}
		}

		
		
	}
	private BroadcastReceiver mGcmMessageReceiver = new BroadcastReceiver() {
		  @Override
		  public void onReceive(Context context, Intent intent) {
		    // Extract data included in the Intent
				 if(Singleton.getInstance().m_bAppHidden == false)
				 {
					 String message = Singleton.getInstance().m_strMessageGcm;
					 LiveData oLive = new LiveData();
					 oLive.mine = false;
					 try {
							message = URLEncoder.encode(message,"utf-8");
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					 oLive.message = message;
					 oLive.username = Singleton.getInstance().m_strMessageGcmUser;
					 gLiveFeedAdaper.mLiveFeeds.add(oLive);
					 if(Singleton.getInstance().m_strMessageGcmBid.equals(mRevData.id) == true)
					 {
						 mRevData.livefeeds.add(oLive);
						 gLiveFeedAdaper.notifyDataSetChanged();
						 livefeedList.setSelection(gLiveFeedAdaper.mLiveFeeds.size() - 1);
					 }
				 }
		}
				 
	};

}
