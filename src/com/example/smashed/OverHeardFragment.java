package com.example.smashed;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.smashedin.*;
import com.loopj.android.image.SmartImageView;
import com.loopj.android.image.SmartImageTask.OnCompleteListener;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class OverHeardFragment extends Fragment {
	private View MainView;
	public OverHeardFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_overheard, container, false);
        MainView = rootView;
        String url = getArguments().getString("url");
        SmartImageView img = (SmartImageView) rootView.findViewById(R.id.ohview);
        ProgressBar oProgress = (ProgressBar) rootView.findViewById(R.id.progressImageGridOh);
        if(url.contains("http"))
        {
        	img.setVisibility(View.INVISIBLE);
        	oProgress.setVisibility(View.VISIBLE);
        	img.setImageUrl(url,new OnCompleteListener() {
				
				@Override
				public void onComplete() {
			        SmartImageView img = (SmartImageView) MainView.findViewById(R.id.ohview);
			        ProgressBar oProgress = (ProgressBar) MainView.findViewById(R.id.progressImageGridOh);

		        	img.setVisibility(View.VISIBLE);
		        	oProgress.setVisibility(View.INVISIBLE);

					// TODO Auto-generated method stub
				//	FrameLayout oL = (FrameLayout) getActivity().findViewById(R.id.spinView);
				//	oL.setVisibility(View.INVISIBLE);
				}
			});
        }
        else
        {
	        Bitmap mBitmap = BitmapFactory.decodeFile(url);
	        img.setImageBitmap(mBitmap);
	  //      FrameLayout oL = (FrameLayout) getActivity().findViewById(R.id.spinView);
	  //      if(oL != null)
	   //     	oL.setVisibility(View.INVISIBLE);
        }
        getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        setHasOptionsMenu(true);
        return rootView;
    }
	@Override
	public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override 
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.shareoh:
	    	ShareOverheard();
	    default:
	    	return super.onOptionsItemSelected(item);
	    }
		
	}
	  @Override
	  public void onResume() {
		  Singleton.getInstance().m_bShareMenuItem = false;
		  Singleton.getInstance().m_bSearchMenuItem = true;
		  getActivity().invalidateOptionsMenu();
	     super.onResume();
	  }
	  private void ShareOverheard()
		{
			View u = getActivity().findViewById(R.id.ohview);
		    
	        u.setDrawingCacheEnabled(true);                                                
	        int totalHeight = u.getHeight();
	        int totalWidth = u.getWidth();
	        u.layout(0, 0, totalWidth, totalHeight);    
	        u.buildDrawingCache(true);
	        Bitmap b = Bitmap.createBitmap(u.getDrawingCache());             
	        u.setDrawingCacheEnabled(false);
	        String localAbsoluteFilePath = saveImageLocally(b);
	        if (localAbsoluteFilePath!=null && localAbsoluteFilePath!="") {

	            Intent shareIntent = new Intent(Intent.ACTION_SEND);
	            Uri phototUri = Uri.parse(localAbsoluteFilePath);

	            File file = new File(phototUri.getPath());


	            if(file.exists()) {
	                // file create success

	            } else {
	                // file create fail
	            }
	            shareIntent.setData(phototUri);
	            shareIntent.setType("image/png");
	            shareIntent.putExtra(Intent.EXTRA_STREAM, phototUri);
	            startActivity(Intent.createChooser(Intent.createChooser(shareIntent, "Share Via"), "Share Image"));

	        }
	        
		}
		 private String saveImageLocally(Bitmap _bitmap) {
		        File outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		        File outputFile = null;
		        try {
		            outputFile = File.createTempFile("tmp", ".png", outputDir);
		        } catch (IOException e1) {
		            // handle exception
		        }

		        try {
		            FileOutputStream out = new FileOutputStream(outputFile);
		            _bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
		            out.close();

		        } catch (Exception e) {
		            // handle exception
		        }

		        return outputFile.getAbsolutePath();
		    }
}
