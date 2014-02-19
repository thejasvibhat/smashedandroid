package com.example.smashed;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.loopj.android.image.SmartImageTask;
import com.loopj.android.image.SmartImageTask.OnCompleteListener;
import com.loopj.android.image.SmartImageView;
import com.loopj.android.image.WebImage;

public class CSmartImageView extends SmartImageView {
	private Context mContext;
	ImageView view;
	ProgressDialog mProgress;
	private int oWidth;
	public CSmartImageView(Context context) {
		
		super(context);
		mContext = context;
	}
	
	public void LoadScaleView(ImageView oView,int width,int height)
	{
		view = oView;
		oWidth = width;
	}
	
	@Override 
	public void setImageUrl(String url){
        super.setImageUrl(url, new OnCompleteListener() {
			
			@Override
			public void onComplete() {
				scaleImage();
				if(mProgress != null)
				{
					mProgress.dismiss();
				}
			}
		});
    }
	public void scaleImage()
    {
        // Get the ImageView and its bitmap
        Drawable drawing = view.getDrawable();
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
        BitmapDrawable result = new BitmapDrawable(scaledBitmap);
       

        // Apply the scaled bitmap
        view.setImageDrawable(result);

        // Now change ImageView's dimensions to match the scaled image
     //   LayoutParams params = view.getLayoutParams();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(new LayoutParams(width, height));
        if(params != null)
        {
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
        }
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
