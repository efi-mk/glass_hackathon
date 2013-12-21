package com.google.android.glass.powerpoint;

import com.google.android.glass.sample.powerpoint.R;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;

public class PowerpointDisplayActivity extends Activity  {

	private GestureDetector _detector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		_detector = createGestureDetector(this);
	}

	private GestureDetector createGestureDetector(Context context) {
		GestureDetector gestureDetector = new GestureDetector(context);
		// Create a base listener for generic gestures
		gestureDetector.setBaseListener(new GestureDetector.BaseListener() {
			@Override
			public boolean onGesture(Gesture gesture) {
				if (gesture == Gesture.TAP) {
					// do something on tap
					return true;
				} else if (gesture == Gesture.TWO_TAP) {
					finish();
					return true;
				} else if (gesture == Gesture.SWIPE_RIGHT) {
					// Going forward
					UdpClientTask back = new UdpClientTask(8989, "f");
					back.execute();
					return true;
				} else if (gesture == Gesture.SWIPE_LEFT) {
					// Going backward
					UdpClientTask forward = new UdpClientTask(8989, "b");
					forward.execute();
					return true;
				}
				return false;
			}
		});

		return gestureDetector;
	}

	/*
     * Send generic motion events to the gesture detector
     */
    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (_detector != null) {
            return _detector.onMotionEvent(event);
        }
        return false;
    }

}
