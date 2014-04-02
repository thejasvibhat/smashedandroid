package com.smashedin.reviews;

import java.util.ArrayList;


public class MyGroupDataSingleton {
    private static MyGroupDataSingleton mInstance = null;
    public ArrayList<PrivateGroupData> m_arrPrivateGroups = new ArrayList<PrivateGroupData>();
    public PrivateGroupData acceptGroupData = new PrivateGroupData();
    private MyGroupDataSingleton(){

    } 
    public static MyGroupDataSingleton getInstance(){
        if(mInstance == null)
        {
            mInstance = new MyGroupDataSingleton();
        }
        return mInstance;
    }
    public boolean CheckForPrivateGroup(ReviewData mRevData2) {
		for(PrivateGroupData oData:m_arrPrivateGroups)
		{
			if(oData.mRevData.id.equals(mRevData2.id))
				return true;
		}
		return false;
	}
    public boolean CheckForMyPrivateGroup(ReviewData mRevData2) {
		for(PrivateGroupData oData:m_arrPrivateGroups)
		{
			if(oData.m_bMine)
			{
				if(oData.mRevData.id.equals(mRevData2.id))
					return true;
			}
		}
		return false;
	}
    public PrivateGroupData GetGroupData(ReviewData oRevData)
    {
    	for(PrivateGroupData oData:m_arrPrivateGroups)
    	{
    		if(oData.mRevData.id.equals(oRevData.id))
    			return oData;
    	}
    	return null;
    }
	public PrivateGroupData GetMyPrivateGroup(ReviewData mRevData) {
    	for(PrivateGroupData oData:m_arrPrivateGroups)
    	{
    		if(oData.mRevData.id.equals(mRevData.id))
    		{
    			if(oData.m_bMine)
    				return oData;
    		}
    	}
    	return null;
	}
	public PrivateGroupData GetFriendsPrivateGroup(ReviewData mRevData) {
    	for(PrivateGroupData oData:m_arrPrivateGroups)
    	{
    		if(oData.mRevData.id.equals(mRevData.id))
    			if(oData.m_bMine == false)
    				return oData;
    	}
    	return null;
	}
	public void RemoveGroup(String uniqueid) {
		for(PrivateGroupData oData:m_arrPrivateGroups)
		{
			if(oData.uniqueId.equals(uniqueid))
			{
				m_arrPrivateGroups.remove(oData);
				oData = null;
				return;
			}
		}
		
	}
}
