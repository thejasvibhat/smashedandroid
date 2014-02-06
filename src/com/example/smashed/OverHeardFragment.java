package com.example.smashed;

import android.app.Fragment;
import com.example.smashedin.*;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class OverHeardFragment extends Fragment {
	
	public OverHeardFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_overheard, container, false);
         
        return rootView;
    }
}
