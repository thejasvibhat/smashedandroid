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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ShowParticipantsFragment extends android.support.v4.app.Fragment implements OnResponseListener {
	private ListView friendsList;
	public ShowParticipantsFragment(){}
	private FriendsPickerAdapter mFriendsAdapter;
	private PrivateGroupData mGroupData;
	private Button deleteexit;
	private Button resendrequests;
	private ShowParticipantsFragment oParent;
	ProgressDialog oPd;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.view_friends, container, false);
        oParent = this;
       
        getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        friendsList = (ListView) rootView.findViewById(R.id.friendsList);
        deleteexit = (Button) rootView.findViewById(R.id.deletexit);
        resendrequests = (Button) rootView.findViewById(R.id.resendrequest);
        deleteexit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String url = "http://www.smashed.in/api/b/gcm/groupexit?uniqueid="+mGroupData.uniqueId;
				if(mGroupData.m_bMine == true)
				{
					url = "http://www.smashed.in/api/b/gcm/groupdelete?uniqueid="+mGroupData.uniqueId;
				}
				SmashedAsyncClient oAsyncClient = new SmashedAsyncClient();
		    	oAsyncClient.Attach(oParent);
		    	oAsyncClient.SetPersistantStorage(getActivity().getApplicationContext());
		    	oAsyncClient.MakeCallWithTag(url,"delete");
		    	oPd = new ProgressDialog(getActivity());
				oPd.setTitle("Deleting group...");
				oPd.setMessage("Please wait.");
				oPd.setIndeterminate(true);
				oPd.setCancelable(false);
				oPd.show();


				
			}
		});
        resendrequests.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Toast.makeText(getActivity(), "Resending requests", Toast.LENGTH_SHORT).show();
				String url = "http://www.smashed.in/api/b/gcm/groupresend?uniqueid="+mGroupData.uniqueId;
				SmashedAsyncClient oAsyncClient = new SmashedAsyncClient();
		    	oAsyncClient.Attach(oParent);
		    	oAsyncClient.SetPersistantStorage(getActivity().getApplicationContext());
		    	oAsyncClient.MakeCallWithTag(url,"resend");
				
			}
		});
        setHasOptionsMenu(true);
        return rootView;
    }
	@Override
	public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
		menu.clear();
	//	super.onCreateOptionsMenu(menu, inflater);
	}
	public void AddData(PrivateGroupData oGroupData)
	{
		mGroupData = oGroupData;
	}
	@Override 
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.savefriends:
	    	return true;
	    default:
	    	return super.onOptionsItemSelected(item);
	    }
		
	}
	  @Override
	  public void onResume() {
		  if(mGroupData.m_bMine == true)
		  {
			  resendrequests.setVisibility(View.VISIBLE);
		  }
		mFriendsAdapter = new FriendsPickerAdapter(getActivity());
		mFriendsAdapter.mFriends = new ArrayList<FacebookFriendsData>();
		if(mGroupData != null  && mGroupData.m_arrParticipants != null)
			mFriendsAdapter.mFriends.addAll(mGroupData.m_arrParticipants);

		  friendsList.setAdapter(mFriendsAdapter);
	     super.onResume(); 
	  }	 
	@Override
	public void OnResponse(String response, String tag, Object data) {
		if(oPd != null)
			oPd.cancel();
		if(tag.equals("delete"))
		{
			MyGroupDataSingleton.getInstance().RemoveGroup(mGroupData.uniqueId);
			if(this.isVisible())
			{
				FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
				fragmentManager.popBackStack();

			}
		}
		if(tag.equals("resend"))
		{
			Toast.makeText(getActivity(), "Requests sent", Toast.LENGTH_SHORT).show();
		}
	}
	@Override
	public void OnFailure() {
		// TODO Auto-generated method stub
		
	}

}
