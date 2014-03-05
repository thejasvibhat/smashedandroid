package com.smashedin.reviews;
import com.smashedin.smashedin.R; 
import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


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
    }
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
 
        LayoutInflater mInflater = (LayoutInflater)
            context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.reviews_list_item, null);
            holder = new ViewHolder();
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
