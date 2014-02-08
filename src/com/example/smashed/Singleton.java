package com.example.smashed;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class Singleton {
    private static Singleton mInstance = null;

	public boolean m_bGalleryMenuItem = true;
	public boolean m_bCameraMenuItem  = true;
	public boolean m_bRowAddMenuItem = true;
	public boolean m_bSaveMenuItem = true;
	public boolean m_bSearchMenuItem = true;

    private Singleton(){
        
    }

    public static Singleton getInstance(){
        if(mInstance == null)
        {
            mInstance = new Singleton();
        }
        return mInstance;
    }
    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }


}
