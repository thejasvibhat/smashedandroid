package com.smashedin.smashed;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.loopj.android.image.SmartImageTask.OnCompleteListener;
import com.loopj.android.image.SmartImageView;

public class CSmartImageView extends RelativeLayout {
	private Context mContext;
	ProgressBar oProgress;
	RelativeLayout oMainView;
	public SmartImageView imageView;
	ProgressDialog mProgress;
	private int oWidth;
	public CSmartImageView(Context context) {
		
		super(context);
		mContext = context;
		imageView = new SmartImageView(context);
	}
	public void setImageURI(Uri uri)
	{
		this.addView(imageView);
		imageView.setImageURI(uri);
	}
	public void setImageResource(int resId)
	{
		this.addView(imageView);
		imageView.setImageResource(resId);
	}
	public void setScaleType(ScaleType scaleType)
	{
		imageView.setScaleType(scaleType);
	}
	public void LoadScaleView(CSmartImageView oView,int width,int height)
	{
		oMainView = oView;
		oWidth = width;
	}
	public void SetProgressBar(ProgressBar lProgress)
	{
		oProgress = lProgress;
	}

	public void setImageUrl(String url){
		
		oMainView.addView(imageView);
		oMainView.addView(oProgress);
		oProgress.setVisibility(View.VISIBLE);
		imageView.setImageUrl(url, new OnCompleteListener() {
			
			@Override
			public void onComplete() {
				oProgress.setVisibility(View.INVISIBLE);
				scaleImage();
				
			}
		});		
    }

	public void scaleImage()
    {
        // Get the ImageView and its bitmap
        Drawable drawing = imageView.getDrawable();
        if (drawing == null) {
            return; // Checking for null & return, as suggested in comments
        }
        Bitmap bitmap = ((BitmapDrawable)drawing).getBitmap();

        // Get current dimensions AND the desired bounding box
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int bounding = (int)(dipToPixels(mContext,oWidth));
       

        // Determine how much to scale: the dimension requiring less scaling is
        // closer to the its side. This way the image always stays inside your
        // bounding box AND either x/y axis touches it.  
        float xScale = ((float) bounding) / width;
        float yScale = ((float) bounding) / height;
        float scale = (xScale <= yScale) ? xScale : yScale;
    

        // Create a matrix for the scaling and add the scaling data
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Create a new bitmap and convert it to a format understood by the ImageView 
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        width = scaledBitmap.getWidth(); // re-use
        height = scaledBitmap.getHeight(); // re-use
        BitmapDrawable result = new  BitmapDrawable(mContext.getResources(), scaledBitmap);
               // Apply the scaled bitmap
        imageView.setImageDrawable(result);
        imageView.setLayoutParams(new LayoutParams(width, height));

    }
	
	public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

	public void SetProgress(ProgressDialog oProgress) {
		// TODO Auto-generated method stub
		mProgress = oProgress;
	}
}
