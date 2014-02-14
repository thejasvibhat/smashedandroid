package com.example.smashed;

import com.example.async.SmashedAsyncClient;
import com.loopj.android.http.PersistentCookieStore;

import android.content.Context;
import android.content.SharedPreferences;
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
	public SmashedAsyncClient oAsyncClient;
	public SharedPreferences m_LocalStorage;
	private Context mContext;
	public PersistentCookieStore myCookieStore;
	public boolean loggedIn = false;
	public String m_strType = "create";
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
    public void SetApplicationContext(Context oContext)
    {
    	mContext = oContext;
    	m_LocalStorage = mContext.getSharedPreferences("", mContext.MODE_PRIVATE);
    }
    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
    public void SetAccessToken(String oAccessToken)
    {
    	SharedPreferences.Editor editor = m_LocalStorage.edit();
    	editor.putString("access", oAccessToken);
    	editor.commit();
    }
    public String getAccessToken()
    {
    	return m_LocalStorage.getString("access", "NOT_FOUND");
    }


}
