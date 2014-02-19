package com.example.smashed;

import android.app.Fragment;
import com.example.smashedin.*;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomeFragment extends android.support.v4.app.Fragment {
	
	public HomeFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
         
        return rootView;
    }

	  @Override
	  public void onResume() {
		  if(Singleton.getInstance().m_oType == "home")
		  {
			  Singleton.getInstance().ClearAllOptionMenus();
			  Singleton.getInstance().m_bCameraMenuItem = false;
			  Singleton.getInstance().m_bGalleryMenuItem = false;
			  getActivity().invalidateOptionsMenu();
		  }
	     super.onResume();
	  }
}
