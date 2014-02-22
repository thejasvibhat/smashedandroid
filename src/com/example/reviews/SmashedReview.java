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
        String id = b.getString("id");

        getActionBar().setTitle(b.getString("name"));
		String url 	= "https://api.foursquare.com/v2/venues/"+id+"?client_id=5MZNWHVUBAKSAYIOD3QZZ5X2IDLCGWKM5DV4P0UJ3PFLM5P2&client_secret=XSZAZ5XHDOEBBGJ331T4UNVGY5S2MHU0XJVEETV2SC5RWERC&v=2013081";
    	SmashedAsyncClient oAsyncClient = new SmashedAsyncClient();
    	oAsyncClient.Attach(this);
    	oAsyncClient.MakeCall(url);        	
    	
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
