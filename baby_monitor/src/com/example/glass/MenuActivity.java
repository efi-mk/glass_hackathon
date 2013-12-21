package com.example.glass;

import static java.text.MessageFormat.format;

import java.io.File;
import java.io.FileInputStream;
import java.text.MessageFormat;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

public class MenuActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nothing);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		openOptionsMenu();
		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection.
		switch (item.getItemId()) {
		case R.id.stop:
			stopService(new Intent(this, RetrieveImageService.class));
			return true;
		case R.id.share:
			AsyncTask<String, String, String> task = new AsyncTask<String, String, String>() {

				@Override
				protected String doInBackground(String... params) {
					Log.i(RetrieveImageService.TAG, "Saving image...");
					
					String url = format("{0}store", RetrieveImageService.SERVER_ADDRESS);
					File file = new File(RetrieveImageService.IMAGE_FILE_TO_SHARE);
					try {
					    HttpClient httpclient = new DefaultHttpClient();

					    HttpPost httppost = new HttpPost(url);

//					    InputStreamEntity reqEntity = new InputStreamEntity(new FileInputStream(file), -1);
					    FileEntity fileEntity = new FileEntity(file, "binary/octet-stream");
					    //reqEntity.setContentType("binary/octet-stream");
					    httppost.setEntity(fileEntity);
					    HttpResponse response = httpclient.execute(httppost);
					    Log.i(RetrieveImageService.TAG, "Store response " + response.getStatusLine().hashCode());
					} catch (Exception e) {
					    e.printStackTrace();
					} 
					
					return null;
				}

				@Override
				protected void onPostExecute(String result) {
					ProgressBar bar = (ProgressBar) findViewById(R.id.someProgressBar);
					bar.setVisibility(View.INVISIBLE);
					Log.i(RetrieveImageService.TAG, "Done saving image");
					// Nothing else to do, closing the Activity.
					finish();
				}

				@Override
				protected void onPreExecute() {
					ProgressBar bar = (ProgressBar) findViewById(R.id.someProgressBar);
					bar.setVisibility(View.VISIBLE);
				}
				
			};
			
			task.execute("");
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onOptionsMenuClosed(Menu menu) {
		// Nothing else to do, closing the Activity.
		finish();
	}
}
