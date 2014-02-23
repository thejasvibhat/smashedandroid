package com.example.reviews;

import java.util.ArrayList;
import java.util.List;

import com.example.smashed.CSmartImageView;
import com.example.smashedin.*;
import com.loopj.android.image.SmartImageView;

import android.content.Context;

import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
 
public class GridOverheardReviewAdapter extends BaseAdapter {
    private Context mContext;
 
    // Keep all Images in array
    public  ArrayList<String> mThumbIds = new ArrayList<String>();
 
    // Constructor
    public GridOverheardReviewAdapter(Context c){
        mContext = c;
    }
 
    @Override
    public int getCount() {
        return mThumbIds.size();
    }
 
    @Override
    public Object getItem(int position) {
        return mThumbIds.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return 0;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	RelativeLayout oRelLayout = new RelativeLayout(mContext);
    	oRelLayout.setLayoutParams(new GridView.LayoutParams((int)dipToPixels(mContext,200),(int)dipToPixels(mContext,200)));
    	oRelLayout.setBackgroundColor(0xffcccccc);
    	oRelLayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
        SmartImageView imageView = new SmartImageView(mContext);
        imageView.setImageUrl(mThumbIds.get(position));
        oRelLayout.addView(imageView);
        return oRelLayout;
    }
    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
 
}
