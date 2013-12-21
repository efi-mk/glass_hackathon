package com.google.android.glass.powerpoint;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.AsyncTask;

public class UdpClientTask extends AsyncTask<Void, Void, Void> {
	
	private final String message;
	private final int port;
	
	

	/**
	 * Default constructor
	 */
	public UdpClientTask(int port, String message) {
		this.port = port;
		this.message = message;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		//activity.finish();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(Void... params) {
		// runUdpServer();
		runUdpClient(message);
		return null;
	}

	private void runUdpClient(String message)  {
	    String udpMsg = message;
	    DatagramSocket ds = null;
	    try {
	        ds = new DatagramSocket();
	        InetAddress serverAddr = InetAddress.getByName("192.168.2.104");
	        DatagramPacket dp;
	        dp = new DatagramPacket(udpMsg.getBytes(), udpMsg.length(), serverAddr, port);
	        ds.send(dp);
	    } catch (SocketException e) {
	        e.printStackTrace();
	    }catch (UnknownHostException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        if (ds != null) {
	            ds.close();
	        }
	    }
	}
	

}
