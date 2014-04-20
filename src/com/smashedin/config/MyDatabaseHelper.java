package com.smashedin.config;

import java.util.ArrayList;

import com.smashedin.reviews.FacebookFriendsData;
import com.smashedin.reviews.PrivateGroupData;
import com.smashedin.reviews.ReviewData;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {
	private SQLiteDatabase db;
	private SQLiteDatabase fdb;
    private static final int DATABASE_VERSION = 1;
    private static final String DB_NAME = "smashedprivategroups.db";
    private static final String DB_FRIENDS_NAME = "smashedprivategroupfriends.db";
    private static final String TABLE_NAME = "privategroups";
    private static final String TABLE_FRIENDS_NAME = "friends";
    private String[] allColumns = {"uniqueid","bid","ismine"};
    private String[] allfColumns = {"id","name","avatar_url","issmashed"};

	public MyDatabaseHelper(Context context) {
		 super(context, DB_NAME, null, DATABASE_VERSION);
	        db = getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		arg0.execSQL("create table " + TABLE_NAME +
				  " (_id integer primary key autoincrement," + 
				  "uniqueid text not null,bid text not null,ismine text not null) ");
		arg0.execSQL("create table " + TABLE_FRIENDS_NAME +
				  " (_id integer primary key autoincrement," + 
				  "uniqueid text not null,id text not null,name text not null,avatar_url text not null,issmashed text not null) ");

	}
	public void Insert(PrivateGroupData oData)
	{
		db.execSQL("INSERT INTO privategroups('uniqueid', 'bid','ismine') values ('"
                + oData.uniqueId + "', '"
                + oData.mRevData.id +"', '"
                +String.valueOf(oData.m_bMine) +"')");
		for(FacebookFriendsData oFriends:oData.m_arrParticipants)
		{
			db.execSQL("INSERT INTO friends('uniqueid', 'id','name','avatar_url','issmashed') values ('"
	                + oData.uniqueId + "', '"
	                + oFriends.id +"', '"
	                + oFriends.name +"', '"
	                + oFriends.avatar_url +"', '"
	                + oFriends.issmashed +"')");
		}
	}
	public void Remove(PrivateGroupData oData)
	{
		db.execSQL("DELETE FROM privategroups where 'uniqueid'='"+oData.uniqueId+"'");
	}
	public ArrayList<PrivateGroupData> GetAllPrivateGroups()
	{
		ArrayList<PrivateGroupData> oList = new ArrayList<PrivateGroupData>();

	    Cursor cursor = db.query("privategroups",allColumns,null,null,null,null,null);
		cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	    	String uniqueid = cursor.getString(0);
	    	String bid = cursor.getString(1);
	    	String ismine = cursor.getString(2);
	    	PrivateGroupData oData = new PrivateGroupData();
	    	oData.uniqueId = uniqueid;
	    	oData.m_bMine = ismine.equals("true")?true:false;
	    	oData.mRevData = new ReviewData();
	    	oData.mRevData.id = bid;
	    	{
	    	    Cursor fcursor = db.query("friends",allfColumns,"uniqueid" + "=?",new String[] { uniqueid }, null, null, null, null);
	    		fcursor.moveToFirst();
	    		oData.m_arrParticipants = new ArrayList<FacebookFriendsData>();
	    		while (!fcursor.isAfterLast()) {
	    			FacebookFriendsData oFriendData = new FacebookFriendsData();
	    			oFriendData.id = fcursor.getString(0);
	    			oFriendData.name = fcursor.getString(1);
	    			oFriendData.avatar_url = fcursor.getString(2);
	    			oFriendData.issmashed = fcursor.getString(3);
	    			oData.m_arrParticipants.add(oFriendData);
	    			fcursor.moveToNext();
	    		}
	    		fcursor.close();
	    	}
	    	oList.add(oData);
	      cursor.moveToNext();
	    }
	    // make sure to close the cursor
	    cursor.close();
		return oList;
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
