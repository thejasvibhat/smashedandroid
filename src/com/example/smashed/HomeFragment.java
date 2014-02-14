package com.example.smashed;

import android.app.Fragment;
import com.example.smashedin.*;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomeFragment extends Fragment {
	
	public HomeFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
         
        return rootView;
    }

	  @Override
	  public void onResume() {
		  Singleton.getInstance().m_bCameraMenuItem = false;
		  Singleton.getInstance().m_bGalleryMenuItem = false;
		  Singleton.getInstance().m_bRowAddMenuItem = true;
		  Singleton.getInstance().m_bSaveMenuItem = true;
		  Singleton.getInstance().m_bShareMenuItem = true;
		  getActivity().invalidateOptionsMenu();
	     super.onResume();
	  }
}
