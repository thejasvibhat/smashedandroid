package com.example.reviews;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.example.async.SmashedAsyncClient;
import com.example.async.SmashedAsyncClient.OnResponseListener;
import com.example.smashedin.*;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.GridView;
import android.widget.TextView;

import com.viewpagerindicator.TabPageIndicator;

public class SmashedReview extends FragmentActivity implements OnResponseListener{
    private static final String[] CONTENT = new String[] { "INFO", "MAP", "PHOTOS", "MENU", "OVERHEARDS", "FOOD" };
    private ReviewData oRevData = null;
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

        

    }
    @Override
    public void onResume()
    {
        Bundle b = getIntent().getExtras();
        ReviewData oData = b.getParcelable("places");

        getActionBar().setTitle(b.getString("name"));
    	
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

		public void UpdateFragment() {
			
			m_oFragment.UpdateRevData(oRevData);
		}
    }

	@Override
	public void OnResponse(String response) {
		ReviewData oRev = null;
		try {
			oRev = ParseJson(response);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(oRev != null)
		{
			oRevData = oRev;
	        //FragmentPagerAdapter adapter = new GoogleMusicAdapter(getSupportFragmentManager());

	        ViewPager pager = (ViewPager)findViewById(R.id.pager);
	        GoogleMusicAdapter adapter = (GoogleMusicAdapter) pager.getAdapter();
	        adapter.UpdateFragment();
	        pager.setAdapter(adapter);

	        TabPageIndicator indicator = (TabPageIndicator)findViewById(R.id.indicator);
	        indicator.setViewPager(pager);
			
		}
		
	}
	private ReviewData ParseJson(String response) throws JSONException
	{
		ReviewData venue = new ReviewData();
		ArrayList<FsqVenue> venueList = new ArrayList<FsqVenue>();
		JSONObject jsonObj 	= (JSONObject) new JSONTokener(response).nextValue();
		
		JSONObject item	= (JSONObject) jsonObj.getJSONObject("response").getJSONObject("venue");
					
		venue.id 		= item.getString("id");
		venue.name		= item.getString("name");
		try {
			venue.rating    = item.getString("rating");	
		} catch (Exception e) {
			venue.rating    = "1.0";
		}
		
		JSONObject location = (JSONObject) item.getJSONObject("location");
		venue.location = new ReviewLocation();
		venue.location.PopulateData(location);
		venue.contact = item.getJSONObject("contact").getString("formattedPhone");
		ArrayList<String> photos= new ArrayList<String>();
		
		try {
			JSONArray photosUrls	= (JSONArray)((JSONObject)((JSONArray) item.getJSONObject("photos").getJSONArray("venues")).get(0)).getJSONArray("items");
			for(int i = 0; i < photosUrls.length(); i++)
			{
				JSONObject pata = (JSONObject)photosUrls.get(i);
				venue.photos.add(pata.getString("prefix")+pata.getString("suffix"));
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return venue;
	}

}
