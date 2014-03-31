package com.smashedin.reviews;

import java.util.ArrayList;

public class ReviewData {
	public String id;
	public boolean m_bFromNotification = false;
	public String name;
	public String rating;
	public String contact;
	public String url;
	public String review;
	public String barname;
	public ReviewLocation location;
	public String photo;
	public String categories;
	public OverheardData ohdata;
	public ArrayList<SmashedFsReviewsData> reviews;
	public ArrayList<String> photos;
	public ArrayList<LiveData> livefeeds;
	public ArrayList<LiveData> grouplivefeedsmine = new ArrayList<LiveData>();
	public ArrayList<LiveData> grouplivefeedsfriends = new ArrayList<LiveData>();
	public boolean m_bfollow = false;

}
