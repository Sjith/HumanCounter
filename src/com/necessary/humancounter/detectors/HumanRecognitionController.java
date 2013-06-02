package com.necessary.humancounter.detectors;

import java.util.ArrayList;
import java.util.List;
import com.necessary.humancounter.models.IRecognizedFaceHolder;
import com.necessary.humancounter.utils.FaceAnalyzeUtils;

public class HumanRecognitionController {
	
	/*
	 * Capacity of recognition history
	 */
	private static final Integer HISTORY_CAPACITY = 10;
	
	/*
	 * Limit of warning, when warningCount > WARNINGS_LIMIT
	 * then app should return ERROR
	 */
	private static final Integer WARNINGS_LIMIT = 3;
	
	/*
	 * List which store faces from further analyzes
	 */
	private final List<IRecognizedFaceHolder> facesHistory;
	
	/*
	 * Counter to check how many faces were added after last analyze
	 */
	private Integer addedFacesCounter = 0;
	
	/*
	 * Count how many warning were found
	 */
	private Integer warningCount = 0;
	
	/*
	 * Result of analyze
	 */
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

		if (warningCount >= WARNINGS_LIMIT) {
			result = DetectorResults.ERROR;
		} else {
			if (result.equals(DetectorResults.WARNING)) {
				warningCount++;
			} 
			else if (warningCount >= 0) {
				warningCount--;
			}
		}

		return result;
	}
	
	private DetectorResults analyseForDispute() {
		if (FaceAnalyzeUtils.ifSomeoneGoneByFacesCount(facesHistory)) {
			return DetectorResults.WARNING;
		}
		
		return DetectorResults.FINE;
	}
	
	private DetectorResults analyseHistory() {
		return DetectorResults.FINE;
	}
	
}
