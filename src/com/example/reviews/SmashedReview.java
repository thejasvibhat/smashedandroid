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

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
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
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.viewpagerindicator.TabPageIndicator;

public class SmashedReview extends FragmentActivity implements OnResponseListener{
    //private static final String[] CONTENT = new String[] { "INFO", "MAP", "PHOTOS", "MENU", "OVERHEARDS", "FOOD" };
	private static final String[] CONTENT = new String[] { "INFO", "REVIEWS","OVERHEARDS", "PHOTOS" };
    private ReviewData oRevData = null;
    private Intent mainintent;
    private int m_rating = 0;
    private String m_review = "";
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
	public void onBackPressed() {
    	LinearLayout oRevAdd = (LinearLayout) findViewById(R.id.addreview);
		if(oRevAdd.getVisibility() == View.VISIBLE)
		{
			slideToTop(oRevAdd);
		}
		else
			super.onBackPressed();
	}
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.revmain, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.revoh:
			CreateOverheardForBar();
			break;
		case R.id.revfs:
			CreateReviewForBar();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}
	private void UpdateReview(String id,int rating,String rev)
	{
		String url = "http://www.smashed.in/api/b/updatefscomment?fsbid="+oRevData.id+"&rating="+rating+"&description="+rev;
		SmashedAsyncClient oAsyncClient = new SmashedAsyncClient();
    	oAsyncClient.Attach(this);
    	oAsyncClient.SetPersistantStorage(getApplicationContext());
    	oAsyncClient.MakeCall(url);   

	}
	private void CreateReviewForBar()
	{
		
		LinearLayout oRevAdd = (LinearLayout) findViewById(R.id.addreview);
		if(oRevAdd.getVisibility() == View.INVISIBLE)
		{
			slideToBottom(oRevAdd);
		}
		else
		{
			slideToTop(oRevAdd);
		}
		Button oSubmit = (Button) oRevAdd.findViewById(R.id.submitrev);
		oSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				LinearLayout oRevAdd = (LinearLayout) findViewById(R.id.addreview);
				slideToTop(oRevAdd);
				m_review = ((EditText)oRevAdd.findViewById(R.id.enterrev)).getText().toString();
				UpdateReview(oRevData.id,m_rating,m_review);
	            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	            

			}
		});	
		
		ImageView orate1 = (ImageView) oRevAdd.findViewById(R.id.rate1);
		orate1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				SetRating(1);
			}
		});	
		ImageView orate5 = (ImageView) oRevAdd.findViewById(R.id.rate5);
		orate5.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				SetRating(5);
			}
		});	
		ImageView orate2 = (ImageView) oRevAdd.findViewById(R.id.rate2);
		orate2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				SetRating(2);
			}
		});	
		ImageView orate3 = (ImageView) oRevAdd.findViewById(R.id.rate3);
		orate3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				SetRating(3);
			}
		});	
		ImageView orate4 = (ImageView) oRevAdd.findViewById(R.id.rate4);
		orate4.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				SetRating(4);
			}
		});	
		//oRevAdd.setVisibility(View.VISIBLE);
		//oRevAdd.bringToFront();
	}
	private void SetRating(int rating)
	{
		LinearLayout oRevAdd = (LinearLayout) findViewById(R.id.addreview);
		ImageView rate1 = (ImageView) oRevAdd.findViewById(R.id.rate1);
		ImageView rate2 = (ImageView) oRevAdd.findViewById(R.id.rate2);
		ImageView rate3 = (ImageView) oRevAdd.findViewById(R.id.rate3);
		ImageView rate4 = (ImageView) oRevAdd.findViewById(R.id.rate4);
		ImageView rate5 = (ImageView) oRevAdd.findViewById(R.id.rate5);
		m_rating = rating;
		if(rating == 1)
		{
			rate1.setImageResource(R.drawable.rateon);
			rate2.setImageResource(R.drawable.rate);
			rate3.setImageResource(R.drawable.rate);
			rate4.setImageResource(R.drawable.rate);
			rate5.setImageResource(R.drawable.rate);
		}
		else if(rating == 2)
		{
			rate1.setImageResource(R.drawable.rateon);
			rate2.setImageResource(R.drawable.rateon);
			rate3.setImageResource(R.drawable.rate);
			rate4.setImageResource(R.drawable.rate);
			rate5.setImageResource(R.drawable.rate);
		}
		else if(rating == 3)
		{
			rate1.setImageResource(R.drawable.rateon);
			rate2.setImageResource(R.drawable.rateon);
			rate3.setImageResource(R.drawable.rateon);
			rate4.setImageResource(R.drawable.rate);
			rate5.setImageResource(R.drawable.rate);
		}
		else if(rating == 4)
		{
			rate1.setImageResource(R.drawable.rateon);
			rate2.setImageResource(R.drawable.rateon);
			rate3.setImageResource(R.drawable.rateon);
			rate4.setImageResource(R.drawable.rateon);
			rate5.setImageResource(R.drawable.rate);
		}
		else if(rating == 5)
		{
			rate1.setImageResource(R.drawable.rateon);
			rate2.setImageResource(R.drawable.rateon);
			rate3.setImageResource(R.drawable.rateon);
			rate4.setImageResource(R.drawable.rateon);
			rate5.setImageResource(R.drawable.rateon);
		}
	}
	public void slideToBottom(View view){
		view.setVisibility(View.VISIBLE);
		ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view,
	            "y", -view.getHeight(), 0);
	objectAnimator.addListener(new AnimatorListener() {

	        @Override
	        public void onAnimationStart(Animator animation) {
	        	LinearLayout oRevAdd = (LinearLayout) findViewById(R.id.addreview);
	            // TODO Auto-generated method stub
	        	oRevAdd.setVisibility(View.VISIBLE);
	        }

	        @Override
	        public void onAnimationRepeat(Animator animation) {
	            // TODO Auto-generated method stub

	        }

	        @Override
	        public void onAnimationEnd(Animator animation) {

	        }

	        @Override
	        public void onAnimationCancel(Animator animation) {
	            // TODO Auto-generated method stub

	        }
	    });
	            objectAnimator.setDuration(500);
	    objectAnimator.start();
		}
	public void slideToTop(View view){
		view.setVisibility(View.VISIBLE);
		ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view,
	            "y", 0, -view.getHeight());
	objectAnimator.addListener(new AnimatorListener() {

	        @Override
	        public void onAnimationStart(Animator animation) {
	        	LinearLayout oRevAdd = (LinearLayout) findViewById(R.id.addreview);
	            // TODO Auto-generated method stub
	        	oRevAdd.setVisibility(View.VISIBLE);
	        }

	        @Override
	        public void onAnimationRepeat(Animator animation) {
	        	

	        }

	        @Override
	        public void onAnimationEnd(Animator animation) {
	        	LinearLayout oRevAdd = (LinearLayout) findViewById(R.id.addreview);
	            // TODO Auto-generated method stub
	        	oRevAdd.setVisibility(View.INVISIBLE);
	        }

	        @Override
	        public void onAnimationCancel(Animator animation) {
	            // TODO Auto-generated method stub

	        }
	    });
	            objectAnimator.setDuration(500);
	    objectAnimator.start();
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

	@Override
	public void OnResponse(String response) {
		SmashedFsReviewsData oRev = new SmashedFsReviewsData();
		oRev.review = m_review;
		oRev.rating = String.valueOf(m_rating);
		if(oRevData.reviews != null)
			oRevData.reviews.add(oRev);
		
	}
	@Override
	public void OnFailure() {
		// TODO Auto-generated method stub
		
	}

}
