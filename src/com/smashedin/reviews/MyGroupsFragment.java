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
import com.smashedin.reviews.SmashedReview.GoogleMusicAdapter;
import com.smashedin.smashed.Singleton;
import com.smashedin.smashedin.R;
import com.viewpagerindicator.TabPageIndicator;
import com.loopj.android.image.SmartImageView;
import com.loopj.android.image.SmartImageTask.OnCompleteListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
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

public class MyGroupsFragment extends android.support.v4.app.Fragment implements OnResponseListener {
	private MyGroupInstantFragment mMineFragment;
	private MyGroupInstantFragment mFriendsFragment;
	private ListView friendSelector;
	private ListView friendSelected;
	private PrivateGroupData mMineGroupData;
	private PrivateGroupData mFriendsGroupData;
	private ShowParticipantsFragment myFriendListFragment;
	public MyGroupsFragment(){}
	public void AddData(PrivateGroupData oMineGroupData,PrivateGroupData oFriendsGroupData) {
		mMineGroupData = oMineGroupData;
		mFriendsGroupData = oFriendsGroupData;
	}
	private FriendsPickerAdapter mFriendsAdapter;
	private FriendsPickerAdapter mFriendsPickedAdapter;
	private static final String[] CONTENT = new String[] {"MINE", "FRIENDS" };
	private TabPageIndicator indicator;
	private ViewPager pager;
	FragmentPagerAdapter adapter;
	// Search EditText
    EditText inputSearch;
	private int currentPageIndex = 0;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.mygroups_view, container, false);

       
        getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
     
        setHasOptionsMenu(true);
        adapter = new GoogleMusicAdapter(getActivity().getSupportFragmentManager());

        pager = (ViewPager)rootView.findViewById(R.id.pagergroup);
        pager.setAdapter(adapter);

        indicator = (TabPageIndicator)rootView.findViewById(R.id.indicatorgroup);
        indicator.setViewPager(pager);

        setHasOptionsMenu(true);
        if(mMineFragment != null)
        {
        	android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        	for(android.support.v4.app.Fragment oFrag:fragmentManager.getFragments())
        	{
        		if(oFrag == mMineFragment)
        			fragmentManager.beginTransaction().remove(mMineFragment).commit();
        		if(oFrag == mFriendsFragment)
        			fragmentManager.beginTransaction().remove(mFriendsFragment).commit();

        	}
        	//mMineFragment.RefreshView();
        	//mFriendsFragment.RefreshView();
        }
        adapter.notifyDataSetChanged();
        pager.invalidate();
        return rootView;
    }
	@Override
	public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
		menu.clear();
		getActivity().getMenuInflater().inflate(R.menu.groupmenu, menu);
	//	super.onCreateOptionsMenu(menu, inflater);
	}

	@Override 
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.peoplegroup:
	    	ShowParticipants();
	    	return true;
	    default:
	    	return super.onOptionsItemSelected(item);
	    }
		
	}
	private void ShowParticipants()
	{
		myFriendListFragment = new ShowParticipantsFragment();
		if(currentPageIndex == 0)
			myFriendListFragment.AddData(mMineGroupData);
		else
			myFriendListFragment.AddData(mFriendsGroupData);
		android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
		fragmentManager.beginTransaction()
		.add(R.id.viewpagetabs, myFriendListFragment).addToBackStack( "friendlist" ).commit();
		fragmentManager.addOnBackStackChangedListener(new OnBackStackChangedListener() {
			
			@Override
			public void onBackStackChanged() {
				if(mMineFragment != null && mMineFragment.isVisible())
				{
					mMineFragment.CheckGroupStatus();
				}
				if(mFriendsFragment != null && mFriendsFragment.isVisible())
				{
					mFriendsFragment.CheckGroupStatus();
				}
				
			}
		});
			
	}
	  @Override
	  public void onResume() {
	    	indicator.setOnPageChangeListener(new OnPageChangeListener() {
				
				@Override
				public void onPageSelected(int arg0) {
					currentPageIndex = arg0;
					
				}
				
				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
					String x = "x";
					
				}
				
				@Override
				public void onPageScrollStateChanged(int arg0) {
					String x = "x";
					
				}
			});

	     super.onResume();
	   
	  }	 
	@Override
	public void OnResponse(String response, String tag, Object data) {
		// TODO Auto-generated method stub
		try {
			ParseJsonFriends(response);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
class GoogleMusicAdapter extends FragmentPagerAdapter {
    	
        public GoogleMusicAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
        	if(position == 0)
        	{
        		mMineFragment = MyGroupInstantFragment.newInstance(CONTENT[position % CONTENT.length],mMineGroupData,mFriendsGroupData);
        		return mMineFragment;
        	}
        	else
        	{
        		mFriendsFragment = MyGroupInstantFragment.newInstance(CONTENT[position % CONTENT.length],mMineGroupData,mFriendsGroupData);
        		return mFriendsFragment;
        	} 
        }
        
        @Override
        public CharSequence getPageTitle(int position) {
            return CONTENT[position % CONTENT.length];
        }

        @Override
        public int getCount() {
            return CONTENT.length;
        }
        @Override
        public void destroyItem(View collection, int position, Object view) {
             ((ViewPager) collection).removeView((View) view);
        }
        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }  

    }
public void AddParentFragment(ReviewFragment reviewFragment) {
	// TODO Auto-generated method stub
	
}
}
