package cs.ucl.ac.uk.evolve;


public class Stakeholder {
	private String stakeholderId;
	private double importance;
	

	public Stakeholder(String id, double importance) {
		stakeholderId = id;
		this.importance = importance;
	}

	public double getImportance() {
		return importance;
	}

	public void setImportance(double importance) {
		this.importance = importance;
	}

	

	public String getStakeholderId() {
		return stakeholderId;
	}

	public void setStakeholderId(String stakeholderId) {
		this.stakeholderId = stakeholderId;
	}
	
//	public void addValue(int featIndex, double value){
//		featuresValueVector.add(value);
//	}
//	
//	public void addUrgency(int featIndex, String urgencyTuple){
//		urgencyVector.add(urgencyTuple);
//	}

}
