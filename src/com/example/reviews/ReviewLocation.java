package com.example.reviews;

import org.json.JSONException;
import org.json.JSONObject;

public class ReviewLocation {
	public String lat;
	public String lng;
	public String address;
	public String postalcode;
	public String distance;
	public String city;
	public String state;
	
	public void ReviewLocation()
	{
		
	}
	public void PopulateData(JSONObject location) throws JSONException
	{
		lat = location.getString("lat");
		lng = location.getString("lng");
		try {
			address	= location.getString("address");	
		} catch (Exception e) {
			address	= "Not Available";
		}
		try {
			int dist = location.getInt("distance");
			float inPre = ((float)dist)/1000;
			distance = String.format("%.2f", inPre); 
	
		} catch (Exception e) {
			distance = "";
		}
		
		postalcode = location.getString("postalCode");
		city = location.getString("city");
		state = location.getString("state");
		 

	}
}
