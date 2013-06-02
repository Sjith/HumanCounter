package com.necessary.humancounter.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.necessary.humancounter.models.IRecognizedFaceHolder;

public class FaceAnalyzeUtils {

	/*
	 * Check if someone has disappeared from classes.
	 * Quantitative evaluation!
	 */
	public static final boolean ifSomeoneGoneByFacesCount(List<IRecognizedFaceHolder> facesList) {
		/*
		 * 1st - check if the first two recognitions were good
		 */
		if (facesList.size() > 2) {
			IRecognizedFaceHolder fHolder = facesList.get(0);
			IRecognizedFaceHolder sHolder = facesList.get(1);
			IRecognizedFaceHolder tHolder = facesList.get(2);
			
			if (fHolder.getFacesCount() == sHolder.getFacesCount()) {
			
				if (sHolder.getFacesCount() != tHolder.getFacesCount()) {
					return true;
				}
				
			} else {
				/*
				 * We are not sure which estimation is good so we must 
				 * compare random recognitions.
				 */
				List<IRecognizedFaceHolder> randomFaces = FaceAnalyzeUtils.getRandomFaces(facesList);
				
				if (randomFaces.get(0).getFacesCount() != randomFaces.get(1).getFacesCount()) {
					return true;
				}
			}
		}

		return false;
	}
	
	public static final List<IRecognizedFaceHolder> getRandomFaces(List<IRecognizedFaceHolder> facesList) {
		Random random = new Random();
		List<Integer> indices = new ArrayList<Integer>();
		
		while(indices.size() < 2) {
			int randomIndex = random.nextInt(facesList.size() - 1);
			if (randomIndex != 0) {
				if (indices.size() == 0) {
					indices.add(randomIndex);
				} else {
					if (indices.get(0) != randomIndex) {
						indices.add(randomIndex);
					}
				}
			}
		}
		
		List<IRecognizedFaceHolder> faces = new ArrayList<IRecognizedFaceHolder>();
		faces.add(faces.get(indices.get(0)));
		faces.add(faces.get(indices.get(1)));
		
		return faces;
	}
	
}
