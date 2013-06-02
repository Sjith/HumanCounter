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
				List<IRecognizedFaceHolder> randomFaces = FaceAnalyzeUtils.getRandomFaces(facesList, 2);
				if (randomFaces.get(0).getFacesCount() != randomFaces.get(1).getFacesCount()) {
					return true;
				}
			}
		}

		return false;
	}
	
	/*
	 * Check if someone has disappeared from classes in history.
	 * Quantitative evaluation!
	 */
	public static final boolean ifSomeoneGoneInHistoryByFacesCount(List<IRecognizedFaceHolder> facesList) {
		final int TEST_ELEMENTS = 5;
		final int MAX_WARNINGS = 2;
		
		List<IRecognizedFaceHolder> fRandomFaces = FaceAnalyzeUtils.getRandomFaces(facesList, TEST_ELEMENTS);
		
		/*
		 * This test was made spontaneously.
		 * It means that there is no one good way to test faces.
		 * If you have a mainframe smartphone with 16 cores you can implement 
		 * other test which test every face - one by one.
		 */
		int[] firstArgIndices = {0,1,2,3,4};
		int[] sndArgIndices = {3,4,0,1,2};
		int testWarnings = 0;
		for (int i = 0; i < TEST_ELEMENTS; i++) {
			if (fRandomFaces.get(firstArgIndices[i]).getFacesCount() != fRandomFaces.get(sndArgIndices[i]).getFacesCount()) {
				testWarnings++;
			}
		}
		                                                                                                                                                                                                                                                                           
		if (testWarnings >= MAX_WARNINGS) {
			return true;
		}
		
		return false;
	}
	
	public static final List<IRecognizedFaceHolder> getRandomFaces(List<IRecognizedFaceHolder> facesList, int howMany) {
		Random random = new Random();
		List<Integer> indices = new ArrayList<Integer>();
		List<IRecognizedFaceHolder> faces = new ArrayList<IRecognizedFaceHolder>();
		
		while(indices.size() < howMany) {
			int randomIndex = random.nextInt(facesList.size() - 1);
			if (randomIndex != 0) {
				if (indices.size() == 0 || !indices.contains(randomIndex)) {
					indices.add(randomIndex);
					faces.add(facesList.get(randomIndex));
				}
			}
		}
		
		return faces;
	}
	
}
