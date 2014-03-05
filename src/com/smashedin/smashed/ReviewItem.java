package com.smashedin.smashed;

import android.graphics.Bitmap;

public class ReviewItem {
	
	private String title;
	private String id;
	private String icon;
	private Bitmap bm = null;
	private String count = "0";
	// boolean to set visiblity of the counter
	private boolean isCounterVisible = false;
	
	public ReviewItem(){}

	public ReviewItem(String title, String icon,String id){
		this.title = title;
		this.icon = icon;
		this.id = id;
	}
	
	
	public String getTitle(){
		return this.title;
	}
	
	public String getIcon(){
		return this.icon;
	}
	
	public String getCount(){
		return this.count;
	}
	public Bitmap getBitmap(){
		return this.bm;
	}
	public String getId(){
		return this.id;
	}
	public boolean getCounterVisibility(){
		return this.isCounterVisible;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public void setIcon(String icon){
		this.icon = icon;
	}
	
	public void setCount(String count){
		this.count = count;
	}
	public void setBitmap(Bitmap obm){
		this.bm = obm;
	}
	
	public void setCounterVisibility(boolean isCounterVisible){
		this.isCounterVisible = isCounterVisible;
	}
}
