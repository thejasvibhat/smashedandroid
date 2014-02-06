package com.example.smashed;

import java.util.ArrayList;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;

import com.example.smashedin.*;
import com.loopj.android.image.SmartImageView;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class GridOverheardSkeletonFragment extends Fragment {
	public ArrayList<String> m_strSkeletonUrls = new ArrayList<String>();
	public ArrayList<String> m_strSkeletonIds = new ArrayList<String>();
	private Fragment m_createFragment;
	public GridOverheardSkeletonFragment(){}
	public void AddArgument(Fragment oCreateFragment){
        m_curFragment = this;
        m_createFragment = oCreateFragment;
	}
	private Fragment m_curFragment = null;
	private GridImageSkelAdapter gAdapter = null;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
        View rootView = inflater.inflate(R.layout.fragment_skelview, container, false);
        if(m_strSkeletonIds.size() != 0)
        {
        	SetGridItems((GridView) rootView.findViewById(R.id.grid_view_skels));
        }
        else
        	GetSkeletonData();
        return rootView;
    }
	public void ReturnResponseDocument(Document n_oDocument)
	{
		
		NodeList skelThumbs = n_oDocument.getElementsByTagName("thumburl");
		NodeList skelUrls = n_oDocument.getElementsByTagName("url");
		NodeList skelIds = n_oDocument.getElementsByTagName("id");
		if(gAdapter == null)
			gAdapter = new GridImageSkelAdapter(getActivity());
		for(int i=0 ; i < skelThumbs.getLength(); i++)
		{
			Node thumburl = skelThumbs.item(i);
			gAdapter.mThumbIds.add(thumburl.getTextContent());
			Node url = skelUrls.item(i);
			Node id = skelIds.item(i);
			m_strSkeletonUrls.add(url.getTextContent());
			m_strSkeletonIds.add(id.getTextContent());
		}
		SetGridItems((GridView) getView().findViewById(R.id.grid_view_skels));

	}
	private void SetGridItems(GridView gridView)
	{
		
	    gridView.setAdapter(gAdapter);
	    gridView.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        	((CreateOverHeardFragment)m_createFragment).UpdateSkel(m_strSkeletonIds.get(position),m_strSkeletonUrls.get(position));
	        	FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, m_createFragment).commit();
	        }
	    });
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1) {
        	
        }
    }
	private void GetSkeletonData()
	{
		Toast.makeText(getActivity(),
                "Please wait, connecting to server.",
                Toast.LENGTH_SHORT).show();


        // Create Inner Thread Class
        Thread background = new Thread(new Runnable() {
             
            private final HttpClient Client = new DefaultHttpClient();
            private String URL = "http://www.smashed.in/api/oh/skel-list?offset=0&limit=100";
             
            // After call for background.start this run method call
            public void run() {
                try {

                    String SetServerString = "";
                    HttpGet httpget = new HttpGet(URL);
                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
                    SetServerString = Client.execute(httpget, responseHandler);
                    threadMsg(SetServerString);

                } catch (Throwable t) {
                    // just end the background thread
                    Log.i("Animation", "Thread  exception " + t);
                }
            }

            private void threadMsg(String msg) {

                if (!msg.equals(null) && !msg.equals("")) {
                    Message msgObj = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("message", msg);
                    msgObj.setData(b);
                    handler.sendMessage(msgObj);
                }
            }

            // Define the Handler that receives messages from the thread and update the progress
            private final Handler handler = new Handler() {

                public void handleMessage(Message msg) {
                    String aResponse = msg.getData().getString("message");

                    if ((null != aResponse)) {
                    	ResponseParser oParser = new ResponseParser(aResponse,getActivity());
                    	oParser.SetFragment(m_curFragment);
                    	oParser.Parse();
                    }
                    else
                    {

                            // ALERT MESSAGE
                            Toast.makeText(
                                    getActivity(),
                                    "Not Got Response From Server.",
                                    Toast.LENGTH_SHORT).show();
                    }   

                }
            };

        });
        // Start Thread
        background.start();  //After call start method thread called run Methods
	}
}
