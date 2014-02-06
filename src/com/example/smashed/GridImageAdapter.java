package com.example.smashed;

import java.util.ArrayList;
import java.util.List;

import com.example.smashedin.*;
import com.loopj.android.image.SmartImageTask.OnCompleteListener;
import com.loopj.android.image.SmartImageView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
 
public class GridImageAdapter extends BaseAdapter {
    private Context mContext;
    private static int WIDTH = 350;
    private static int HEIGHT = 400;
    // Keep all Images in array
    public  ArrayList<String> mThumbIds = new ArrayList<String>();
 
    // Constructor
    public GridImageAdapter(Context c){
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
    	int oWidth = WIDTH;
    	int oHeight = HEIGHT;
    	if(getCount() > 1)
    		oHeight = HEIGHT/2;
    	RelativeLayout oRelLayout = new RelativeLayout(mContext);
    	oRelLayout.setLayoutParams(new GridView.LayoutParams((int)dipToPixels(mContext,oWidth),(int)dipToPixels(mContext,oHeight)));
    	oRelLayout.setBackgroundColor(0xffBBBBBB);
    	oRelLayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
        CSmartImageView imageView = new CSmartImageView(mContext);
        //imageView.setImageResource(mThumbIds[position]);
        String url = mThumbIds.get(position);
        String[] arrUrl =  url.split("theju");
        if(arrUrl[0].equals("uri"))
        {
        	imageView.setImageURI(Uri.parse(arrUrl[1]));
        }
        else if(mThumbIds.get(position) == "local")
        	imageView.setImageResource(R.drawable.ic_home);
        else
        {
        	imageView.LoadScaleView(imageView,oWidth);
        	imageView.setImageUrl(mThumbIds.get(position));
        }
        
        //imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        //imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
        oRelLayout.addView(imageView);
        return oRelLayout;
    }
    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
    
}
