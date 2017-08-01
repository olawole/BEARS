package cs.ucl.ac.uk.evolve;

import java.util.List;

public class Release {
	
	private int releaseImportance;
	private int releaseId;
	private List<Feature> featuresInRelease;
	
	public Release() {
		// TODO Auto-generated constructor stub
	}

	public int getReleaseImportance() {
		return releaseImportance;
	}

	public void setReleaseImportance(int releaseImportance) {
		this.releaseImportance = releaseImportance;
	}

	public List<Feature> getFeaturesInRelease() {
		return featuresInRelease;
	}

	public void setFeaturesInRelease(List<Feature> featuresInRelease) {
		this.featuresInRelease = featuresInRelease;
	}

	public int getReleaseId() {
		return releaseId;
	}

	public void setReleaseId(int releaseId) {
		this.releaseId = releaseId;
	}
	
	public  void addItemToRelease(Feature feature) {
        if (featuresInRelease.contains(feature.getFeatureId())) {
            throw new IllegalArgumentException("This Work Item already exists in the release "
                    + feature.getFeatureId());
        }
        featuresInRelease.add(feature);
    }

}
