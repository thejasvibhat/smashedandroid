package com.smashedin.smashed;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.loopj.android.http.PersistentCookieStore;
import com.smashedin.async.SmashedAsyncClient;
import com.smashedin.reviews.LiveData;
import com.smashedin.reviews.ReviewData;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class Singleton {
    private static Singleton mInstance = null;

	public boolean m_bGalleryMenuItem = true;
	public boolean m_bCameraMenuItem  = true;
	public boolean m_bRowAddMenuItem = true;
	public boolean m_bSaveMenuItem = true;
	public boolean m_bShareMenuItem = true;
	public boolean m_bSearchMenuItem = true;
	public boolean m_bSaveOhTextMenuItem = true;
	public boolean m_bSearchOverheardSkel = true;
	public boolean m_bHideLoginMenuItem = true;
	public boolean m_bDrawerClosed = false;
	public boolean m_bCreateReviewMenuItem = true;
	public boolean m_bCreateReviewOhMenuItem = true;
	public boolean m_bCreateFollowMenuItem = true;
	public boolean m_bUnFollowMenuItem = true;
	public String m_oType = "init";
	public SmashedAsyncClient oAsyncClient;
	public SharedPreferences m_LocalStorage;
	private Context mContext;
	public PersistentCookieStore myCookieStore;
	public boolean loggedIn = false;
	public boolean m_bnevermind = false;
	public String m_strType = "create";
	public String bid = "";
	public String username = "Anonymous";
	public String usericonurl = "";
	public String regid;
	public String m_strMessageGcm;
	public String m_strMessageGcmUser;

	public String m_strMessageGcmBid;
	public ArrayList<LiveData> m_arrListGcmMessages = new ArrayList<LiveData>();

	public String m_strMessageGcmBname;

	public ReviewData mRevData;

	public boolean m_bAppHidden = false;

	public boolean m_bFromNotification = false;
	public Location m_livelocation = null;
	public ArrayList<ReviewData> FsVenues = new ArrayList<ReviewData>();

    private Singleton(){
    	oAsyncClient = new SmashedAsyncClient();
    }
    public static Singleton getInstance(){
        if(mInstance == null)
        {
            mInstance = new Singleton();
        }
        return mInstance;
    }
    public void restoreUserDetails()
    {
    	username = m_LocalStorage.getString("username", "Anonymous");
    	usericonurl = m_LocalStorage.getString("usericonurl", "");
    	loggedIn = m_LocalStorage.getBoolean("loggedin", false);
    }
    public void SetApplicationContext(Context oContext)
    {
    	mContext = oContext;
    	m_LocalStorage = mContext.getSharedPreferences("", Context.MODE_PRIVATE);
    	restoreUserDetails();
    }
    public float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
    public void SetAccessToken(String oAccessToken)
    {
    	SharedPreferences.Editor editor = m_LocalStorage.edit();
    	editor.putString("access", oAccessToken);
    	editor.putString("provider", "facebook");
    	editor.commit();
    }
    public void SetFollowBid(String bid)
    {    	
    	Set<String> stringSet = new HashSet<String>();
    	stringSet = m_LocalStorage.getStringSet("follow", stringSet);
    	stringSet.add(bid);
    	SharedPreferences.Editor editor = m_LocalStorage.edit();
    	editor.putStringSet("follow", stringSet);
    	editor.commit();
    }
    public HashSet<String> GetFollowingBids()
    {
    	Set<String> stringSet = new HashSet<String>();
    	stringSet = m_LocalStorage.getStringSet("follow", stringSet);
    	return (HashSet<String>) stringSet;
    }
    public void SetAccessTokenGoogle(String oAccessToken)
    {
    	SharedPreferences.Editor editor = m_LocalStorage.edit();
    	editor.putString("access", oAccessToken);
    	editor.putString("provider", "google");
    	editor.commit();
    }

    public String getAccessToken()
    {
    	return m_LocalStorage.getString("access", "NOT_FOUND");
    }
    public String GetProvider()
    {
    	return m_LocalStorage.getString("provider", "NOT_FOUND");
    }
    public void ClearAllOptionMenus()
    {
    	m_bGalleryMenuItem = true;
    	m_bCameraMenuItem  = true;
    	m_bRowAddMenuItem = true;
    	m_bSaveMenuItem = true;
    	m_bShareMenuItem = true;
    	m_bSearchMenuItem = true;
    	m_bSaveOhTextMenuItem = true;
    	m_bSearchOverheardSkel = true;
    	m_bHideLoginMenuItem = true;
    	m_bDrawerClosed = false;
    	m_bCreateReviewMenuItem = true;
    	m_bCreateReviewOhMenuItem = true;
    	m_bCreateFollowMenuItem = true;
    }
	public void parseJsonUserDetails(String response) {
		JSONObject jsonObj;
		try {
			jsonObj = (JSONObject) new JSONTokener(response).nextValue();			
			username = jsonObj.getString("username");
			usericonurl = jsonObj.getString("avatar");
			
			SharedPreferences.Editor editor = m_LocalStorage.edit();
	    	editor.putString("username", username);
	    	editor.putString("usericonurl", usericonurl);
	    	editor.putBoolean("loggedin", true);
	    	editor.commit();
	    	
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void SetUnFollowBid(String id) {
    	Set<String> stringSet = new HashSet<String>();
    	stringSet = m_LocalStorage.getStringSet("follow", stringSet);
    	stringSet.remove(id);
    	SharedPreferences.Editor editor = m_LocalStorage.edit();
    	editor.putStringSet("follow", stringSet);
    	editor.commit();

		
	}


}
