package com.smashedin.reviews;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.smashedin.smashedin.R;
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
import com.smashedin.smashed.ResponseParser;
import com.smashedin.smashedin.MainActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public final class ReviewFragment extends Fragment implements OnResponseListener {
    private static final String KEY_CONTENT = "TestFragment:Content";
    private GridImageSkelAdapter gAdapter = null;
    private GridOverheardReviewAdapter gOhAdapter = null;
    private GridFsReviewsAdapter gReviewsAdapter = null;
    private PullToRefreshGridView oGrid;
    private PullToRefreshGridView ohGrid;
    private PullToRefreshListView reviewsGrid;
    private LatLng KIEL = new LatLng(53.551, 9.993);
    private String m_oType = "";
    private String m_StrResponse;
    private GoogleMap map;
    private Fragment m_curFragment;
    private View photosview;
    private View infoview;
    private View ohview;
    private View reviewview;
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        m_curFragment = this;
    	if(mRevData != null)
    	{
    		return GetView();
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
    
    private View GetView() {
		// TODO Auto-generated method stub
    	if(mContent == "INFO")
    	{
    		return GetViewForInfo();
    	}
    	else if(mContent == "PHOTOS")
    	{
    		return GetViewForPhotos();
    	}
    	else if(mContent == "OVERHEARDS")
    	{
    		return GetViewForOverheards();
    	}
    	else if(mContent == "REVIEWS")
    	{
    		return GetViewForReviews();
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
    private View GetViewForReviews()
    {
    	if(reviewview != null)
    		return reviewview;
    	reviewview = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_reviews,null,false);
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
    private void GetLatestReviews()
    {
    	m_oType = "fsreviews";
		mRevData.reviews = new ArrayList<SmashedFsReviewsData>();
		String url = "http://www.smashed.in/api/b/fscomments?fsbid="+mRevData.id;
		SmashedAsyncClient oAsyncClient = new SmashedAsyncClient();
    	oAsyncClient.Attach(mainFragment);
    	oAsyncClient.SetPersistantStorage(getActivity());
    	oAsyncClient.MakeCall(url);   

    }
    private View GetViewForOverheards()
    {
    	if(ohview != null)
    		return ohview;
        ohview = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_skelview,null,false);
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
		    	oAsyncClient.MakeCall(url);   

				
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
	    	oAsyncClient.MakeCall(url);   

		}
		return ohview;

    }
    
	private View GetViewForInfo() {

		if(infoview != null)
			return infoview;
        infoview = LayoutInflater.from(getActivity()).inflate(R.layout.infoview,null,false);
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
	private View GetViewForPhotos()
	{
		if(photosview != null)
			return photosview;
        photosview = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_skelview,null,false);
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
		oAsyncClient.MakeCall(url);        	
	}
	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, mContent);
    }
	public void UpdateRevData(ReviewData oRevData) {
        this.mRevData = oRevData;
		
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
	private void CreateOverheardForBar()
	{

		Intent	mainintent = new Intent(getActivity(), MainActivity.class);
		Intent intent = new Intent("my-event");
  	  // add data
  	  	intent.putExtra("position", 2);
  	  	intent.putExtra("bid", mRevData.id);
  	  	LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
		
        startActivity(mainintent);

	}

	@Override
	public void OnResponse(String response) {
		if(m_oType == "fs")
			oGrid.onRefreshComplete();
		if(m_oType == "oh")
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
		else if(m_oType == "fsreviews")
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
}
