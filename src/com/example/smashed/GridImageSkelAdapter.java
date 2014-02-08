package com.example.smashed;

import java.util.ArrayList;
import java.util.List;

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
 
public class GridImageSkelAdapter extends BaseAdapter {
    private Context mContext;
 
    // Keep all Images in array
    public  ArrayList<String> mThumbIds = new ArrayList<String>();
 
    // Constructor
    public GridImageSkelAdapter(Context c){
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
    	oRelLayout.setLayoutParams(new GridView.LayoutParams((int)dipToPixels(mContext,120),(int)dipToPixels(mContext,120)));
    	oRelLayout.setBackgroundColor(0xffcccccc);
    	oRelLayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
        CSmartImageView imageView = new CSmartImageView(mContext);
        //imageView.setImageResource(mThumbIds[position]);
        if(mThumbIds.get(position) == "local")
        	imageView.setImageResource(R.drawable.ic_home);
        else
        {
        	imageView.LoadScaleView(imageView,100);
        	imageView.setImageUrl(mThumbIds.get(position));
        }
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        //imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
        oRelLayout.addView(imageView);
        return oRelLayout;
    }
    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
 
}
