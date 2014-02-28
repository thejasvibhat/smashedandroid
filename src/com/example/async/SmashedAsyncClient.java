package com.example.async;

import java.util.List;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;

import android.content.Context;

import com.example.smashed.GridOverheardSkeletonFragment.OnHeadlineSelectedListener;
import com.example.smashed.Singleton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

public class SmashedAsyncClient{
	private AsyncHttpClient m_oAsyncClient;
	private OnResponseListener oListenerCallback;
	public SmashedAsyncClient()
	{
		m_oAsyncClient = new AsyncHttpClient();
	}
	public interface OnResponseListener {
        public void OnResponse(String response);
        public void OnFailure();
    }
	public void SetPersistantStorage(Context oContext)
	{
		if(Singleton.getInstance().myCookieStore == null)
		{
			Singleton.getInstance().myCookieStore = new PersistentCookieStore(oContext);
		}
		m_oAsyncClient.setCookieStore(Singleton.getInstance().myCookieStore);
	}
	public void Attach(Object obj)
	{
		// This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            oListenerCallback = (OnResponseListener) obj;
        } catch (ClassCastException e) {
            throw new ClassCastException(obj.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
	}
	public void MakePostCall(String url,RequestParams params)
	{
		m_oAsyncClient.setTimeout(60000);
		m_oAsyncClient.setCookieStore(Singleton.getInstance().myCookieStore);
		m_oAsyncClient.post(url, params, new AsyncHttpResponseHandler(){
    		@Override
		    public void onSuccess(String response) {
    			oListenerCallback.OnResponse(response);
	        }
    		@Override
    		public void onFailure(Throwable error, String content)
    		{
    			oListenerCallback.OnFailure();
    		}		        
		 });
	}
	public void MakeCall(String url)
	{
		m_oAsyncClient.setCookieStore(Singleton.getInstance().myCookieStore);
//		List<Cookie> cookies = Singleton.getInstance().myCookieStore.getCookies();
//        String authVal = "";
//        for(Cookie oCookie:cookies)
//        {
//        	String name = oCookie.getName();
//        	String value = oCookie.getValue();
//        	if(name.equalsIgnoreCase("auth"))
//        	{
//        		value = '"'+value+'"';
//        		authVal = value;
//        		break;
//        	}
//        }
//        m_oAsyncClient.addHeader("Cookie", "auth="+authVal);
		
		m_oAsyncClient.get(url, new AsyncHttpResponseHandler(){
    		@Override
		    public void onSuccess(String response) {
    			oListenerCallback.OnResponse(response);
	        }
    		@Override
    		public void onFailure(Throwable error, String content)
    		{
    			oListenerCallback.OnFailure();
    		}		        
		 });
	}
}
