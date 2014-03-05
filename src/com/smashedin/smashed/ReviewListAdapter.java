package com.smashedin.smashed;


import com.smashedin.smashedin.R; 
import com.loopj.android.image.SmartImageView;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ReviewListAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<ReviewItem> RevItems;
	
	public ReviewListAdapter(Context context, ArrayList<ReviewItem> RevItems){
		this.context = context;
		this.RevItems = RevItems;
	}

	@Override
	public int getCount() {
		return RevItems.size();
	}

	@Override
	public Object getItem(int position) {		
		return RevItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	
	private class ViewHolder {
        SmartImageView imageView;
        TextView txtTitle;
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
       	holder.imageView.setImageUrl(RevItems.get(position).getIcon());
        holder.txtTitle.setText(RevItems.get(position).getTitle());
        
 
        return convertView;
    }
	
}
