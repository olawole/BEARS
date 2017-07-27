package cs.ucl.ac.uk.evolve;

public class Release {
	
	private int releaseImportance;
	private int releaseId;
	private Feature[] featuresInRelease;
	
	public Release() {
		// TODO Auto-generated constructor stub
	}

	public int getReleaseImportance() {
		return releaseImportance;
	}

	public void setReleaseImportance(int releaseImportance) {
		this.releaseImportance = releaseImportance;
	}

	public Feature[] getFeaturesInRelease() {
		return featuresInRelease;
	}

	public void setFeaturesInRelease(Feature[] featuresInRelease) {
		this.featuresInRelease = featuresInRelease;
	}

	public int getReleaseId() {
		return releaseId;
	}

	public void setReleaseId(int releaseId) {
		this.releaseId = releaseId;
	}

}
