package cs.ucl.ac.uk.evolve;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EvolveProject {
	
	int noOfReleases;
	int noOfFeatures;
	int noOfStakeholders;
	int noOfObjectives;
	
	String featureIds[];
	
	Feature[] features;
	
	Stakeholder[] stakeholders;
	
	double[] capacity;
	
	double[][] valueMatrix;
	
	double[][] urgencyMatrix;
	

	public EvolveProject() {
		featureIds = new String[noOfFeatures];
		features = new Feature[noOfFeatures];
	//	values = new int[noOfFeatures][noOfStakeholders];
		
		capacity = new double[noOfReleases];
		
		for (int i = 0; i < noOfFeatures;i++){
			featureIds[i] = features[i].getFeatureId();
	//		values[i] = features[i].getValue();
	//		priority[i] = features[i].getPriority();
	//		effort[i] = features[i].getEffort();
		}
		// TODO Auto-generated constructor stub
	}
	
	public void addFeature(Feature feature){
		if (!Arrays.asList(featureIds).contains(feature.getFeatureId())){
			Arrays.asList(featureIds).add(feature.getFeatureId());
		}
	}

}
