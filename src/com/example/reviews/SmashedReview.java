package com.example.reviews;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.example.async.SmashedAsyncClient;
import com.example.async.SmashedAsyncClient.OnResponseListener;
import com.example.smashed.Singleton;
import com.example.smashedin.*;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.viewpagerindicator.TabPageIndicator;

public class SmashedReview extends FragmentActivity{
    //private static final String[] CONTENT = new String[] { "INFO", "MAP", "PHOTOS", "MENU", "OVERHEARDS", "FOOD" };
	private static final String[] CONTENT = new String[] { "INFO", "OVERHEARDS", "PHOTOS" };
    private ReviewData oRevData = null;
    private Intent mainintent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpage_tabs);
        Bundle b = getIntent().getExtras();
        int pos = b.getInt("position");
        oRevData = (ReviewData) ListReviewDataSingleton.getInstance().venueList.get(pos);

        FragmentPagerAdapter adapter = new GoogleMusicAdapter(getSupportFragmentManager());

        ViewPager pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(adapter);

        TabPageIndicator indicator = (TabPageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(pager);
        pager.setOffscreenPageLimit(3); 
        getActionBar().setTitle(oRevData.name);

    }
    @Override
    public void onResume()
    {
        Bundle b = getIntent().getExtras();
        ReviewData oData = b.getParcelable("places");

        getActionBar().setTitle(b.getString("name"));
    	super.onResume();
    }
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.revmain, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		CreateOverheardForBar();
		return super.onOptionsItemSelected(item);
	}
	private void CreateOverheardForBar()
	{
		if(mainintent == null)
			mainintent = new Intent(this, MainActivity.class);
		Intent intent = new Intent("my-event");
  	  // add data
  	  	intent.putExtra("position", 3);
  	  	intent.putExtra("bid", oRevData.id);
  	  	LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
		
        startActivity(mainintent);

	}

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}
	
    class GoogleMusicAdapter extends FragmentPagerAdapter {
    	public ReviewFragment m_oFragment = null;
        public GoogleMusicAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
        	m_oFragment = ReviewFragment.newInstance(CONTENT[position % CONTENT.length],oRevData); 
            return m_oFragment;
        }
        
        @Override
        public CharSequence getPageTitle(int position) {
            return CONTENT[position % CONTENT.length].toUpperCase();
        }

        @Override
        public int getCount() {
            return CONTENT.length;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        	container.removeView((View) object);
            super.destroyItem(container, position, object);
        }


		public void UpdateFragment() {
			
			m_oFragment.UpdateRevData(oRevData);
		}
    }

}
