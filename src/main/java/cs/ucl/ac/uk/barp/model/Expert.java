package cs.ucl.ac.uk.barp.model;

public class Expert {
	
	private String expertId;
	
	/**
	 * estimates is a string consisting of a comma separated value of
	 * lower bound, lower quartile, median, upper quartile and upper bound 
	 * of expert value estimate of a feature
	 */
	private String estimates;
	
	/**
	 * @param expertId
	 * @param estimate
	 */
	public Expert(String expertId, String estimate) {
		setEstimates(estimate);
		setExpertId(expertId);
	}

	public String getEstimates() {
		return estimates;
	}

	public void setEstimates(String estimates) {
		this.estimates = estimates;
	}

	public String getExpertId() {
		return expertId;
	}

	public void setExpertId(String expertId) {
		this.expertId = expertId;
	}

}
