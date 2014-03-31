package com.smashedin.reviews;

import java.util.ArrayList;

import com.loopj.android.image.SmartImageView;
import com.smashedin.smashedin.R;
import com.smashedin.smashed.CSmartImageView;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
 
public class FriendGroupsAdapter extends BaseAdapter {
    private Context mContext;
 
    // Keep all Images in array
 
    // Constructor
    public FriendGroupsAdapter(Context c){
        mContext = c;
    }
 
    @Override
    public int getCount() {
        return MyGroupDataSingleton.getInstance().m_arrPrivateGroups.size();
    }
 
    @Override
    public Object getItem(int position) {
        return MyGroupDataSingleton.getInstance().m_arrPrivateGroups.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return 0;
    }
	private class ViewHolder {
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	ViewHolder holder = null;
        LayoutInflater mInflater = (LayoutInflater)
                mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.reviews_list_item, null);
                holder = new ViewHolder();
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            LinearLayout oBack = (LinearLayout) convertView.findViewById(R.id.itemRevBack);
            PrivateGroupData oPrivData = MyGroupDataSingleton.getInstance().m_arrPrivateGroups.get(position);
            if(oPrivData.m_bMine == true)
            {
            	oBack.setBackgroundColor(0x30689154);
            }
            ReviewData oRevData = oPrivData.mRevData;
            ((TextView)convertView.findViewById(R.id.revname)).setText(oRevData.name);
            if(oPrivData.m_bMine)
            {
	            if(oPrivData.mRevData.grouplivefeedsmine.size() == 0)
	            	((TextView)convertView.findViewById(R.id.revadd)).setText("Group Created");
	            else
	            {
	            	LiveData oLive = oPrivData.mRevData.grouplivefeedsmine.get(oPrivData.mRevData.grouplivefeedsmine.size() - 1);
	            	((TextView)convertView.findViewById(R.id.revadd)).setText(oLive.username+":"+oLive.message);
	            }
            }
            else
            {
	            if(oPrivData.mRevData.grouplivefeedsfriends.size() == 0)
	            	((TextView)convertView.findViewById(R.id.revadd)).setText("Group Created");
	            else
	            {
	            	LiveData oLive = oPrivData.mRevData.grouplivefeedsfriends.get(oPrivData.mRevData.grouplivefeedsfriends.size() - 1);
	            	((TextView)convertView.findViewById(R.id.revadd)).setText(oLive.username+":"+oLive.message);
	            }

            }

            SmartImageView imgview = ((SmartImageView)convertView.findViewById(R.id.m_oRevIcon));
            imgview.setImageUrl(oRevData.photo);

            return convertView;
    }
    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
 
}
