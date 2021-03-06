package com.smashedin.reviews;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.internal.es;
import com.smashedin.smashedin.R;

import android.R.anim;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smashedin.async.SmashedAsyncClient;
import com.smashedin.async.SmashedAsyncClient.OnResponseListener;
import com.smashedin.facebook.HelloFacebookSampleActivity;
import com.smashedin.smashed.CreateOverHeardFragment;
import com.smashedin.smashed.GridOverheardFragment;
import com.smashedin.smashed.GridOverheardSkeletonFragment;
import com.smashedin.smashed.Singleton;
import com.smashedin.smashedin.*;
import com.viewpagerindicator.TabPageIndicator;

public class SmashedReview extends FragmentActivity implements OnResponseListener{

    
	
	private static final String[] CONTENT = new String[] {"INSTANTS", "OVERHEARDS","REVIEWS","INFO" };
    private ReviewData oRevData = null;
    private Intent mainintent;
    private int m_rating = 0;
    private String m_review = "";
	private ProgressDialog oPd = null;
	private String gBid;
	public ReviewFragment m_oReviewsPageFragment = null;
	public ReviewFragment m_oInstantPageFragment = null;
	private ViewPager pager;
	private TabPageIndicator indicator;
	private FragmentActivity mActivity;
	private View oh;
	private int currentPageIndex = 0;
	private SelectFriendsFragment friendPickerFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	Singleton.getInstance().m_bFromNotification = false;
    	Singleton.getInstance().m_bFromMyGroups = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpage_tabs);
        mActivity = this;
        Bundle b = getIntent().getExtras();
        int pos = b.getInt("position");
        String bid = b.getString("bid","");
        int position = b.getInt("position", 0);
        FragmentPagerAdapter adapter = new GoogleMusicAdapter(getSupportFragmentManager());

        pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(adapter);

        indicator = (TabPageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(pager);
        pager.setOffscreenPageLimit(3);
        if(bid.equals("navdrawer") == true)
        {
        	Singleton.getInstance().m_bFromNotification = true;
        	oRevData = Singleton.getInstance().mRevData;
        	pager.setCurrentItem(0);
        }
        else if(bid.equals("groupdrawer") == true)
        {
        	Singleton.getInstance().m_bFromMyGroups = true;
        	oRevData = MyGroupDataSingleton.getInstance().m_arrPrivateGroups.get(position).mRevData;
        	pager.setCurrentItem(0);
        }

        else
        oRevData = (ReviewData) ListReviewDataSingleton.getInstance().venueList.get(pos);

        getActionBar().setTitle(oRevData.name);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
  		      new IntentFilter("review-event"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mLoginMessageReceiver,
    		      new IntentFilter("custom-login-event"));
        
		getActionBar().setDisplayHomeAsUpEnabled(true);
		oh = LayoutInflater.from(mActivity).inflate(R.layout.quickinstantoh, null);
		ViewGroup parentView = (ViewGroup) findViewById(R.id.quickcontainer);
		parentView.addView(oh);
    }
    @Override
    public void onResume()
    {
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
        Bundle b = getIntent().getExtras();

        getActionBar().setTitle(b.getString("name"));
        if(Singleton.getInstance().loggedIn == true)
    	{
    		Singleton.getInstance().m_bHideLoginMenuItem = true;
    	}
        else
        {
        	Singleton.getInstance().m_bHideLoginMenuItem = false;
        }
        
    	Singleton.getInstance().m_bCreateReviewMenuItem = false;
    	Singleton.getInstance().m_bCreateReviewOhMenuItem = false;
    	if(Singleton.getInstance().m_bFromNotification == true)
    	{
    		Singleton.getInstance().m_bCreateFollowMenuItem = true;
    		Singleton.getInstance().m_bUnFollowMenuItem = false;
    		Singleton.getInstance().m_bFromNotification = false;
    	}
    	else if(Singleton.getInstance().m_bFromMyGroups == true)
    	{
    		Singleton.getInstance().m_bCreateFollowMenuItem = true;
    		Singleton.getInstance().m_bUnFollowMenuItem = false;    		
    	}
    	else
    	{
    		Singleton.getInstance().m_bCreateFollowMenuItem = true;
    		Singleton.getInstance().m_bUnFollowMenuItem = true;
    	}
    	//if(pager.getCurrentItem() == 1)
    	{
			if(oRevData.m_bfollow == true)
			{
				Singleton.getInstance().m_bUnFollowMenuItem = false;
				Singleton.getInstance().m_bCreateFollowMenuItem = true;
			}
			else
			{
				Singleton.getInstance().m_bUnFollowMenuItem = true;
				Singleton.getInstance().m_bCreateFollowMenuItem = false;
			}
			mActivity.invalidateOptionsMenu();
    		
    	}
    	Singleton.getInstance().m_bShareMenuItem = true;
    	this.invalidateOptionsMenu();
    	super.onResume();
    }
    @Override
	public void onBackPressed() {
    	Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_container_each);
		   if (fragment instanceof FsOverHeardFragment) {
		    
			   Singleton.getInstance().m_bCreateReviewMenuItem = false;
		    	Singleton.getInstance().m_bCreateReviewOhMenuItem = false;
		    	Singleton.getInstance().m_bShareMenuItem = true;
		    	Singleton.getInstance().m_bCreateFollowMenuItem = false;
		    	if(oRevData.m_bfollow == true)
				{
					Singleton.getInstance().m_bUnFollowMenuItem = false;
					Singleton.getInstance().m_bCreateFollowMenuItem = true;
				}
				else
				{
					Singleton.getInstance().m_bUnFollowMenuItem = true;
					Singleton.getInstance().m_bCreateFollowMenuItem = false;
				}
		    	this.invalidateOptionsMenu();
		    	super.onBackPressed();
				  return;
		   }
		  
    	LinearLayout oRevAdd = (LinearLayout) findViewById(R.id.addreview);
    	RelativeLayout oh = (RelativeLayout) findViewById(R.id.quickinstantohid);
		if(oRevAdd.getVisibility() == View.VISIBLE)
		{
			slideToTop(oRevAdd);
		}
		else
		{
			if(oh.getVisibility() == View.VISIBLE)
			{
				slideToTopOh(oh);
			}
			else
			{
				Singleton.getInstance().m_bFirstInstanceReview = true;
				super.onBackPressed();
			}
		}
	}
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.revmain, menu);
		 MenuItem m_oLoginMenuItem = menu.findItem(R.id.login);
	        if(Singleton.getInstance().m_bHideLoginMenuItem == true)
				m_oLoginMenuItem.setVisible(false);
        MenuItem m_oShareMenuItem = menu.findItem(R.id.shareohrev);
		if(Singleton.getInstance().m_bShareMenuItem == true)
			m_oShareMenuItem.setVisible(false);
		
        MenuItem m_oCreateMenuItem = menu.findItem(R.id.revfs);
		if(Singleton.getInstance().m_bCreateReviewMenuItem == true)
			m_oCreateMenuItem.setVisible(false);

		 MenuItem m_oCreateOhMenuItem = menu.findItem(R.id.revoh);
			if(Singleton.getInstance().m_bCreateReviewOhMenuItem == true)
				m_oCreateOhMenuItem.setVisible(false);
			MenuItem m_oFollowMenuItem = menu.findItem(R.id.followinstant);
				if(Singleton.getInstance().m_bCreateFollowMenuItem == true)
					m_oFollowMenuItem.setVisible(false);
				MenuItem m_oUnFollowMenuItem = menu.findItem(R.id.unfollowinstant);
				if(Singleton.getInstance().m_bUnFollowMenuItem == true)
					m_oUnFollowMenuItem.setVisible(false);

				MenuItem m_oUnAddGroupMenuItem = menu.findItem(R.id.privategroup);
				if(Singleton.getInstance().m_bPrivateGroupMenuItem == true)
					m_oUnAddGroupMenuItem.setVisible(false);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;
		case R.id.revoh:
			CreateOverheardForBar(oRevData.id);
			break;
		case R.id.revfs:
			CreateReviewForBar(oRevData.id);
			break;
		case R.id.login:
			Login();
			return true;
		case R.id.privategroup:
			CreatePrivateGroup();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}
	private void CreatePrivateGroup()
	{
		if(friendPickerFragment == null)
		{
			friendPickerFragment = new SelectFriendsFragment();
			friendPickerFragment.AddData(oRevData);
		}
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.viewpagetabs, friendPickerFragment).addToBackStack( "main" ).commit();
		fragmentManager.addOnBackStackChangedListener(new OnBackStackChangedListener() {
			
			@Override
			public void onBackStackChanged() {
				if(m_oInstantPageFragment.isVisible())
				{
					m_oInstantPageFragment.CheckForPrivateGroups();
				}
				
			}
		});
	}
	private void UpdateReview(String id,int rating,String rev, String username)
	{
		String url = "http://www.smashed.in/api/b/updatefscomment?fsbid="+oRevData.id+"&rating="+rating+"&description="+rev+"&name="+oRevData.name+"&username="+username;
		SmashedAsyncClient oAsyncClient = new SmashedAsyncClient();
    	oAsyncClient.Attach(this);
    	oAsyncClient.SetPersistantStorage(getApplicationContext());
    	oAsyncClient.MakeCall(url);   

	}
	private void CreateReviewForBar(String bid)
	{
		gBid = bid;
		LinearLayout oRevAdd = (LinearLayout) findViewById(R.id.addreview);
		TextView ousername = (TextView) findViewById(R.id.usernametext);
		ousername.setText(Singleton.getInstance().username);
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
				EditText username = (EditText) oRevAdd.findViewById(R.id.usernametext);
				UpdateReview(gBid,m_rating,m_review,username.getText().toString());
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
	public void slideToBottomOh(View view){
		view.setVisibility(View.VISIBLE);
		ViewGroup scrollView = (ViewGroup) findViewById(R.id.quickcontainer);
		scrollView.setVisibility(View.VISIBLE);
		ViewGroup parentView = (ViewGroup) findViewById(R.id.viewpagetabs);
		ViewGroup childViewView = (ViewGroup) findViewById(R.id.slideOh);
		int from = -parentView.getHeight();
		int to = 0;//parentView.getHeight() - childViewView.getHeight();
		
		ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view,
	            "y", from, to);
	objectAnimator.addListener(new AnimatorListener() {

	        @Override
	        public void onAnimationStart(Animator animation) {
	        	RelativeLayout oh = (RelativeLayout) findViewById(R.id.quickinstantohid);
	            // TODO Auto-generated method stub
	        	oh.setVisibility(View.VISIBLE);
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
	public void slideToTopOh(View view){
		view.setVisibility(View.VISIBLE);

		ViewGroup parentView = (ViewGroup) findViewById(R.id.viewpagetabs);
		ViewGroup childViewView = (ViewGroup) findViewById(R.id.slideOh);
		//int to = parentView.getHeight();
		//int from = parentView.getHeight() - childViewView.getHeight();
		int to = -childViewView.getHeight();
		int from = 0;
		
		ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view,
	            "y", from, to);
	objectAnimator.addListener(new AnimatorListener() {

	        @Override
	        public void onAnimationStart(Animator animation) {
	        	RelativeLayout oh = (RelativeLayout) findViewById(R.id.quickinstantohid);
	            // TODO Auto-generated method stub
	        	oh.setVisibility(View.VISIBLE);
	        }

	        @Override
	        public void onAnimationRepeat(Animator animation) {
	        	

	        }

	        @Override
	        public void onAnimationEnd(Animator animation) {
	        	RelativeLayout oh = (RelativeLayout) findViewById(R.id.quickinstantohid);
	            // TODO Auto-generated method stub
	        	oh.setVisibility(View.INVISIBLE);
	    		ViewGroup scrollView = (ViewGroup) findViewById(R.id.quickcontainer);
	    		scrollView.setVisibility(View.GONE);

	        }

	        @Override
	        public void onAnimationCancel(Animator animation) {
	            // TODO Auto-generated method stub

	        }
	    });
	            objectAnimator.setDuration(500);
	    objectAnimator.start();
		}
	private void CreateOverheardForBar(String bid)
	{
		if(currentPageIndex == 0)
		{
			  ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		                 android.R.layout.simple_dropdown_item_1line, Singleton.getInstance().m_arrTags);
			AutoCompleteTextView textView = (AutoCompleteTextView)
	                findViewById(R.id.autocomptag);
			textView.setAdapter(adapter);
			Button suboh = (Button) findViewById(R.id.submitphinstant);
			suboh.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					View oh = LayoutInflater.from(mActivity).inflate(R.layout.quickinstantoh, null);
					slideToTopOh(oh);
					EditText toptext = (EditText) findViewById(R.id.topinstanttext);
					EditText bottomtext = (EditText) findViewById(R.id.bottominstanttext);
					AutoCompleteTextView oTag = (AutoCompleteTextView) findViewById(R.id.autocomptag);
					m_oInstantPageFragment.SendMessageOh(oTag.getText().toString(),toptext.getText().toString(),bottomtext.getText().toString());
				}
			});
			if(oh.getVisibility() == View.INVISIBLE)
			{
				slideToBottomOh(oh);
			}
			else
			{
				slideToTopOh(oh);
			}
		}
		else
		{
			Singleton.getInstance().mOhRevData = oRevData;
			if(mainintent == null)
				mainintent = new Intent(this, MainActivity.class);
			Intent intent = new Intent("my-event");
	  	  // add data
	  	  	intent.putExtra("position", 2);
	  	  	intent.putExtra("bid", bid);
	  	  	LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
			
	        startActivity(mainintent);	
		}

		

	}
	private void Login()
	{
		Singleton.getInstance().m_oType = "login";
		String accessToken = Singleton.getInstance().getAccessToken(); 
		if(accessToken == "NOT_FOUND")
		{
            Intent intent = new Intent(getApplicationContext(), HelloFacebookSampleActivity.class);
            startActivity(intent);
            return;
		}
		else
		{
		/*	if(oPd == null)
			{
				oPd  = new ProgressDialog(this);
				oPd.setTitle("Trying to get Smashed...");
				oPd.setMessage("Please wait.");
				oPd.setIndeterminate(true);
				oPd.setCancelable(false);
				oPd.show();
			}

        	String url = "http://www.smashed.in/auth/post/facebook?access_token="+accessToken;
        	SmashedAsyncClient oAsyncClient = new SmashedAsyncClient();
        	oAsyncClient.Attach(this);
        	oAsyncClient.SetPersistantStorage(getApplicationContext());
        	oAsyncClient.MakeCall(url);       */ 	
			Singleton.getInstance().m_oType = "reviews";
			Singleton.getInstance().loggedIn = true;
			Singleton.getInstance().m_bHideLoginMenuItem = true;
			this.invalidateOptionsMenu();
		}
	}
	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}
	
    class GoogleMusicAdapter extends FragmentPagerAdapter {
    	
        public GoogleMusicAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
        	if(position == 0) //reviews page
        	{
        		m_oInstantPageFragment = ReviewFragment.newInstance(CONTENT[position % CONTENT.length],oRevData);           	 
                return m_oInstantPageFragment;
        	}
        	else if(position == 2) //reviews page
        	{
        		m_oReviewsPageFragment = ReviewFragment.newInstance(CONTENT[position % CONTENT.length],oRevData);           	 
                return m_oReviewsPageFragment;
        	}
        	else
        		return ReviewFragment.newInstance(CONTENT[position % CONTENT.length],oRevData);
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
        public void destroyItem(ViewGroup container, int position, Object object) {
        	//container.removeView((View) object);
           // super.destroyItem(container, position, object);
        }

    }

	@Override
	public void OnResponse(String response,String tag,Object obj) {
/*		if(Singleton.getInstance().m_oType == "login")
		{
			Singleton.getInstance().m_oType = "reviews";
			Singleton.getInstance().loggedIn = true;
			Singleton.getInstance().parseJsonUserDetails(response);
			if(oPd != null)
				oPd.dismiss();
			Singleton.getInstance().m_bHideLoginMenuItem = true;
			this.invalidateOptionsMenu();
			return;
		}*/
		SmashedFsReviewsData oRev = new SmashedFsReviewsData();
		oRev.review = m_review;
		oRev.rating = String.valueOf(m_rating);
		EditText username = (EditText) findViewById(R.id.usernametext);
		oRev.username = username.getText().toString();
		if(oRevData.reviews != null)
			oRevData.reviews.add(oRev);
		m_oReviewsPageFragment.RefreshReviews(oRev);
		
	}
	@Override
	public void OnFailure() {
		// TODO Auto-generated method stub
		
	}
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		  @Override
		  public void onReceive(Context context, Intent intent) {
		    // Extract data included in the Intent
		    String type = intent.getStringExtra("type");
		    String bid = intent.getStringExtra("bid");
		    if(type == "review")
		    {
		    	CreateReviewForBar(bid);
		    }
		    else
		    {
		    	CreateOverheardForBar(bid);
		    }
		  }

	};
	
	private BroadcastReceiver mLoginMessageReceiver = new BroadcastReceiver() {
		  @Override
		  public void onReceive(Context context, Intent intent) {
			  Login();
		  }

	};

	
}
