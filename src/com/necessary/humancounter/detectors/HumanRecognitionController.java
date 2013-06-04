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
	private Integer warningsCount = 0;
	
	/*
	 * ACHTUNG!
	 * This field is using for inform user about the couse of warning/error
	 * It was created only for presentation. 
	 * Refactor me!!!
	 */
	private String warningCause;

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
		
		if (warningsCount >= WARNINGS_LIMIT) {
			result = DetectorResults.ERROR;
			warningCause = "Zbyt du¿o zagadkowych znikniêæ. Du¿e prawdopodobieñstwo matactwa!";
		}
		
		return result;
	}
	
	private DetectorResults analyseLastFaces() {
		DetectorResults result = DetectorResults.FINE;
		
		if (facesHistory.size() > 1) {
			IRecognizedFaceHolder fHolder = facesHistory.get(facesHistory.size() - 2);
			IRecognizedFaceHolder sHolder = facesHistory.get(facesHistory.size() - 1);
			
			if (fHolder.getFacesCount() != sHolder.getFacesCount() || warningsCount > 0) {
				if (facesHistory.size() > 2) {
					result = analyseForDispute();
				} else {
					warningCause = "Coœ siê nie kalkuluje. Jeszcze to sprawdzimy...";
					result = DetectorResults.WARNING;
				}
			} 
		} 

		if (result.equals(DetectorResults.WARNING)) {
			warningsCount++;
		} 
		else if (warningsCount >= 0) {
			warningsCount--;
		}

		return result;
	}
	
	private DetectorResults analyseForDispute() {
		if (FaceAnalyzeUtils.ifSomeoneGoneByFacesCount(facesHistory)) {
			warningCause = "Ktoœ prawdopodobnie przed chwil¹ nawia³!";
			return DetectorResults.WARNING;
		}
		
		return DetectorResults.FINE;
	}
	
	private DetectorResults analyseHistory() {
		int warnings = 0;
		
		if (FaceAnalyzeUtils.ifSomeoneGoneInHistoryByFacesCount(facesHistory)) {
			warnings++;
			warningCause = "Za ma³o osób! Ktoœ chyba w miêdzyczasie znikn¹³ z sali!";
		} else {
			warnings--;
		}
		
		if (FaceAnalyzeUtils.ifSomeoneGoneInHistoryByQuality(facesHistory)) {
			warnings++;
		} else {
			warnings--;
		}
		
		switch (warnings) {
		case -2:
			warningsCount = 0;
			return DetectorResults.FINE;
		case 0:
			warningsCount = 1;
			return DetectorResults.WARNING;
		case 2:
			warningsCount = 2;
			return DetectorResults.WARNING;
		default:
			return DetectorResults.FINE;
		}
	}
	
	public String getCause() {
		return warningCause;
	}
	
}
