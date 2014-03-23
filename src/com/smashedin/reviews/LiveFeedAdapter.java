package com.smashedin.reviews;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.loopj.android.image.SmartImageTask.OnCompleteListener;
import com.loopj.android.image.SmartImageView;
import com.smashedin.smashed.CSmartImageView;
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
import android.widget.ProgressBar;
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

		final ViewHolder holder; 
		if(convertView == null)
		{
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.sms_row, parent, false);
			holder.message = (TextView) convertView.findViewById(R.id.message_text);
			holder.usertext = (TextView) convertView.findViewById(R.id.message_name);
			holder.oContainer = (LinearLayout) convertView.findViewById(R.id.containerStatus);
			holder.atplaceimage = (ImageView) convertView.findViewById(R.id.atplaceimage);
			holder.messageTimestamp = (TextView) convertView.findViewById(R.id.message_timestamp);
			holder.imgView = (SmartImageView) convertView.findViewById(R.id.imageohinstant);
			holder.oProgress = (ProgressBar) convertView.findViewById(R.id.progressohinstantLoad);
			convertView.setTag(holder);
		}
		else
			holder = (ViewHolder) convertView.getTag();
		android.view.ViewGroup.LayoutParams params = convertView.getLayoutParams();
		String type =  mLiveFeeds.get(position).type;
		if(type.equals("image"))
		{
			params.height = 500;//android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
			convertView.setLayoutParams(params);

			holder.message.setVisibility(View.GONE);
			holder.imgView.setVisibility(View.GONE);
			if(mLiveFeeds.get(position).ohurl.equals(""))
			{
				holder.oProgress.setVisibility(View.VISIBLE);
			}
			else
			{
				if(mLiveFeeds.get(position).ohurl.equals("NOTFOUND"))
				{
					holder.oProgress.setVisibility(View.GONE);
					holder.message.setVisibility(View.VISIBLE);
					message = "Your # was not found";
				}
				else
				{
	
					holder.imgView.setImageUrl(mLiveFeeds.get(position).ohurl,new OnCompleteListener() {
					
						@Override
						public void onComplete() {
							holder.oProgress.setVisibility(View.GONE); 
							holder.imgView.setVisibility(View.VISIBLE);
							
						}
					});
				}
			}
		}
		else
		{
			params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
			convertView.setLayoutParams(params);
			holder.message.setVisibility(View.VISIBLE);
			holder.imgView.setVisibility(View.GONE);
			holder.oProgress.setVisibility(View.GONE); 
			
		}
		try {
			String decodeMessage = URLDecoder.decode(message,"UTF-8");
			holder.message.setText(decodeMessage);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			holder.message.setText("");
			e.printStackTrace();
		}
		if(mLiveFeeds.get(position).atplace.equals("true") == true)
		{
			holder.atplaceimage.setVisibility(View.VISIBLE);
		}
		long ts = mLiveFeeds.get(position).timestamp;
		if(mLiveFeeds.get(position).updating == true)
		{
			holder.messageTimestamp.setText(">>");
		}
		else
		{
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeZone(TimeZone.getDefault());
			calendar.setTimeInMillis(ts * 1000);

			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			int minute = calendar.get(Calendar.MINUTE);	
			holder.messageTimestamp.setText(hour+":"+minute);
		}
		
		
		LayoutParams lp = (LayoutParams) holder.oContainer.getLayoutParams();
		
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
		return convertView;
	}
	private static class ViewHolder
	{
		TextView message;
		TextView usertext;
		LinearLayout oContainer;
		ImageView atplaceimage;
		TextView messageTimestamp;
		SmartImageView imgView;
		ProgressBar oProgress;
	}
    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
 
}
