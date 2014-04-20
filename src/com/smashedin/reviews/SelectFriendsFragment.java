package com.smashedin.reviews;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.smashedin.async.SmashedAsyncClient;
import com.smashedin.async.SmashedAsyncClient.OnResponseListener;
import com.smashedin.smashed.Singleton;
import com.smashedin.smashedin.R;
import com.loopj.android.http.RequestParams;
import com.loopj.android.image.SmartImageView;
import com.loopj.android.image.SmartImageTask.OnCompleteListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

public class SelectFriendsFragment extends android.support.v4.app.Fragment implements OnResponseListener {
	private ListView friendSelector;
	private ListView friendSelected;
	public SelectFriendsFragment(){}
	private FriendsPickerAdapter mFriendsAdapter;
	private FriendsPickerAdapter mFriendsPickedAdapter;
	// Search EditText
    EditText inputSearch;
    private ReviewData mRevData;
    ProgressDialog oPd;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.select_friends, container, false);

       
        getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        inputSearch = (EditText) rootView.findViewById(R.id.inputSearch);
        friendSelector = (ListView) rootView.findViewById(R.id.friendsList);
        friendSelected = (ListView) rootView.findViewById(R.id.friendsListSelected);
        setHasOptionsMenu(true);
        return rootView;
    }
	@Override
	public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
		menu.clear();
		getActivity().getMenuInflater().inflate(R.menu.selectfriends, menu);
	//	super.onCreateOptionsMenu(menu, inflater);
	}

	@Override 
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.savefriends:
	    	CreateMyPrivateGroup();
	    	return true;
	    default:
	    	return super.onOptionsItemSelected(item);
	    }
		
	}
	private void CreateMyPrivateGroup()
	{
		if(mFriendsPickedAdapter.mFriends.size() == 0)
			return;
    	oPd = new ProgressDialog(getActivity());
		oPd.setTitle("Creating your group...");
		oPd.setMessage("Please wait.");
		oPd.setIndeterminate(true);
		oPd.setCancelable(false);
		oPd.show();

		PrivateGroupData oData = new PrivateGroupData();
		oData.mRevData = mRevData;
		oData.m_bMine = true;
		oData.m_arrParticipants.addAll(mFriendsPickedAdapter.mFriends);
		MyGroupDataSingleton.getInstance().m_arrPrivateGroups.add(oData);
		CreateGroup(oData);
		
	}
	public void CreateGroup(PrivateGroupData oData)
	{
		try {
			JSONObject obj = new JSONObject();
			JSONArray arr = new JSONArray();
			for(FacebookFriendsData oFaceData:oData.m_arrParticipants)
			{
				JSONObject friend = new JSONObject();
				
					friend.put("userid", Long.parseLong(oFaceData.id));
					if(oFaceData.issmashed.equals("true"))
						friend.put("issmashed", "true");
					else
						friend.put("issmashed", "false");
				
				arr.put(friend);
			}
			obj.put("friends",arr);
			String data = obj.toString();
			SmashedAsyncClient oAsyncClient = new SmashedAsyncClient();
			oAsyncClient.Attach(this);
			oAsyncClient.SetPersistantStorage(getActivity());
			String url = "http://www.smashed.in/api/b/gcm/groupregister";
	    	RequestParams oParams = new RequestParams();
	    	oParams.put("users",data);
	    	oParams.put("bid", oData.mRevData.id);
	    	oParams.put("bname", oData.mRevData.name);
	    	oAsyncClient.MakePostCall(url,oParams);   
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	  @Override
	  public void onResume() {
	     super.onResume();
	     if(Singleton.getInstance().GetProvider().equals("facebook"))
	    	 GetMyFriends();
	     else
	     {
	    	 getActivity().findViewById(R.id.facebooklogin).setVisibility(ViewGroup.VISIBLE);
	     }
	     
	     inputSearch.addTextChangedListener(new TextWatcher() {
             
	            @Override
	            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
	                // When user changed the Text
	            	 mFriendsAdapter.getFilter().filter(cs.toString());
  
	            }
	             
	            @Override
	            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
	                    int arg3) {
	                // TODO Auto-generated method stub
	                 
	            }
	             
	            @Override
	            public void afterTextChanged(Editable arg0) {
	                // TODO Auto-generated method stub                         
	            }

				
	        });
	     
	     friendSelector.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				FacebookFriendsData oData = mFriendsAdapter.mFriends.get(arg2); 
				if(mFriendsAdapter.mFriends.get(arg2).selected == false)
				{					
					oData.selected = true;
					mFriendsAdapter.mFriends.set(arg2, oData);
					mFriendsAdapter.notifyDataSetChanged();
					mFriendsPickedAdapter.mFriends.add(oData);
					mFriendsPickedAdapter.notifyDataSetChanged();
				}
				else
				{
					oData.selected = false;
					mFriendsAdapter.mFriends.set(arg2, oData);
					mFriendsAdapter.notifyDataSetChanged();
					mFriendsPickedAdapter.mFriends.remove(oData);
					mFriendsPickedAdapter.notifyDataSetChanged();

				}
				
			}
		});
	  }	 
	  private void GetMyFriends()
	  {
		  if(mFriendsPickedAdapter == null)
			  mFriendsPickedAdapter = new FriendsPickerAdapter(getActivity());
		  if(mFriendsAdapter == null)
			  mFriendsAdapter = new FriendsPickerAdapter(getActivity());
			String url = "http://www.smashed.in/auth/facebook/getfriends?access_token="+Singleton.getInstance().getAccessToken();
			SmashedAsyncClient oAsyncClient = new SmashedAsyncClient();
	    	oAsyncClient.Attach(this);
	    	oAsyncClient.SetPersistantStorage(getActivity().getApplicationContext());
	    	oAsyncClient.MakeCallWithTag(url,"facebook");   
			ProgressBar oP= (ProgressBar) getActivity().findViewById(R.id.progressImagefriend);
			oP.setVisibility(View.VISIBLE);

	  }

	@Override
	public void OnResponse(String response, String tag, Object data) {
		// TODO Auto-generated method stub
		if(tag.equals("facebook"))
		{
			ProgressBar oP= (ProgressBar) getActivity().findViewById(R.id.progressImagefriend);
			oP.setVisibility(View.GONE);

			try {
				ParseJsonFriends(response);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			if(oPd != null)
				oPd.cancel();
			MyGroupDataSingleton.getInstance().m_arrPrivateGroups.get(MyGroupDataSingleton.getInstance().m_arrPrivateGroups.size() - 1).uniqueId = response;
			MyGroupDataSingleton.getInstance().StoreGroup(response);
			FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
			fragmentManager.popBackStack();
		}
	}

	private void ParseJsonFriends(String response) throws JSONException {
		JSONObject jsonObj 	= (JSONObject) new JSONTokener(response).nextValue();
		JSONArray items = (JSONArray) jsonObj.getJSONArray("friends");	
		for (int i = 0; i < items.length(); i++) {
			FacebookFriendsData oData = new FacebookFriendsData();
			
			JSONObject liveItem = (JSONObject)items.get(i);
			oData.name = liveItem.getString("name");
			oData.issmashed = liveItem.getString("issmashed");
			oData.id = liveItem.getString("id");
			try
			{
				oData.avatar_url = liveItem.getString("avatar");
			}
			catch(JSONException e) {
				oData.avatar_url = "";
			}
			mFriendsAdapter.mFriends.add(oData);

		}
		if(getActivity() == null)
			return;
		ProgressBar oP= (ProgressBar) getActivity().findViewById(R.id.progressImagefriend);
		oP.setVisibility(View.GONE);

		friendSelector.setVisibility(View.VISIBLE);
		friendSelector.setAdapter(mFriendsAdapter);
		friendSelected.setAdapter(mFriendsPickedAdapter);
	}

	@Override
	public void OnFailure() {
		// TODO Auto-generated method stub
		
	}
	public void AddData(ReviewData oRevData) {
		mRevData = oRevData;
		
	}
}
