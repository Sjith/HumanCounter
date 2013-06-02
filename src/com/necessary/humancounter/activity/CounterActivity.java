package com.necessary.humancounter.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.FaceDetector;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.necessary.humancounter.R;
import com.necessary.humancounter.callbacks.SilentFocusCallback;
import com.necessary.humancounter.callbacks.SilentPictureCallback;
import com.necessary.humancounter.callbacks.SilentShutterCallback;
import com.necessary.humancounter.configs.AppConstants;
import com.necessary.humancounter.detectors.DetectorResults;
import com.necessary.humancounter.detectors.HumanRecognitionController;
import com.necessary.humancounter.listeners.OnFocusListener;
import com.necessary.humancounter.listeners.OnPictureSavedListener;
import com.necessary.humancounter.listeners.OnPictureTakeListener;
import com.necessary.humancounter.models.IRecognizedFaceHolder;
import com.necessary.humancounter.models.RecognizedFaceHolder;
import com.necessary.humancounter.view.CameraView;

public class CounterActivity extends Activity implements OnFocusListener, OnPictureTakeListener, OnPictureSavedListener {

	private PictureCallback pictureCallback;
	private ShutterCallback shutterCallback;
	private AutoFocusCallback focusCallback;
	
	private ProgressBar detectionProgressBar;
	private Boolean detectionInProgress = false;
	
	private HumanRecognitionController recognitionController;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_counter);
		
		detectionProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		
		prepareCallbacks();
		runTimer();
		
	}

	@Override
	public void onFocusFailed() {
		detectionInProgress = false;
		prepareDetection();
	}
	
	@Override
	public void onFocusSucceed() {
		// nothing to do now!
	}

	@Override
	public void onPictureTake() {
		if (detectionProgressBar.getVisibility() == View.INVISIBLE) {
			detectionProgressBar.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onPictureSaved(boolean isSaveSucceed) {
		if (isSaveSucceed) {
			DetectionTask task = new DetectionTask();
			task.execute();
		} else {
			detectionInProgress = false;
			prepareDetection();
		}
		
	}
	
	private void prepareDetection() {
		if (!detectionInProgress) {
			final CameraView cameraView = ((CameraView) findViewById(R.id.cameraView));
			final Camera camera = cameraView.getCamera();

			if (camera != null) {
				detectionInProgress = true;
				camera.autoFocus(focusCallback);
			}
		}
	}
	
	private void prepareCallbacks() {
		String cachePath = getExternalCacheDir().getAbsolutePath().toString();
		pictureCallback = new SilentPictureCallback(CounterActivity.this, cachePath);
		shutterCallback = new SilentShutterCallback(CounterActivity.this);
		focusCallback = new SilentFocusCallback(this, shutterCallback, pictureCallback);
	}
	
	private void runTimer() {
		Timer timer = new Timer();
		TimerTask timerTask = new DetectionTimerTask();
		timer.schedule(timerTask, AppConstants.TIMER_DELAY, AppConstants.DETECTION_INTERVAL);
	}
	
	private class DetectionTimerTask extends TimerTask {

		@Override
		public void run() {
			prepareDetection();
		}
		
	}
	
	private class DetectionTask extends AsyncTask<Void, Void, IRecognizedFaceHolder> {

		@Override
		protected IRecognizedFaceHolder doInBackground(Void... params) {
			Bitmap cachedBitmap = getPreparedBitmap();
	        		
			FaceDetector detector = new FaceDetector(cachedBitmap.getWidth(), cachedBitmap.getHeight(), AppConstants.MAX_FACES);
			FaceDetector.Face[] faces = new FaceDetector.Face[AppConstants.MAX_FACES];
			Integer facesDetected = detector.findFaces(cachedBitmap, faces);
			
			RecognizedFaceHolder holder = new RecognizedFaceHolder();
			holder.setFacesArray(faces);
			holder.setFacesCount(facesDetected);
			
			return holder;
		}
		
		@Override
		protected void onPostExecute(IRecognizedFaceHolder result) {
			((TextView) findViewById(R.id.infoView)).setText(result.getFacesCount().toString());
			if (detectionProgressBar.getVisibility() == View.VISIBLE) {
				detectionProgressBar.setVisibility(View.INVISIBLE);
			}
			
			FaceAnalyzeTask analyseTask = new FaceAnalyzeTask(); 
			analyseTask.execute(result);
			
			detectionInProgress = false;
		}
		
		private Bitmap getPreparedBitmap() {
			BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inPreferredConfig = Bitmap.Config.RGB_565;
	        Bitmap cachedBitmap = BitmapFactory.decodeFile(getExternalCacheDir() + "/" + AppConstants.CACHE_PHOTO_FILENAME, options);
	        
	        Integer scale = 2;
	        if (cachedBitmap.getWidth() >= 2000) {
	        	scale = 4;
	        }
	        
	        Integer scaledWidth = cachedBitmap.getWidth() / scale;
	        Integer scaledHieght = cachedBitmap.getHeight() / scale;
	        cachedBitmap = Bitmap.createScaledBitmap(cachedBitmap, scaledWidth, scaledHieght, false);
	        
	        return cachedBitmap;
		}
		
	}
	
	private class FaceAnalyzeTask extends AsyncTask<IRecognizedFaceHolder, DetectorResults, DetectorResults> {

		@Override
		protected DetectorResults doInBackground(IRecognizedFaceHolder... params) {
			if (recognitionController == null) {
				recognitionController = new HumanRecognitionController();
			}
			
			return recognitionController.recognize(params[0]);
		}
		
		
		@Override
		protected void onPostExecute(DetectorResults result) {
			ImageButton resultButton = (ImageButton) findViewById(R.id.detectionStateButton);
			
			if (DetectorResults.FINE.equals(result)) {
				resultButton.setImageDrawable(getResources().getDrawable(R.drawable.fine));
			} 
			else if (DetectorResults.WARNING.equals(result)) {
				resultButton.setImageDrawable(getResources().getDrawable(R.drawable.warning));
			} 
			else if (DetectorResults.ERROR.equals(result)) {
				resultButton.setImageDrawable(getResources().getDrawable(R.drawable.alert));
			} 
		}

		
	}
}
