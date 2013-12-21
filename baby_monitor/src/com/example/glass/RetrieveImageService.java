/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.glass;

import static java.text.MessageFormat.format;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Provider.Service;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.Debug;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.LiveCard.PublishMode;
import com.google.android.glass.timeline.TimelineManager;

/**
 * Service owning the LiveCard living in the timeline.
 */
public class RetrieveImageService extends android.app.Service {

	/**
	 * Sync stop
	 */
	private Object _sync = new Object();
	
	public static final String SERVER_ADDRESS = "http://192.168.2.105:8080/";
	public static final String IMAGE_FILE_TO_SHARE = Environment
			.getExternalStorageDirectory()
			+ File.separator
			+ "temporary_file.jpg";
	public static final String TAG = "RetrieveImageService";
	private static final String LIVE_CARD_TAG = "retrieveimg";

	private TimelineManager _timelineManager;
	private LiveCard _liveCard;
	private boolean stop = false;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "onCreate() called.");

		_timelineManager = TimelineManager.from(this);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "Received start id " + startId + ": " + intent);
		startRunning();
		return START_STICKY;
	}

	private void startRunning() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				URL url;

				Bitmap decodeByteArray = null;
				while (!stop) {
					long mili = System.currentTimeMillis();
					try {
						Log.i(TAG, "Trying to connect server...");
						url = new URL(SERVER_ADDRESS);
						HttpURLConnection connection = (HttpURLConnection) url
								.openConnection();

						InputStream is = connection.getInputStream();

						decodeByteArray = BitmapFactory.decodeStream(is);

						File f = new File(IMAGE_FILE_TO_SHARE);
						try {
							f.createNewFile();
							FileOutputStream fo = new FileOutputStream(f);
							decodeByteArray.compress(CompressFormat.JPEG, 30,
									fo);
						} catch (IOException e) {
							e.printStackTrace();
						}
						Log.i(TAG, "Decoded image");
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

					synchronized (_sync) {
						if (!stop) {
							updateCard(getApplicationContext(), decodeByteArray);
						}
					}
					

					Log.i(TAG,
							format("FPS: {0}",
									1000 / (System.currentTimeMillis() - mili)));
				}

			}
		}).start();
		;

	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy called");
		synchronized (_sync) {
			stop = true;
		}
		if (_liveCard != null && _liveCard.isPublished()) {
			Log.d(TAG, "Unpublishing LiveCard");

			_liveCard.unpublish();
			_liveCard = null;
		}
		
		
		super.onDestroy();
	}

	/**
	 * Update the local card.
	 * 
	 * @param context
	 */
	private void updateCard(Context context, Bitmap bitmapToDisplay) {
		Log.i(TAG, "updateCard() called.");
		if (bitmapToDisplay == null) {
			Log.w(TAG, "Bitmap to display is empty, ignoring...");
		}
		// if (liveCard == null || !liveCard.isPublished()) {
		if (_liveCard == null) {
			// Use the default content.
			publishCard(context, bitmapToDisplay);
		} else {
			updateImage(context, bitmapToDisplay);
		}
	}

	private void updateImage(Context context, Bitmap bitmapToDisplay) {
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.activity_main);
		if (bitmapToDisplay != null) {
			remoteViews.setImageViewBitmap(R.id.imgFromCamera, bitmapToDisplay);
		}
		_liveCard.setViews(remoteViews);
		Intent intent = new Intent(context, MenuActivity.class);
		_liveCard.setAction(PendingIntent.getActivity(context, 0, intent, 0));
	}

	private void publishCard(Context context, Bitmap bitmapToDisplay) {
		Log.i(TAG, "publishCard() called.");
		// if (liveCard == null || !liveCard.isPublished()) {
		if (_liveCard == null) {

			_liveCard = _timelineManager.createLiveCard(LIVE_CARD_TAG);
			updateImage(context, bitmapToDisplay);
			_liveCard.publish(LiveCard.PublishMode.REVEAL);
		} else {
			Log.w(TAG, "Card already publushed, call update instead");
		}

	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}
