package com.smashedin.reviews;

import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;

public class ReviewLocation {
	public String lat;
	public String lng;
	public String address;
	public String postalcode;
	public String distance;
	public String city;
	public String state;
	public Location m_location;
	
	public void PopulateData(JSONObject location) throws JSONException
	{
		m_location = new Location("hotel");
		
		lat = location.getString("lat");
		lng = location.getString("lng");
		m_location.setLatitude(Double.valueOf(lat));
		m_location.setLongitude(Double.valueOf(lng));
		try {
			address	= location.getString("address");	
		} catch (Exception e) {
			address	= "Not Available";
		}
		try {
			int dist = location.getInt("distance");
			float inPre = ((float)dist)/1000;
			distance = String.format("%.2f", inPre); 
			distance = distance + "km";
	
		} catch (Exception e) {
			distance = "";
		}
		
		//postalcode = location.getString("postalCode");
		try {
			city = location.getString("city");
			
		} catch (Exception e) {
			city = "";

		}
		
		try {
			state = location.getString("state");	
		} catch (Exception e) {
			state = "Karnataka";
		}
		
		 

	}
}
