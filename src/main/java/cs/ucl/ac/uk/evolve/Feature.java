package cs.ucl.ac.uk.evolve;

public class Feature {
	
	private String featureId;
	
	private double effort;
	
	private int[] value;
	
	private int[] priority;

	public Feature(String id) {
		featureId = id;
	}

	public int[] getValue() {
		return value;
	}

	public void setValue(int[] value) {
		this.value = value;
	}

	public int[] getPriority() {
		return priority;
	}

	public void setPriority(int[] priority) {
		this.priority = priority;
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
