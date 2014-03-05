package com.smashedin.config;

import java.util.ArrayList;
public class OverheardData {
    private static OverheardData mInstance = null;

    public  ArrayList<String> mThumbIds = new ArrayList<String>();
    public  ArrayList<String> mTopTexts = new ArrayList<String>();
    public  ArrayList<String> mBottomTexts = new ArrayList<String>();
    public  ArrayList<String> mResIds = new ArrayList<String>();
    private OverheardData(){
        
    }

    public static OverheardData getInstance(){
        if(mInstance == null)
        {
            mInstance = new OverheardData();
        }
        return mInstance;
    }
    public void Clear()
    {
    	mThumbIds.clear();
    	mTopTexts.clear();
    	mBottomTexts.clear();
    }
    public void Remove(int postion)
    {
    	mThumbIds.remove(postion);
    	mTopTexts.remove(postion);
    	mBottomTexts.remove(postion);
    }
    public void AddImage(int position,String url)
    {
    	mThumbIds.add(position, url);
    	mTopTexts.add("");
    	mBottomTexts.add("");

    }
    public void AddImageId(int position,String id)
    {
    	mResIds.add(position, id);
    }    
    public void AddTexts(String top,String bottom,int position)
    {
    	mTopTexts.add(position, top);
    	mBottomTexts.add(position, bottom);
    }
    public void AddLocalOverheard()
    {
    	mThumbIds.add("local");
    	mTopTexts.add("");
    	mBottomTexts.add("");
    }

}
