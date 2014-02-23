package com.example.smashed;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.example.reviews.ReviewFragment;
import com.example.smashedin.MainActivity;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.FragmentManager;

public class ResponseParser {
	private DocumentBuilderFactory m_oDocumentBuilderFactory = null;
	private DocumentBuilder m_oDocumentBuilder = null;
	private String m_strResponseData;
	private Activity oMainActivity;
	private Object m_Fragment = null;
	public ResponseParser(String responseData,Activity activity)
	{
		oMainActivity = activity;
		m_strResponseData = responseData;
		m_oDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
		m_oDocumentBuilderFactory.setIgnoringElementContentWhitespace(true);
		try 
		{
			m_oDocumentBuilder = m_oDocumentBuilderFactory.newDocumentBuilder();
		} 
		catch (ParserConfigurationException e) { }
	}
	 public void Parse()
	 {
		 ParseXMLTask oParseXMLTask = new ParseXMLTask();
		 oParseXMLTask.execute(m_strResponseData);
	 }
	 public void SetFragment(Fragment m_curFragment)
	 {
		 m_Fragment = m_curFragment;
	 }
	 private class ParseXMLTask extends AsyncTask<String, Void, Document> 
	 {
	        @Override
	        protected Document doInBackground(String... n_strXMLs) 
	        {

	            for(String strXML: n_strXMLs) 
	            {
	                try 
	                {
	                    ByteArrayInputStream oInputStream = new ByteArrayInputStream(strXML.getBytes());
	                    InputSource oInputSource = new InputSource(oInputStream);          
	                    Document oDocument  = m_oDocumentBuilder.parse(oInputSource);
	                    oInputStream = null;
	                    oInputStream = null;
	                    return oDocument;
	                } 
	                catch (SAXException e) { } 
	                catch (IOException e) { }
	                catch (NullPointerException e) { }
	                catch (DOMException e) { }
	            }
	            return null;
	        }

	        @Override
	        protected void onPostExecute(Document n_oDocument) 
	        {
	        	n_oDocument.normalize();
	        	if(m_Fragment != null)
	        	{
	        		if(m_Fragment instanceof GridOverheardSkeletonFragment)
	        			((GridOverheardSkeletonFragment)m_Fragment).ReturnResponseDocument(n_oDocument);
	        		else if(m_Fragment instanceof ReviewFragment)
	        			((ReviewFragment)m_Fragment).ReturnResponseDocument(n_oDocument);
	        		else
	        			((GridOverheardFragment)m_Fragment).ReturnResponseDocument(n_oDocument);
	        	}
	        	else
	        	{
	        		if(oMainActivity instanceof MainActivity)
	        			((MainActivity)oMainActivity).ReturnResponseDocument(n_oDocument);
	        		else
	        			((OverHeardActivity)oMainActivity).ReturnResponseDocument(n_oDocument);
	        	}
	        	
	        }
	    }
	public void SetFragment(android.support.v4.app.Fragment m_curFragment) {
		m_Fragment = m_curFragment;
		
	}

}
