package com.smashedin.reviews;
import com.smashedin.smashedin.*;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;

public class ListGroupsFriends extends FragmentActivity  {
	private FriendGroupsAdapter gAdapter = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listgroups);
		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		//getActionBar().setHomeButtonEnabled(true);
		//getActionBar().setHomeAsUpIndicator(R.id.buttonLoginLogout);


    }
    @Override
    public void onResume() 
    {
    	SetGridItems((GridView) findViewById(R.id.reviewsGrid));
    	super.onResume();
    }
	private void SetGridItems(GridView gridView)
	{
		if(gAdapter == null)
			gAdapter = new FriendGroupsAdapter(this);
		
		gridView.setAdapter(gAdapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int location,
					long arg3) {
	            Intent intent = new Intent(getApplicationContext(), SmashedReview.class);
	            Bundle b = new Bundle();
	            b.putInt("position",location);
	            b.putString("bid", "groupdrawer");
	            intent.putExtras(b);
	            startActivity(intent);
				
			}
		});
	}

}
