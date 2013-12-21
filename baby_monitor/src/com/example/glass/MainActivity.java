package com.example.glass;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final ImageView img = (ImageView) findViewById(R.id.imgFromCamera);
		new Thread(new Runnable() {

			private InputStream openStream;
			private Bitmap decodeByteArray;

			@Override
			public void run() {
				URL url;
				
				while (true) {
					long mili = System.currentTimeMillis();
					try {
						url = new URL("http://192.168.2.105:8080/");
						HttpURLConnection connection = (HttpURLConnection) url
								.openConnection();

						InputStream is = connection.getInputStream();

						decodeByteArray = BitmapFactory.decodeStream(is);
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					runOnUiThread(new Runnable() {
						public void run() {

							img.setImageBitmap(decodeByteArray);
						}
					});
					
					Log.i("Glass", format("FPS: {0}", 1000/(System.currentTimeMillis() - mili))) ;
				}

			}
		}).start();
	}
}
