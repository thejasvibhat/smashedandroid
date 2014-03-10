package com.smashedin.reviews;

import java.util.ArrayList;

import com.smashedin.smashed.Singleton;
import com.smashedin.smashedin.R;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
public class LiveFeedAdapter extends BaseAdapter {
    private Context mContext;
 
    // Keep all Images in array
    public  ArrayList<LiveData> mLiveFeeds = new ArrayList<LiveData>();
 
    // Constructor
    public LiveFeedAdapter(Context c){
        mContext = c;
    }
 
    @Override
    public int getCount() {
        return mLiveFeeds.size();
    }
 
    @Override
    public Object getItem(int position) {
        return mLiveFeeds.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return 0;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       
       String message = mLiveFeeds.get(position).message;

		ViewHolder holder; 
		if(convertView == null)
		{
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.sms_row, parent, false);
			holder.message = (TextView) convertView.findViewById(R.id.message_text);
			holder.usertext = (TextView) convertView.findViewById(R.id.message_name);
			holder.oContainer = (LinearLayout) convertView.findViewById(R.id.containerStatus);
			holder.oDivider = (View) convertView.findViewById(R.id.dividername);
			convertView.setTag(holder);
		}
		else
			holder = (ViewHolder) convertView.getTag();
		
		holder.message.setText(message);
		
		LayoutParams lp = (LayoutParams) holder.oContainer.getLayoutParams();
		LayoutParams lpiewV = (LayoutParams) holder.oDivider.getLayoutParams();
		
		
		//Check whether message is mine to show green background and align to right
		if(mLiveFeeds.get(position).mine == true)
		{
			holder.oContainer.setBackgroundResource(R.drawable.calloutself);
			lp.gravity = Gravity.RIGHT;
		}
		//If not mine then it is from sender to show orange background and align to left
		else
		{
			holder.oContainer.setBackgroundResource(R.drawable.calloutthem);
			lp.gravity = Gravity.LEFT;
		}
		holder.oContainer.setPadding(30,20,30,20);
		holder.oContainer.setLayoutParams(lp);
		holder.message.setTextColor(0xFF000000);	
		holder.usertext.setText(mLiveFeeds.get(position).username);
		lpiewV.width = holder.usertext.getWidth();
		holder.oDivider.setLayoutParams(lpiewV);
		return convertView;
	}
	private static class ViewHolder
	{
		TextView message;
		TextView usertext;
		LinearLayout oContainer;
		View oDivider;
	}
    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
 
}
