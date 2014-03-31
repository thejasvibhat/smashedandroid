package com.smashedin.reviews;

import java.util.ArrayList;

public class PrivateGroupData {
	public String uniqueId;
	public ReviewData mRevData;
	public boolean m_bMine = false;
	public ArrayList<FacebookFriendsData> m_arrParticipants = new ArrayList<FacebookFriendsData>();
	public ArrayList<LiveData> m_arrInstantQueueMessages = new ArrayList<LiveData>();
}
