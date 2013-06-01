package com.necessary.humancounter.callbacks;

import com.necessary.humancounter.listeners.OnPictureTakeListener;

import android.hardware.Camera.ShutterCallback;

public class SilentShutterCallback implements ShutterCallback {

	private final OnPictureTakeListener listener;
	
	public SilentShutterCallback(OnPictureTakeListener listener) {
		this.listener = listener;
	}
	
	@Override
	public void onShutter() {
		listener.onPictureTake();
	}

}
