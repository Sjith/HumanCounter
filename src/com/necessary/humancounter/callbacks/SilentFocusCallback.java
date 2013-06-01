package com.necessary.humancounter.callbacks;

import com.necessary.humancounter.listeners.OnFocusListener;

import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;

public class SilentFocusCallback implements AutoFocusCallback {

	private final OnFocusListener listener;
	
	private final ShutterCallback shutterCallback;
	
	private final PictureCallback pictureCallback;

	public SilentFocusCallback(OnFocusListener listener, ShutterCallback shutterCallback, PictureCallback pictureCallback) {
		this.listener = listener;
		this.shutterCallback = shutterCallback;
		this.pictureCallback = pictureCallback;
	}
	
	@Override
	public void onAutoFocus(boolean success, Camera camera) {
		if (success) {
			listener.onFocusSucceed();
			camera.takePicture(shutterCallback, null, pictureCallback);
		} else {
			camera.startPreview();
			listener.onFocusFailed();
		}
		
	}

}
