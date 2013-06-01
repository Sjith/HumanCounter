package com.necessary.humancounter.models;

import android.media.FaceDetector;

public interface IRecognizedFaceHolder {
	
	public FaceDetector.Face[]  getFacesArray();
	
	public Integer getFacesCount();

}
