package com.google.android.glass.powerpoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.text.format.Time;
import android.util.Log;

public class HttpUtils {
	public static String auth = "&user=admin&password=admin";
	public static String host = "http://192.168.2.108:81/moveptz.xml?";
	public static String LEFT_URL = host + "dir=left"+auth;
	public static String RIGHT_URL = host + "dir=right" + auth;
	public static String UP_URL = host + "dir=up" + auth;
	public static String DOWN_URL = host + "dir=down" + auth;
	public static String STOP_URL = host + "dir=stop" + auth;

	
	static void executeRequest(String url){
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url); 
		HttpGet httpStop = new HttpGet(STOP_URL); 
		Time now = new Time();
	    // Execute the request
	    HttpResponse response;
	    try {
	    	now.setToNow();
	    	Log.i("GLASS","time="+now.toMillis(true));
	        response = httpclient.execute(httpget);
	        now.setToNow();
	        Log.i("GLASS","time="+now.toMillis(true));
	        // Examine the response status
	        Log.i("GLASS",response.getStatusLine().toString());
	        //httpclient.execute(httpStop);
	        // Get hold of the response entity
	        HttpEntity entity = response.getEntity();
	        // If the response does not enclose an entity, there is no need
	        // to worry about connection release

	        if (entity != null) {

	            // A Simple JSON Response Read
	            InputStream instream = entity.getContent();
	            String result= convertStreamToString(instream);
	            // now you have the string representation of the HTML request
	            instream.close();
	        }


	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	}
	
	private static String convertStreamToString(InputStream is) {
	    /*
	     * To convert the InputStream to String we use the BufferedReader.readLine()
	     * method. We iterate until the BufferedReader return null which means
	     * there's no more data to read. Each line will appended to a StringBuilder
	     * and returned as String.
	     */
	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();

	    String line = null;
	    try {
	        while ((line = reader.readLine()) != null) {
	            sb.append(line + "\n");
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            is.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    return sb.toString();
	}
}
