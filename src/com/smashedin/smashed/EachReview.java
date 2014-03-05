package com.smashedin.smashed;

import com.smashedin.smashedin.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class EachReview extends Fragment {
	String url;
	public EachReview(){}
	public void SetUrl(String lurl){
		this.url = lurl;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_bar_view, container, false);
        ((WebView) rootView).loadUrl(url);

        return rootView;
    }
	
}
