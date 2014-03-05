package com.smashedin.smashed;

import com.smashedin.smashedin.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Reviews extends Fragment {
	
	public Reviews(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_reviews, container, false);
         
        return rootView;
    }
}
