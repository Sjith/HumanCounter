package com.necessary.humancounter.callbacks;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;

import com.necessary.humancounter.configs.AppConstants;
import com.necessary.humancounter.listeners.OnPictureSavedListener;

public class SilentPictureCallback implements PictureCallback {
	
	private final OnPictureSavedListener listener;
	
	private final String cachePath;
	
	public SilentPictureCallback(OnPictureSavedListener listener, String path) {
		this.listener = listener;
		this.cachePath = path;
	}
	
	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		if (data != null) {
			try {
				FileOutputStream outStream = new FileOutputStream(cachePath + "/" + AppConstants.CACHE_PHOTO_FILENAME);
				outStream.write(data);
				outStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				camera.startPreview();
				listener.onPictureSaved(true);
			}
		} else {
			camera.startPreview();		
			listener.onPictureSaved(false);
		}
	}

}
