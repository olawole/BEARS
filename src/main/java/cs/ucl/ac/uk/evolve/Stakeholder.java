package cs.ucl.ac.uk.evolve;

import java.util.ArrayList;
import java.util.List;

public class Stakeholder {
	private String stakeholderId;
	private double importance;
	private List<Double> featuresValueVector;
	private List<String> urgencyVector;

	public Stakeholder(String id) {
		stakeholderId = id;
		featuresValueVector = new ArrayList<Double>();
		urgencyVector = new ArrayList<String>();
	}

	public double getImportance() {
		return importance;
	}

	public void setImportance(double importance) {
		this.importance = importance;
	}

	public List<Double> getFeaturesValueVector() {
		return featuresValueVector;
	}

	public void setFeaturesValueVector(List<Double> featuresValueVector) {
		this.featuresValueVector = featuresValueVector;
	}

	public String getStakeholderId() {
		return stakeholderId;
	}

	public void setStakeholderId(String stakeholderId) {
		this.stakeholderId = stakeholderId;
	}

	public List<String> getUrgencyVector() {
		return urgencyVector;
	}

	public void setUrgencyVector(List<String> urgencyVector) {
		this.urgencyVector = urgencyVector;
	}
	
	public void addValue(int featIndex, double value){
		featuresValueVector.add(value);
	}
	
	public void addUrgency(int featIndex, String urgencyTuple){
		urgencyVector.add(urgencyTuple);
	}

}
