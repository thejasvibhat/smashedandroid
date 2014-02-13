package com.example.smashed;

import android.app.Fragment;
import android.graphics.Typeface;

import com.example.config.OverheardData;
import com.example.smashedin.*;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EachOhTextView extends Fragment {
	TextView oTextTop;
	TextView oTextBottom;
	private String m_strUrl;
	private Fragment m_cFragment;
	private int m_iPosition;
	private int oWidth;
	private int oHeight;
	public EachOhTextView(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_eachoh_view, container, false);
        FrameLayout m_eachFrameView = (FrameLayout) rootView.findViewById(R.id.m_eachFrameView);
        LayoutParams params = m_eachFrameView.getLayoutParams();
        params.height = oHeight;
        m_eachFrameView.setLayoutParams(params);
        CSmartImageView imageView = new CSmartImageView(getActivity());
        RelativeLayout m_eachRelView = (RelativeLayout) rootView.findViewById(R.id.m_eachRelView);
        m_eachRelView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
        m_eachRelView.addView(imageView);
        String url = m_strUrl;
        String[] arrUrl =  url.split("theju");
        if(arrUrl[0].equals("uri"))
        {
        	imageView.setImageURI(Uri.parse(arrUrl[1]));
        }
        else if(m_strUrl == "local")
        	imageView.setImageResource(R.drawable.ic_home);
        else
        {
        	imageView.LoadScaleView(imageView,oWidth);
        	
        	imageView.setImageUrl(m_strUrl);
        }
        oTextBottom = (TextView) rootView.findViewById(R.id.TextViewBottom);
        Typeface typeFace=Typeface.createFromAsset(getActivity().getAssets(),"impact.ttf");
        oTextBottom.setTypeface(typeFace);
        oTextTop = (TextView) rootView.findViewById(R.id.textViewTop);
        oTextTop.setTypeface(typeFace);
        FrameLayout.LayoutParams paramsTv  = (android.widget.FrameLayout.LayoutParams) oTextBottom.getLayoutParams();
        paramsTv.topMargin = (int) (oHeight - Singleton.getInstance().dipToPixels(getActivity(), 80));
        oTextBottom.setLayoutParams(paramsTv);
        EditText topOne = (EditText) rootView.findViewById(R.id.editText1);
        topOne.setFocusable(true);
        EditText bottomOne = (EditText) rootView.findViewById(R.id.editText2);
        topOne.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				oTextTop.setText(s);
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});

        topOne.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus == false)
				{
					oTextTop.setText(((EditText)v).getText());
				}
				
			}
		});
        
        bottomOne.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				oTextBottom.setText(s);
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});

        bottomOne.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus == false)
				{
					oTextBottom.setText(((EditText)v).getText());
				}
				
			}
		});        
        rootView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				MenuItem item = menu.findItem(R.id.saveoh);
				item.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						((CreateOverHeardFragment) m_cFragment).ReturnText(oTextTop.getText().toString(),oTextBottom.getText().toString(),m_iPosition);
						return false;
					}
				});
			}
		});
        setHasOptionsMenu(true);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
		return rootView;
    }
	@Override
	public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}
	@Override 
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.saveohtext:
	    	((CreateOverHeardFragment) m_cFragment).ReturnText(oTextTop.getText().toString(),oTextBottom.getText().toString(),m_iPosition);
	    	break;
	    }
		return true;
	}
	  @Override
	  public void onResume() {
		  Singleton.getInstance().m_bCameraMenuItem = true;
		  Singleton.getInstance().m_bGalleryMenuItem = true;
		  Singleton.getInstance().m_bRowAddMenuItem = true;
		  Singleton.getInstance().m_bSaveMenuItem = true;
		  Singleton.getInstance().m_bSaveOhTextMenuItem = false;
		  getActivity().invalidateOptionsMenu();
	     super.onResume();
	  }
	
	public void setArguments(Fragment m_createFragment, int position,
			OverheardData m_overheardData,int width,int height) {
		 m_cFragment = m_createFragment;
		 m_strUrl = m_overheardData.mThumbIds.get(position);
		 m_iPosition = position;
		oWidth = width;
		oHeight = height;
	}
}
