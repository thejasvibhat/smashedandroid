package com.example.smashed;

import java.util.ArrayList;
import java.util.List;

import com.example.smashedin.*;
import com.loopj.android.image.SmartImageTask.OnCompleteListener;
import com.loopj.android.image.SmartImageView;

import android.app.Activity;
import android.app.Fragment;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
 
public class GridImageAdapter extends BaseAdapter {
    private Context mContext;
    private Fragment m_oCreateFragment;
    private static int WIDTH = 350;
    private static int HEIGHT = 400;
	int oWidth;
	int oHeight;

    // Keep all Images in array
    public  ArrayList<String> mThumbIds = new ArrayList<String>();
 
    // Constructor
    public GridImageAdapter(Context c){
        mContext = c;
    }
 
    public void AddArgument(Fragment m_createFragment) {
    	m_oCreateFragment = m_createFragment;
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
    	oWidth = WIDTH;
    	oHeight = HEIGHT;
    	if(getCount() > 1)
    		oHeight = HEIGHT/2;
    	FrameLayout oFrameLayout = new FrameLayout(mContext);
    	oFrameLayout.setLayoutParams(new GridView.LayoutParams((int)dipToPixels(mContext,oWidth),(int)dipToPixels(mContext,oHeight)));
    	oFrameLayout.setBackgroundColor(0xffBBBBBB);
    	RelativeLayout oRelLayout = new RelativeLayout(mContext); 
    	oRelLayout.setLayoutParams(new GridView.LayoutParams((int)dipToPixels(mContext,oWidth),(int)dipToPixels(mContext,oHeight)));
    	oRelLayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
    	oFrameLayout.addView(oRelLayout);
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
        ImageView imageView2 = new ImageView(mContext);
        imageView2.setImageResource(R.drawable.edit);
        imageView2.setTag(new Integer(position));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,Gravity.RIGHT | Gravity.TOP);
        imageView2.setLayoutParams(params);
        imageView2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Integer tag = (Integer) v.getTag();
				((CreateOverHeardFragment)m_oCreateFragment).AddOverheardText(tag.intValue(),(int)dipToPixels(mContext,oWidth),(int)dipToPixels(mContext, oHeight));
			}
		});
        oFrameLayout.addView(imageView2);
        return oFrameLayout;
    }
    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
    
}
