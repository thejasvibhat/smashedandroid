package com.example.reviews;

import com.example.smashedin.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public final class ReviewFragment extends Fragment {
    private static final String KEY_CONTENT = "TestFragment:Content";

    public static ReviewFragment newInstance(String content,ReviewData oRevData) {
        ReviewFragment fragment = new ReviewFragment();

        fragment.mContent = content;
        fragment.mRevData = oRevData;
        return fragment;
    }

    private String mContent = "???";
    private ReviewData mRevData = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
            mContent = savedInstanceState.getString(KEY_CONTENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	if(mRevData != null)
    	{
    		return GetView();
    	}
    	else
    	{
	        TextView text = new TextView(getActivity());
	        text.setGravity(Gravity.CENTER);
	        text.setText(mContent);
	        text.setTextSize(20 * getResources().getDisplayMetrics().density);
	        text.setPadding(20, 20, 20, 20);
	
	        LinearLayout layout = new LinearLayout(getActivity());
	        layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	        layout.setGravity(Gravity.CENTER);
	        layout.addView(text);
	
	        return layout;
    	}
    }
    
    private View GetView() {
		// TODO Auto-generated method stub
    	if(mContent == "INFO")
    	{
    		return GetViewForInfo();
    	}
		return null;
	}

	private View GetViewForInfo() {
		// TODO Auto-generated method stub
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.infoview,null,false);
        ((TextView)view.findViewById(R.id.revinfoname)).setText(mRevData.name);
        ((TextView)view.findViewById(R.id.revinfoaddress)).setText(mRevData.location.address);
        ((TextView)view.findViewById(R.id.revinfocitystate)).setText(mRevData.location.city+","+mRevData.location.state);
        ((TextView)view.findViewById(R.id.revinfodist)).setText(mRevData.location.distance);
		return view;
	}

	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, mContent);
    }
	public void UpdateRevData(ReviewData oRevData) {
        this.mRevData = oRevData;
		
	}
}
