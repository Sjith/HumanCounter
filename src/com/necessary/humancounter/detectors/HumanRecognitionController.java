package com.necessary.humancounter.detectors;

import java.util.ArrayList;
import java.util.List;
import com.necessary.humancounter.models.IRecognizedFaceHolder;

public class HumanRecognitionController {
	
	private static final Integer HISTORY_CAPACITY = 10;
	
	private final List<IRecognizedFaceHolder> facesHistory;
	
	private Integer addedFacesCounter = 0;
	
	private DetectorResults globalResult = DetectorResults.FINE;
	
	public HumanRecognitionController() {
		this.facesHistory = new ArrayList<IRecognizedFaceHolder>();
	}

	public DetectorResults recognize(IRecognizedFaceHolder faceHolder) {
		facesHistory.add(faceHolder);

		DetectorResults result;
		addedFacesCounter++;
		if (addedFacesCounter == HISTORY_CAPACITY) {
			addedFacesCounter = 0;
			result = analyseHistory();
		} else {
			result = analyseLastFaces();
		}
		
		if (facesHistory.size() > HISTORY_CAPACITY) {
			facesHistory.remove(0);
		}
		
		if (!globalResult.equals(result)) {
			if (facesHistory.size() > 2) {
				if (addedFacesCounter == 0) {
					globalResult = result;
				} else {
					
				}
			} else {
				globalResult = result;
			}
		}
		
		return globalResult;
	}
	
	private DetectorResults analyseLastFaces() {
		DetectorResults result = DetectorResults.FINE;
		
		if (facesHistory.size() > 1) {
			IRecognizedFaceHolder fHolder = facesHistory.get(facesHistory.size() - 2);
			IRecognizedFaceHolder sHolder = facesHistory.get(facesHistory.size() - 1);
			
			if (fHolder.getFacesCount() != sHolder.getFacesCount()) {
				if (facesHistory.size() > 2) {
					result = analyseForDispute();
				} else {
					result = DetectorResults.WARNING;
				}
			}
		} 

		return result;
	}
	
	private DetectorResults analyseForDispute() {
		return DetectorResults.FINE;
	}
	
	private DetectorResults analyseHistory() {
		return DetectorResults.FINE;
	}
	
}
