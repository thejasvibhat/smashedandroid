package com.example.smashed;

public class Singleton {
    private static Singleton mInstance = null;

	public boolean m_bGalleryMenuItem = true;
	public boolean m_bCameraMenuItem  = true;
	public boolean m_bRowAddMenuItem = true;
	public boolean m_bSaveMenuItem = true;


    private Singleton(){
        
    }

    public static Singleton getInstance(){
        if(mInstance == null)
        {
            mInstance = new Singleton();
        }
        return mInstance;
    }

}
