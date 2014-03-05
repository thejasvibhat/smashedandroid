package com.smashedin.smashedin;


import com.smashedin.smashed.CSmartImageView;
import com.smashedin.smashedin.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.LinearLayout.LayoutParams;

public final class OhPagerFragment extends Fragment {
    private String m_url;
    public static OhPagerFragment newInstance(String content, String url) {
        OhPagerFragment fragment = new OhPagerFragment();
        fragment.m_url = url;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CSmartImageView imageView = new CSmartImageView(getActivity());
    	LayoutInflater li = LayoutInflater.from(getActivity());
    	ProgressBar oProgress = (ProgressBar) li.inflate(R.layout.progressbarlayout, null);
    	imageView.LoadScaleView(imageView,container.getWidth(),container.getHeight());
    	imageView.setScaleType(ScaleType.FIT_CENTER);
    	imageView.SetProgressBar(oProgress);
    	imageView.setImageUrl(m_url);

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        layout.setBackgroundColor(0x22EEEEEE);
        layout.setGravity(Gravity.CENTER);
        layout.addView(imageView);

        return layout;
    }
    
}
