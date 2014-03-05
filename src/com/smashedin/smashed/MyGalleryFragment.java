package com.smashedin.smashed;

import com.smashedin.smashedin.R;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class MyGalleryFragment extends Fragment {
	int count;
	int image_column_index;
	int actual_image_column_index;
	Cursor imagecursor;
	Fragment m_OhFragment;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);
        if(m_OhFragment == null)
        	m_OhFragment = new OverHeardFragment();
        return rootView;
    }
    @Override
	public void onResume() {
    	Singleton.getInstance().m_bShareMenuItem = true;
		  Singleton.getInstance().m_bSearchMenuItem = true;
		  getActivity().invalidateOptionsMenu();
    	super.onResume();
    	
    	init_phone_image_grid();
    }

    	
	private void init_phone_image_grid() {
        final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
         imagecursor = getActivity().getContentResolver().query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                columns, 
                MediaStore.Images.Media.DATA + " like ? ",
                new String[] {"%/Pictures/%"},  
                null);
        /*imagecursor = getActivity().managedQuery(
MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, img, null,
null, MediaStore.Images.Thumbnails.IMAGE_ID + "");*/
        image_column_index = imagecursor
.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);
        count = imagecursor.getCount();
        GridView imagegrid = (GridView) getActivity().findViewById(R.id.PhoneImageGrid);
        imagegrid.setAdapter(new ImageAdapter(getActivity().getApplicationContext()));
        imagegrid.setOnItemClickListener(new OnItemClickListener() {
              public void onItemClick(AdapterView parent, View v,
int position, long id) {
                    System.gc();
                    Cursor actualimagecursor = getActivity().getContentResolver().query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            columns, 
                            MediaStore.Images.Media.DATA + " like ? ",
                            new String[] {"%/Pictures/%"},  
                            null);
                    actual_image_column_index = actualimagecursor
.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    actualimagecursor.moveToPosition(position);
                    String i = actualimagecursor.getString(actual_image_column_index);
                    System.gc();
                    Bundle bundle = new Bundle();
                    bundle.putString("url", i);
                    m_OhFragment.setArguments(bundle);
        			FragmentManager fragmentManager = getFragmentManager();
        			fragmentManager.beginTransaction()
        					.add(R.id.frame_container_oh, m_OhFragment).addToBackStack( "new" ).commit();

//                    Intent intent = new Intent(getActivity().getApplicationContext(), ViewImage.class);
  //                  intent.putExtra("filename", i);
    //                startActivity(intent);
              }
        });
  }


  public class ImageAdapter extends BaseAdapter {
        private Context mContext;
        public ImageAdapter(Context c) {
              mContext = c;
        }
        public int getCount() {
              return count;
        }
        public Object getItem(int position) {
              return position;
        }
        public long getItemId(int position) {
              return position;
        }
        public View getView(int position,View convertView,ViewGroup parent) {
              System.gc();
              ImageView i = new ImageView(mContext.getApplicationContext());
            imagecursor.moveToPosition(position);
            int id = imagecursor.getInt(image_column_index);
            Uri uri = Uri.withAppendedPath( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Integer.toString(id) );
            String url = uri.toString();
            // Set the content of the image based on the image URI
            int originalImageId = Integer.parseInt(url.substring(url.lastIndexOf("/") + 1, url.length()));
            Bitmap b = MediaStore.Images.Thumbnails.getThumbnail(getActivity().getContentResolver(),
                            originalImageId, MediaStore.Images.Thumbnails.MINI_KIND, null);
            i.setImageBitmap(b);
            i.setLayoutParams(new GridView.LayoutParams(200, 200));
            i.setScaleType(ImageView.ScaleType.FIT_XY);
            return i;
        }
  	}
}
