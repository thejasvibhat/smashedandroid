package com.example.reviews;

import java.util.ArrayList;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.example.async.SmashedAsyncClient;
import com.example.async.SmashedAsyncClient.OnResponseListener;
import com.example.smashed.GridImageSkelAdapter;
import com.example.smashed.ResponseParser;
import com.example.smashed.Singleton;
import com.example.smashedin.R;
import com.google.android.gms.internal.gh;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.RequestParams;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public final class ReviewFragment extends Fragment implements OnResponseListener {
    private static final String KEY_CONTENT = "TestFragment:Content";
    private GridImageSkelAdapter gAdapter = null;
    private GridOverheardReviewAdapter gOhAdapter = null;
    private GridAdapter catAdapter = null;
    private GridView oGrid;
    private GridView ohGrid;
    private LatLng HAMBURG = new LatLng(53.558, 9.927);
    private LatLng KIEL = new LatLng(53.551, 9.993);
    private String m_oType = "";
    private String m_StrResponse;
    private GoogleMap map;
    private Fragment m_curFragment;
    private View mapview;
    private View photosview;
    private View infoview;
    private View ohview;
    public static ReviewFragment newInstance(String content,ReviewData oRevData) {
        ReviewFragment fragment = new ReviewFragment();

        fragment.mContent = content;
        fragment.mRevData = oRevData;
        return fragment;
    }

    private String mContent = "???";
    private ReviewData mRevData = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
	        layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
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
    	else if(mContent == "MAP")
    	{
    		return GetViewForMap();
    	}
    	else if(mContent == "OVERHEARDS")
    	{
    		return GetViewForOverheards();
    	}
    	else
    	{
    		TextView text = new TextView(getActivity());
	        text.setGravity(Gravity.CENTER);
	        text.setText(mContent);
	        text.setTextSize(20 * getResources().getDisplayMetrics().density);
	        text.setPadding(20, 20, 20, 20);
	
	        LinearLayout layout = new LinearLayout(getActivity());
	        layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	        layout.setGravity(Gravity.CENTER);
	        layout.addView(text);
	
	        return layout;	
    	}
		
	}
    private View GetViewForOverheards()
    {
    	if(ohview != null)
    		return ohview;
        ohview = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_skelview,null,false);
		ohGrid = (GridView) ohview.findViewById(R.id.grid_view_skels);
		if(gOhAdapter == null)
			gOhAdapter = new GridOverheardReviewAdapter(getActivity());
		if(mRevData.ohdata != null)
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
	    	oAsyncClient.Attach(this);
	    	oAsyncClient.SetPersistantStorage(getActivity());
	    	oAsyncClient.MakeCall(url);   

		}
		return ohview;

    }
    
    private View GetViewForMap()
    {
    	if(mapview != null)
    		return mapview;
    	KIEL = new LatLng(Double.parseDouble(mRevData.location.lat), Double.parseDouble(mRevData.location.lng));
    	mapview = LayoutInflater.from(getActivity()).inflate(R.layout.mapview,null,false);
    	 map = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
//    		    Marker hamburg = map.addMarker(new MarkerOptions().position(HAMBURG)
  //  		        .title("Hamburg"));
    		    Marker kiel = map.addMarker(new MarkerOptions()
    		        .position(KIEL)
    		        .title("Kiel")
    		        .snippet("Kiel is cool")
    		        .icon(BitmapDescriptorFactory
    		            .fromResource(R.drawable.ic_action_location_place)));

    		    // Move the camera instantly to hamburg with a zoom of 15.
    		//    map.moveCamera(CameraUpdateFactory.newLatLngZoom(HAMBURG, 15));

    		    // Zoom in, animating the camera.
    		 //   map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
    	return mapview;
    }
	private View GetViewForInfo() {
		// TODO Auto-generated method stub
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
//	    Marker hamburg = map.addMarker(new MarkerOptions().position(HAMBURG)
//  		        .title("Hamburg"));
	    Marker kiel = map.addMarker(new MarkerOptions()
	        .position(KIEL)
	        .title("Kiel")
	        .snippet("Kiel is cool")
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
		oGrid = (GridView) photosview.findViewById(R.id.grid_view_skels);
		if(mRevData.photos != null)
		{
			gAdapter.mThumbIds.addAll(mRevData.photos);
			oGrid.setAdapter(gAdapter);			
		}
		
		oGrid.setAdapter(gAdapter);
		return photosview;
		
	}
	private void GetPhotos(String id)
	{	
		String url 	= "https://api.foursquare.com/v2/venues/"+id+"/photos?client_id=5MZNWHVUBAKSAYIOD3QZZ5X2IDLCGWKM5DV4P0UJ3PFLM5P2&client_secret=XSZAZ5XHDOEBBGJ331T4UNVGY5S2MHU0XJVEETV2SC5RWERC&v=20130815";
		SmashedAsyncClient oAsyncClient = new SmashedAsyncClient();
		oAsyncClient.Attach(this);
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
		gAdapter.mThumbIds.addAll(mRevData.photos);
		oGrid.setAdapter(gAdapter);
	}
	public void ReturnResponseDocument(Document n_oDocument)
	{
		NodeList skelThumbs = n_oDocument.getElementsByTagName("icon");
		NodeList skelIds = n_oDocument.getElementsByTagName("url");
//		NodeList downloadUrls = n_oDocument.getElementsByTagName("downloadurl");
		//NodeList ohCreatorname = n_oDocument.getElementsByTagName("id");
		if(gOhAdapter == null)
			gOhAdapter = new GridOverheardReviewAdapter(getActivity());
		for(int i=0 ; i < skelThumbs.getLength(); i++)
		{
			Node thumburl = skelThumbs.item(i);
			String iconUrl = "http://www.smashed.in"+thumburl.getTextContent();
			
			gOhAdapter.mThumbIds.add(iconUrl);
			Node id = skelIds.item(i);
		}
		ohGrid.setAdapter(gOhAdapter);


	}

	@Override
	public void OnResponse(String response) {
		if(m_oType == "oh")
		{
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
}
