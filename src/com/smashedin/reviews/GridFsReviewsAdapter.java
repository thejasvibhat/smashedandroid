package com.smashedin.reviews;

import java.util.ArrayList;
import com.smashedin.smashedin.R;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
public class GridFsReviewsAdapter extends BaseAdapter {
    private Context mContext;
 
    // Keep all Images in array
    public  ArrayList<SmashedFsReviewsData> mReviews = new ArrayList<SmashedFsReviewsData>();
 
    // Constructor
    public GridFsReviewsAdapter(Context c){
        mContext = c;
    }
 
    @Override
    public int getCount() {
        return mReviews.size();
    }
 
    @Override
    public Object getItem(int position) {
        return mReviews.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return 0;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.fsreviewitem,null,false);
        }
        
        ((TextView)convertView.findViewById(R.id.fsrevname)).setText(mReviews.get(position).username);
        ((TextView)convertView.findViewById(R.id.fsrevtext)).setText(mReviews.get(position).review);
        SetRating(convertView, Integer.parseInt(mReviews.get(position).rating));
        return convertView;
    }
    private void SetRating(View view,int rating)
	{
		ImageView rate1 = (ImageView) view.findViewById(R.id.revrate1);
		ImageView rate2 = (ImageView) view.findViewById(R.id.revrate2);
		ImageView rate3 = (ImageView) view.findViewById(R.id.revrate3);
		ImageView rate4 = (ImageView) view.findViewById(R.id.revrate4);
		ImageView rate5 = (ImageView) view.findViewById(R.id.revrate5);

		if(rating == 1)
		{
			rate1.setImageResource(R.drawable.rateon);
			rate2.setImageResource(R.drawable.rate);
			rate3.setImageResource(R.drawable.rate);
			rate4.setImageResource(R.drawable.rate);
			rate5.setImageResource(R.drawable.rate);
		}
		else if(rating == 2)
		{
			rate1.setImageResource(R.drawable.rateon);
			rate2.setImageResource(R.drawable.rateon);
			rate3.setImageResource(R.drawable.rate);
			rate4.setImageResource(R.drawable.rate);
			rate5.setImageResource(R.drawable.rate);
		}
		else if(rating == 3)
		{
			rate1.setImageResource(R.drawable.rateon);
			rate2.setImageResource(R.drawable.rateon);
			rate3.setImageResource(R.drawable.rateon);
			rate4.setImageResource(R.drawable.rate);
			rate5.setImageResource(R.drawable.rate);
		}
		else if(rating == 4)
		{
			rate1.setImageResource(R.drawable.rateon);
			rate2.setImageResource(R.drawable.rateon);
			rate3.setImageResource(R.drawable.rateon);
			rate4.setImageResource(R.drawable.rateon);
			rate5.setImageResource(R.drawable.rate);
		}
		else if(rating == 5)
		{
			rate1.setImageResource(R.drawable.rateon);
			rate2.setImageResource(R.drawable.rateon);
			rate3.setImageResource(R.drawable.rateon);
			rate4.setImageResource(R.drawable.rateon);
			rate5.setImageResource(R.drawable.rateon);
		}
	}
    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
 
}
