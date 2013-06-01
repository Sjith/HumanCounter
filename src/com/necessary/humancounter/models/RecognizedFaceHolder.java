package com.necessary.humancounter.models;

import android.media.FaceDetector;

public class RecognizedFaceHolder implements IRecognizedFaceHolder {
	
	private FaceDetector.Face[] facesArray;
	
	private Integer facesCount;

	public FaceDetector.Face[] getFacesArray() {
		return facesArray;
	}

	public void setFacesArray(FaceDetector.Face[] facesArray) {
		this.facesArray = facesArray;
	}

	public Integer getFacesCount() {
		return facesCount;
	}

	public void setFacesCount(Integer facesCount) {
		this.facesCount = facesCount;
	}

}
