package cs.ucl.ac.uk.evolve;

import java.util.List;

public class Feature {
	
	private String featureId;
	
	private double effort;
	
	private int[] value;
	
	private List<Double> featuresValueVector;
	
	private List<String> urgencyVector;

	public Feature(String id) {
		featureId = id;
	}

	public int[] getValue() {
		return value;
	}

	public void setValue(int[] value) {
		this.value = value;
	}

	public List<Double> getFeaturesValueVector() {
		return featuresValueVector;
	}

	public void setFeaturesValueVector(List<Double> featuresValueVector) {
		this.featuresValueVector = featuresValueVector;
	}
	
	public List<String> getUrgencyVector() {
		return urgencyVector;
	}

	public void setUrgencyVector(List<String> urgencyVector) {
		this.urgencyVector = urgencyVector;
	}
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

}
