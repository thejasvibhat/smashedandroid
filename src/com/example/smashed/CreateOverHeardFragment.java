package com.example.smashed;
import com.example.smashed.NumberPicker.OnChangedListener;
import com.example.smashedin.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.database.Cursor;

import com.example.smashedin.*;
import com.loopj.android.image.SmartImageView;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CreateOverHeardFragment extends Fragment {
	public Fragment m_createFragment;
	private GridImageAdapter gAdapter = null;
	public CreateOverHeardFragment(){
		m_createFragment = this;
	}
	private static final int SELECT_PICTURE = 1;
	private String selectedImagePath;
	private SmartImageView selectedImage;
	private int selectedPosition = 0;
	private Fragment gridSkelView = null;
	String path = "";
	private Dialog dialog;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		Singleton.getInstance().m_bCameraMenuItem = true;
		Singleton.getInstance().m_bGalleryMenuItem = true;
		Singleton.getInstance().m_bRowAddMenuItem = false;
		Singleton.getInstance().m_bSaveMenuItem = false;
		getActivity().invalidateOptionsMenu();
        View rootView = inflater.inflate(R.layout.fragment_createoverheard, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.grid_view);
        // Instance of ImageAdapter Class
        if(gAdapter == null)
        	gAdapter = new GridImageAdapter(getActivity());
		if(gAdapter.mThumbIds.size() == 0)
			gAdapter.mThumbIds.add("local");
	    gridView.setAdapter(gAdapter);
	    gridView.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        	selectedImage = (SmartImageView)(((RelativeLayout)v).getChildAt(0));
	        	selectedPosition = position;
	        	if(gridSkelView == null)
	        	{
	        		gridSkelView = new GridOverheardSkeletonFragment();	    
	        		((GridOverheardSkeletonFragment) gridSkelView).AddArgument(m_createFragment);
	        	}
	        	Singleton.getInstance().m_bCameraMenuItem = false;
				Singleton.getInstance().m_bGalleryMenuItem = false;
				Singleton.getInstance().m_bRowAddMenuItem = true;
				Singleton.getInstance().m_bSaveMenuItem = true;
				getActivity().invalidateOptionsMenu();
	        	FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, gridSkelView).addToBackStack( "tag" ).commit();
				
            
	        }
	    });
	    
	    SetSpinner(rootView);
        return rootView;
    }
	private void SetSpinner(View view)
	{
		String[] data = new String[gAdapter.mThumbIds.size()];
		for(int i = 0; i < gAdapter.mThumbIds.size();i++)
		{
			data[i] = String.valueOf(i + 1);
		}
		 
		Spinner spinner = (Spinner) view.findViewById(R.id.spinner1);
		
       // adapter.setDropDownViewResource(android.R.layout.simple_selectable_list_item);
        
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),android.R.layout.simple_spinner_item, data) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(0xff00ff00);

                return textView;
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(0xff00ff00);

                return textView;
            }

            
        };

        adapter.setDropDownViewResource(android.R.layout.simple_list_item_multiple_choice);
        spinner.setAdapter(adapter);

	}
	public void AddFromCamera()
	{
		takePhoto();
	}
	public void AddFromGallery()
	{
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
      	startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE);
	}
	public void RefreshRows(int numRows)
	{
		gAdapter.mThumbIds.clear();
		for(int i=0; i < numRows;i++)
		{
			gAdapter.mThumbIds.add("local");
		}
		GridView gridView = (GridView) getActivity().findViewById(R.id.grid_view);
        // Instance of ImageAdapter Class
	    gridView.setAdapter(gAdapter);
	    SetSpinner(getView());
	}
	public void AddRowDialog()
	{
		if(dialog == null)
		{
			dialog = new Dialog(getActivity());
	
			com.example.smashed.NumberPicker oNumPicker = new com.example.smashed.NumberPicker(getActivity().getApplicationContext());
			oNumPicker.setOnChangeListener(new OnChangedListener() {
				
				@Override
				public void onChanged(com.example.smashed.NumberPicker picker, int oldVal,
						int newVal) {
					 RefreshRows(newVal);
					
				}
			});
			oNumPicker.mStart = 1;
			oNumPicker.mEnd = 4;
			oNumPicker.mCurrent = 1;
			dialog.setContentView(oNumPicker);
			dialog.setTitle("Add Rows");
			dialog.setCancelable(true);
		}
		dialog.show();
		
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1) {
        	Uri selectedImageUri;
            if (requestCode == SELECT_PICTURE) {
	        	FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, m_createFragment).commit();
				selectedImageUri = data.getData();
            }
            else
            {
            	selectedImageUri = Uri.parse(path);
            }
            if(gAdapter.mThumbIds.get(selectedPosition) != null)
    			gAdapter.mThumbIds.remove(selectedPosition);
    		gAdapter.mThumbIds.add(selectedPosition,"uritheju"+selectedImageUri.toString());

        	FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, m_createFragment).commit();

        }
    }
	 public void takePhoto()
    {
         Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
         File folder = new File(Environment.getExternalStorageDirectory() + "/LoadImg");

         if(!folder.exists())
         {
             folder.mkdir();
         }        
         final Calendar c = Calendar.getInstance();
         String new_Date= c.get(Calendar.DAY_OF_MONTH)+"-"+((c.get(Calendar.MONTH))+1)   +"-"+c.get(Calendar.YEAR) +" " + c.get(Calendar.HOUR) + "-" + c.get(Calendar.MINUTE)+ "-"+ c.get(Calendar.SECOND);
         path=String.format(Environment.getExternalStorageDirectory() +"/LoadImg/%s.png","LoadImg("+new_Date+")");
         File photo = new File(path);
         intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(photo));
         startActivityForResult(intent, 2);
    }
	    
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, filePathColumn, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
	public void UpdateSkel(String id, String url) {
		if(gAdapter.mThumbIds.get(selectedPosition) != null)
			gAdapter.mThumbIds.remove(selectedPosition);
		gAdapter.mThumbIds.add(selectedPosition,url);
		
	}
}
