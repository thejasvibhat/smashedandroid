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

public class ShowParticipantsFragment extends android.support.v4.app.Fragment implements OnResponseListener {
	private ListView friendsList;
	public ShowParticipantsFragment(){}
	private FriendsPickerAdapter mFriendsAdapter;
	private PrivateGroupData mGroupData;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.view_friends, container, false);

       
        getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        friendsList = (ListView) rootView.findViewById(R.id.friendsList);
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
		mFriendsAdapter = new FriendsPickerAdapter(getActivity());
		mFriendsAdapter.mFriends = new ArrayList<FacebookFriendsData>();
		if(mGroupData != null  && mGroupData.m_arrParticipants != null)
			mFriendsAdapter.mFriends.addAll(mGroupData.m_arrParticipants);

		  friendsList.setAdapter(mFriendsAdapter);
	     super.onResume(); 
	  }	 
	@Override
	public void OnResponse(String response, String tag, Object data) {
	}
	@Override
	public void OnFailure() {
		// TODO Auto-generated method stub
		
	}

}
