package com.example.reviews;


import com.example.smashedin.R; 
import com.loopj.android.image.SmartImageView;

import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GridAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<OverheardData> catItems;
	
	public GridAdapter(Context context, ArrayList<OverheardData> RevItems){
		this.context = context;
		this.catItems = RevItems;
	}

	@Override
	public int getCount() {
		return catItems.size();
	}

	@Override
	public Object getItem(int position) {		
		return catItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	
	private class ViewHolder {
        SmartImageView imageView;
        TextView txtTitle;
        Bitmap bitmap;
    }
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
 
        LayoutInflater mInflater = (LayoutInflater)
            context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.reviews_list_item, null);
            holder = new ViewHolder();
            holder.txtTitle = (TextView) convertView.findViewById(R.id.m_oRevTitle);
            holder.imageView = (SmartImageView) convertView.findViewById(R.id.m_oRevIcon);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
 
     //   RowItem rowItem = (RowItem) getItem(position);
//       	holder.imageView.setImageUrl(catItems.get(position).icon);
 //       holder.txtTitle.setText(catItems.get(position).category);
        
 
        return convertView;
    }
	
}
