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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
 
public class GridReviewsAdapter extends BaseAdapter {
    private Context mContext;
 
    // Keep all Images in array
    public  ArrayList<ReviewData> FsqVenues = new ArrayList<ReviewData>();
 
    // Constructor
    public GridReviewsAdapter(Context c){
        mContext = c;
    }
 
    @Override
    public int getCount() {
        return FsqVenues.size();
    }
 
    @Override
    public Object getItem(int position) {
        return FsqVenues.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return 0;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.revieweachitem,null,false);
        }
        String distance = FsqVenues.get(position).location.distance;
        
        ((TextView)convertView.findViewById(R.id.revname)).setText(FsqVenues.get(position).name);
        ((TextView)convertView.findViewById(R.id.revadd)).setText(FsqVenues.get(position).location.address);
        ((TextView)convertView.findViewById(R.id.revdist)).setText(distance);
        SmartImageView imgview = ((SmartImageView)convertView.findViewById(R.id.m_oRevIcon));
        imgview.setImageUrl(FsqVenues.get(position).photo);
        return convertView;
    }
    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
 
}
