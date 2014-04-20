package com.smashedin.reviews;

import java.util.ArrayList;

import com.smashedin.config.MyDatabaseHelper;


public class MyGroupDataSingleton {
    private static MyGroupDataSingleton mInstance = null;
    public ArrayList<PrivateGroupData> m_arrPrivateGroups = new ArrayList<PrivateGroupData>();
    public PrivateGroupData acceptGroupData = new PrivateGroupData();
    public MyDatabaseHelper dbHelper;
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
	public ArrayList<String> Repopulate()
	{
		ArrayList<String> bids = new ArrayList<String>();
		if(m_arrPrivateGroups.size() == 0)
		{
			m_arrPrivateGroups = dbHelper.GetAllPrivateGroups();
			for(PrivateGroupData oData:m_arrPrivateGroups)
			{
				bids.add(oData.mRevData.id);
			}
		}
		return bids;
	}
	public void RefreshPlace(ReviewData oRevdata)
	{
		for(PrivateGroupData oData:m_arrPrivateGroups)
		{
			if(oRevdata.id.equals(oData.mRevData.id))
			{
				oData.mRevData = oRevdata;
				return;
			}
		}
	}
	public void RemoveGroup(String uniqueid) {
		for(PrivateGroupData oData:m_arrPrivateGroups)
		{
			if(oData.uniqueId.equals(uniqueid))
			{
				m_arrPrivateGroups.remove(oData);
				dbHelper.Remove(oData);
				oData = null;
				return;
			}
		}
		
	}
	public void StoreGroup(String uniqueid) {
		for(PrivateGroupData oData:m_arrPrivateGroups)
		{
			if(oData.uniqueId.equals(uniqueid))
			{
				dbHelper.Insert(oData);
				return;
			}
		}
		
	}
}
