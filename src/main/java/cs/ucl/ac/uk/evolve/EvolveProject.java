package cs.ucl.ac.uk.evolve;

import java.util.ArrayList;
import java.util.List;

public class EvolveProject {
	
	int noOfReleases;
	int noOfFeatures;
	int noOfStakeholders;
	
	List<String> featureIds;
	
	List<Feature> features;
	
	List<Stakeholder> stakeholders;
	
	double[] capacity;
	
	double[][] valueMatrix;
	
	double[][] urgencyMatrix;
	

	public EvolveProject() {
	//	this.capacity = capacity;
		noOfReleases = capacity.length;
		noOfFeatures = 0;
		featureIds = new ArrayList<String>();
		features = new ArrayList<Feature>();
	}
	
	public void addFeature(Feature feature){
		if (!featureIds.contains(feature.getFeatureId())){
			featureIds.add(feature.getFeatureId());
			features.add(feature);
		}
	}
	
	

}
