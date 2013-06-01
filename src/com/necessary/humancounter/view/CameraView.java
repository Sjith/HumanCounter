package com.necessary.humancounter.view;

import java.io.IOException;

import com.necessary.humancounter.utils.ICameraHandler;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback, ICameraHandler {

	private SurfaceHolder iHolder;
	
	private Camera iCamera;

	public CameraView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		iHolder = getHolder();
		iHolder.addCallback(this);
	    iHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	    
	    iHolder.setKeepScreenOn(true);
	}
	
	public CameraView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public CameraView(Context context) {
		this(context, null, 0);
	}
	
	
	public void surfaceCreated(SurfaceHolder holder) {
		iCamera = Camera.open();
        
        try {
			iCamera.setPreviewDisplay(holder);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        iCamera.stopPreview();
        iCamera.setPreviewCallback(null);
        iCamera.release();
        iCamera = null;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Camera.Parameters parameters = iCamera.getParameters();
        parameters.setPreviewSize(width, height);
        iCamera.setParameters(parameters);
        iCamera.startPreview();
    }

    public Camera getCamera() {
    	return iCamera;
    }
}
