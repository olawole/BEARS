package cs.ucl.ac.uk.evolve;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Feature {
	
	private String featureId;
	
	private double effort;
		
//	private int[] value;
	
	private int value;
	
	private List<Integer> featuresValueVector;
	
//	private List<Integer> urgencyVector;
	
	private List<String> precursors;

	public Feature(String id) {
		featureId = id;
		precursors = new ArrayList<String>();
	}

//	public int[] getValue() {
//		return value;
//	}
	
	public int getValue() {
		return value;
	}

//	public void setValue(int[] value) {
//		this.value = value;
//	}
	
	public void setValue(int value) {
		this.value = value;
	}

	public List<Integer> getFeaturesValueVector() {
		return featuresValueVector;
	}

	public void setFeaturesValueVector(List<Integer> featuresValueVector) {
		this.featuresValueVector = featuresValueVector;
	}
	
//	public List<Integer> getUrgencyVector() {
//		return urgencyVector;
//	}
//
//	public void setUrgencyVector(List<Integer> urgencyVector) {
//		this.urgencyVector = urgencyVector;
//	}
	public String getFeatureId() {
		return featureId;
	}

	public void setFeatureId(String featureId) {
		this.featureId = featureId;
	}

	public double getEffort() {
		return effort;
	}

	public void setEffort(double effort) {
		this.effort = effort;
	}

	public List<String> getPrecursors() {
		return Collections.unmodifiableList(precursors);
	}
	
	private void addPrecursor(String item){
		if (item.equalsIgnoreCase(featureId)){
			return;
		}
		if (!precursors.contains(item)){
			precursors.add(item);
		}
	}

	public void setPrecursors(List<String> precursors) {
		precursors.forEach(feature->{
			addPrecursor(feature);
		});
	}

}
