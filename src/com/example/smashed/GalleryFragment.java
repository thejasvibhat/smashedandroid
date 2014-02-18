package com.example.smashed;
import com.example.smashedin.*;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GalleryFragment extends Fragment {
	Fragment m_OhFragment;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        if(m_OhFragment == null)
        	m_OhFragment = new OverHeardFragment();

        return rootView;
    }
	@Override
	public void onResume() {
    	super.onResume();
    }

}
