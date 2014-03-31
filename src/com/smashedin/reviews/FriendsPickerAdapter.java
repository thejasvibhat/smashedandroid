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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
public class FriendsPickerAdapter extends BaseAdapter implements Filterable{
    private Context mContext;
 
    // Keep all Images in array
    public  ArrayList<FacebookFriendsData> mFriends = new ArrayList<FacebookFriendsData>();
    public ArrayList<FacebookFriendsData> mOriginalValues = null;
    // Constructor
    public FriendsPickerAdapter(Context c){
        mContext = c;
    }
 
    @Override
    public int getCount() {
        return mFriends.size();
    }
 
    @Override
    public Object getItem(int position) {
        return mFriends.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return 0;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       
    	  if (convertView == null) {
              convertView = LayoutInflater.from(mContext).inflate(R.layout.eachfriendview,null,false);
          }
          LinearLayout oBack = (LinearLayout) convertView.findViewById(R.id.itemFriendsBack); 
          if(position % 2 == 0)
          {
          	oBack.setBackgroundColor(0xffffffff);

          }
          else
          {
          	oBack.setBackgroundColor(0xfff4f4f4);
          }
          if(mFriends.get(position).selected == true)
          {
        	  oBack.setBackgroundColor(0x30689154);
          }
          ((TextView)convertView.findViewById(R.id.friendname)).setText(mFriends.get(position).name);
          SmartImageView imgview = ((SmartImageView)convertView.findViewById(R.id.m_oAvatarIcon));
          if(mFriends.get(position).avatar_url.equals("") == false)
        	  imgview.setImageUrl(mFriends.get(position).avatar_url);
          else
        	  imgview.setImageResource(R.drawable.slogomed);
          return convertView;
		
	}
	private static class ViewHolder
	{
		
	}
    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

	@Override
	public Filter getFilter() {
		 Filter filter = new Filter() {

             @SuppressWarnings("unchecked")
             @Override
             protected void publishResults(CharSequence constraint,FilterResults results) {

                 mFriends = (ArrayList<FacebookFriendsData>) results.values; // has the filtered values
                 notifyDataSetChanged();  // notifies the data with new filtered values
             }

             @Override
             protected FilterResults performFiltering(CharSequence constraint) {
                 FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                 ArrayList<FacebookFriendsData> FilteredArrList = new ArrayList<FacebookFriendsData>();

                 if (mOriginalValues == null) {
                     mOriginalValues = new ArrayList<FacebookFriendsData>(mFriends); // saves the original data in mOriginalValues
                 }

                 /********
                  * 
                  *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                  *  else does the Filtering and returns FilteredArrList(Filtered)  
                  *
                  ********/
                 if (constraint == null || constraint.length() == 0) {

                     // set the Original result to return  
                     results.count = mOriginalValues.size();
                     results.values = mOriginalValues;
                 } else {
                     constraint = constraint.toString().toLowerCase();
                     for (int i = 0; i < mOriginalValues.size(); i++) {
                         FacebookFriendsData data = mOriginalValues.get(i);
                         if (data.name.toLowerCase().startsWith(constraint.toString())) {
                             FilteredArrList.add(data);
                         }
                     }
                     // set the Filtered result to return
                     results.count = FilteredArrList.size();
                     results.values = FilteredArrList;
                 }
                 return results;
             }
         };
         return filter;
	}
 
}
