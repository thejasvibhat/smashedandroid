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

import com.smashedin.smashedin.MainActivity;
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
import android.app.NotificationManager;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public final class MyGroupInstantFragment extends Fragment implements OnResponseListener{
    private static final String KEY_CONTENT = "TestFragment:Content";
    private GridImageSkelAdapter gAdapter = null;
    private GridOverheardReviewAdapter gOhAdapter = null;
    private GridFsReviewsAdapter gReviewsAdapter = null;
    private LiveFeedAdapter gLiveFeedAdaper = null;
    private PullToRefreshGridView oGrid;
    private PullToRefreshGridView ohGrid;
    private PullToRefreshListView reviewsGrid;
    private ListView livefeedList;
    private String m_oType = "";
    private String m_StrResponse;
    private Fragment m_curFragment;
    private View liveView;
    private String groupType = "mine";
    Fragment m_OhFragment;
    AtomicInteger msgId = new AtomicInteger();
    public static MyGroupInstantFragment newInstance(String content,PrivateGroupData oMineGroupData,PrivateGroupData oFriendsGroupData) {
        MyGroupInstantFragment fragment = new MyGroupInstantFragment();

        fragment.mContent = content;

        fragment.oMineGroupData = oMineGroupData;
        fragment.oFriendsGroupData = oFriendsGroupData;
        return fragment;
    }

    private String mContent = "???";
    private PrivateGroupData oMineGroupData = null;
    private PrivateGroupData oFriendsGroupData = null;
    private Fragment mainFragment;
	private MyGroupsFragment myGroupsFragment; 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainFragment = this;
        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
            mContent = savedInstanceState.getString(KEY_CONTENT);
        }
		  setHasOptionsMenu(true);
    }
    public void RefreshView()
    {
    	View view = getView();
    }
    @Override
    public void onResume()
    {
    	super.onResume();
   		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mGcmMessageReceiver);
    	LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mGcmMessageReceiver,
    		      new IntentFilter("push-group-event"));
    }
    public void CheckGroupStatus()
    {
    	if(oMineGroupData != null && MyGroupDataSingleton.getInstance().GetMyPrivateGroup(oMineGroupData.mRevData) == null)
    	{
    		if(groupType.equals("mine"))
    		{
    			Button sendStatus = (Button)liveView.findViewById(R.id.sendStatus);
    			sendStatus.setEnabled(false);
    		}
    	}
    	if(oFriendsGroupData != null && MyGroupDataSingleton.getInstance().GetFriendsPrivateGroup(oFriendsGroupData.mRevData) == null)
    	{
    		if(groupType.equals("friends"))
    		{
    			Button sendStatus = (Button)liveView.findViewById(R.id.sendStatus);
    			sendStatus.setEnabled(false);
    		}
    		
    	}
    }
    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
    	super.onCreateOptionsMenu(menu, inflater);
    }
	@Override 
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	  
	    }
		return super.onOptionsItemSelected(item);
	}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        m_curFragment = this;
        PrivateGroupData oGroupData;
        if(oMineGroupData != null)
        {
        	oGroupData = oMineGroupData;
        	getActivity().getActionBar().setTitle(oMineGroupData.mRevData.name);
        }
        else
        {
        	oGroupData = oFriendsGroupData;
        	getActivity().getActionBar().setTitle(oFriendsGroupData.mRevData.name);
        }
    	if(oGroupData.mRevData != null)
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
    	if(mContent == "MINE")
    	{
    		groupType = "mine";
    		return GetViewForLiveFeed(inflater);
    	}
    	else
    	{
    		groupType = "friends";
    		return GetViewForLiveFeed(inflater);
    	}
		
	}
    private View GetViewForLiveFeed(LayoutInflater inflater)
    {
    	ReviewData oLocalRevData = null;
    	if(groupType.equals("mine"))
    	{
    		if(oMineGroupData != null)
    		{
	    		oLocalRevData = oMineGroupData.mRevData;
	    		//oLocalRevData.grouplivefeeds = new ArrayList<LiveData>();
	    		oLocalRevData.grouplivefeedsmine.addAll(oMineGroupData.m_arrInstantQueueMessages);
	    		oMineGroupData.m_arrInstantQueueMessages.clear();
    		}
    	}
    	else
    	{
    		if(oFriendsGroupData != null)
    		{
	    		oLocalRevData = oFriendsGroupData.mRevData;
	    		//oLocalRevData.grouplivefeeds = new ArrayList<LiveData>();
	    		oLocalRevData.grouplivefeedsfriends.addAll(oFriendsGroupData.m_arrInstantQueueMessages);
	    		oFriendsGroupData.m_arrInstantQueueMessages.clear();
    		}
    	}

    	if(liveView != null)
    	{
			gLiveFeedAdaper.mLiveFeeds.clear();
			if(oLocalRevData != null)
			{
				if(groupType.equals("mine"))
					gLiveFeedAdaper.mLiveFeeds.addAll(oLocalRevData.grouplivefeedsmine);
				else
					gLiveFeedAdaper.mLiveFeeds.addAll(oLocalRevData.grouplivefeedsfriends);
			}
			gLiveFeedAdaper.notifyDataSetChanged();
			livefeedList.setSelection(1000);
			livefeedList.invalidate();
    		return liveView;
    	}
    	liveView = inflater.inflate(R.layout.fragment_livefeed_group,null);
		livefeedList =  (ListView) liveView.findViewById(R.id.m_oLiveList);
		
		if(gLiveFeedAdaper == null)
			gLiveFeedAdaper = new LiveFeedAdapter(getActivity());
		Button sendStatus = (Button)liveView.findViewById(R.id.sendStatus);
		if(oLocalRevData == null)
		{			
			sendStatus.setEnabled(false);
		}
		else if(oLocalRevData.grouplivefeedsmine != null)
		{
			sendStatus.setEnabled(true);
			if(groupType.equals("mine"))
				gLiveFeedAdaper.mLiveFeeds.addAll(oLocalRevData.grouplivefeedsmine);
			else
				gLiveFeedAdaper.mLiveFeeds.addAll(oLocalRevData.grouplivefeedsfriends);
			livefeedList.setAdapter(gLiveFeedAdaper);
			livefeedList.setSelection(1000);
			livefeedList.invalidate();
		}
		else
		{
			sendStatus = (Button)liveView.findViewById(R.id.sendStatus);
			gLiveFeedAdaper.mLiveFeeds = new ArrayList<LiveData>();
			livefeedList.setAdapter(gLiveFeedAdaper);
		}
		if(groupType.equals("mine"))
		{
			sendStatus = (Button)liveView.findViewById(R.id.sendStatus);
			sendStatus.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					EditText oStatusText = (EditText)liveView.findViewById(R.id.textStatus);
					String message = oStatusText.getText().toString(); 
					SendMessage(message,"mine");
					 oStatusText.setText("");
	
				}
			});
		}
		else
		{
			sendStatus = (Button)liveView.findViewById(R.id.sendStatus);
			sendStatus.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					EditText oStatusText = (EditText)liveView.findViewById(R.id.textStatus);
					String message = oStatusText.getText().toString(); 
					SendMessage(message,"friends");
					 oStatusText.setText("");
	
				}
			});		
		}

		return liveView;
    }

    public void SendMessage(String message,String type)
    {
    	ReviewData mRevData = null;
    	if(type.equals("mine"))
    		mRevData = oMineGroupData.mRevData;
    	else
    		mRevData = oFriendsGroupData.mRevData;
    	message = message.trim();
		if(message.equals(""))
			return;
		boolean meme = CheckForOhShortcut(message); 
			if(meme == false)
			{
				try {
					message = URLEncoder.encode(message,"utf-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		float distance = mRevData.location.m_location.distanceTo(Singleton.getInstance().m_livelocation);
		

		 LiveData oLive = new LiveData();				
		 oLive.message = message;
		 oLive.username = Singleton.getInstance().username;
		 if(distance < 200)
		 {
			 oLive.atplace = "true";
		 }
		 oLive.mine = true;
		 Long tsLong = System.currentTimeMillis()/1000;
		 oLive.timestamp = tsLong;
		 oLive.updating = true; 
		 if(meme == true)
			 oLive.type = "image";
		 gLiveFeedAdaper.mLiveFeeds.add(oLive);
		 gLiveFeedAdaper.notifyDataSetChanged();
		 livefeedList.setSelection(1000);
		 livefeedList.invalidate();
		 gLiveFeedAdaper.mLiveFeeds.set(gLiveFeedAdaper.mLiveFeeds.size() - 1,oLive);
		 if(type.equals("mine"))
			 mRevData.grouplivefeedsmine.add(oLive);
		 else
			 mRevData.grouplivefeedsfriends.add(oLive);
		 UpdateDataGCM(oLive,type);
    }
    private boolean CheckForOhShortcut(String message)
    {
    	boolean meme = false;
    	String[] msgs = message.split(" ");
    	if(msgs.length == 3)
    	{
    		for(String msg:msgs)
    		{
    			if(msg.contains("#") == true)
    			{
    				meme = true;
    			}
    			else
    				meme = false;
    		}
    	}
    	return meme;
    }
    private String geturlforinstantoh(LiveData oLive)
    {
    	String url = "http://www.smashed.in/api/oh/getohinstant";
    	url += "?tag="+oLive.tag;
    	url += "&toptext="+oLive.toptext;
    	url += "&bottomtext="+oLive.bottomtext;
    	return url;
    }
    private void UpdateDataGCM(LiveData oLive,String type)
    {
    	PrivateGroupData oGroupData;
    	if(type.equals("mine"))
    		oGroupData = oMineGroupData;
    	else
    		oGroupData = oFriendsGroupData;
    	Singleton.getInstance().m_oType = "pushGet";    	
    	String url = "http://www.smashed.in/api/b/gcm/group?uniqueid="+oGroupData.uniqueId+"&bname="+oGroupData.mRevData.name+"&message="+oLive.message+"&atplace="+oLive.atplace;
    	if(oLive.type.equals("image"))
    	{
    		
    		url = geturlforinstantoh(oLive);
    		url = url + "&bid="+oGroupData.mRevData.id+"&bname="+oGroupData.mRevData.name+"&regid="+Singleton.getInstance().regid+"&atplace="+oLive.atplace;
    	}
    	SmashedAsyncClient oAsyncClient = new SmashedAsyncClient();
    	oAsyncClient.Attach(this);
    	oAsyncClient.SetPersistantStorage(getActivity().getApplicationContext());
    	oAsyncClient.MakeCallWithTagAndData(url,"pushGet",oLive); 
    	
    }
	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, mContent);
    }
	private BroadcastReceiver mGcmMessageReceiver = new BroadcastReceiver() {
		  @Override
		  public void onReceive(Context context, Intent intent) {
		    // Extract data included in the Intent
				 if(Singleton.getInstance().m_bAppHidden == false)
				 {
					 for(PrivateGroupData oGroupData:MyGroupDataSingleton.getInstance().m_arrPrivateGroups)
					 {
						 if(oGroupData.uniqueId.equals(Singleton.getInstance().uniqueid))
						 {
							 oGroupData.m_arrInstantQueueMessages.clear();
							 NotificationManager mNotificationManager = (NotificationManager)
						                getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
							 mNotificationManager.cancel(MainActivity.NOTIFICATION_ID_GROUP);
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
							 oLive.atplace = Singleton.getInstance().m_strMessageGcmLocation;
							 oLive.timestamp = Singleton.getInstance().m_iMessageGcmTimestamp;
							 oLive.ohurl = Singleton.getInstance().m_strOhUrl;
							 oLive.type = Singleton.getInstance().m_strMessageType;
							 if(oGroupData.m_bMine)
								 oGroupData.mRevData.grouplivefeedsmine.add(oLive);
							 else
								 oGroupData.mRevData.grouplivefeedsfriends.add(oLive);
							 gLiveFeedAdaper.mLiveFeeds.add(oLive);
							 gLiveFeedAdaper.notifyDataSetChanged();
							 break;
						 }
					 }
				 }
		}
				 
	};
    @Override
    public void onDestroy()
    {
    	LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mGcmMessageReceiver);
    	super.onDestroy();
    }
    @Override
    public void onPause()
    {
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mGcmMessageReceiver);
    	super.onPause();
    }
	@Override
	public void OnResponse(String response, String tag, Object data) {
		if(tag == "pushGet") 
		{
			Singleton.getInstance().m_oType = "";
			
			LiveData oLive = (LiveData)data;
			int index = -1 ;
			if(oMineGroupData != null)
			{
				
				index = oMineGroupData.mRevData.grouplivefeedsmine.indexOf(oLive);
			}
			if(index < 0)
			{
				index = oFriendsGroupData.mRevData.grouplivefeedsfriends.indexOf(oLive);
				oLive.updating = false;
				oLive.ohurl = response;
				oFriendsGroupData.mRevData.grouplivefeedsfriends.set(index,oLive);
			}
			else
			{
				oLive.updating = false;
				oLive.ohurl = response;
				oMineGroupData.mRevData.grouplivefeedsmine.set(index,oLive);
			}
			gLiveFeedAdaper.notifyDataSetChanged();
			livefeedList.setSelection(1000);
			livefeedList.invalidate();
			return;
		}

	}
	@Override
	public void OnFailure() {
		// TODO Auto-generated method stub
		
	}

}
