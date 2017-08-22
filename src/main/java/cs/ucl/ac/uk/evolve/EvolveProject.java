package cs.ucl.ac.uk.evolve;

import java.util.ArrayList;
import java.util.List;

public class EvolveProject {
	
	int noOfReleases;
	int noOfFeatures;
	int noOfStakeholders;
	
	private List<String> featureIds;
	
	private List<Feature> features;
	
//	List<Stakeholder> stakeholders;
	
	public double[] capacity;
	
	public int[] valueMatrix;
	
//	public int[][] valueMatrix;
	
//	public int[][] urgencyMatrix;
	
	public double effortMatrix[];
	
//	public double stakeImp[];
	
	public double releaseImp[];
	

	public EvolveProject() {
	//	this.capacity = capacity;
	//	noOfReleases = capacity.length;
		noOfFeatures = 0;
		featureIds = new ArrayList<String>();
		features = new ArrayList<Feature>();
//		stakeholders = new ArrayList<Stakeholder>();
	}
	
	//public EvolveProject(double[] releaseImp, )
	
	public void addFeature(Feature feature){
		if (!featureIds.contains(feature.getFeatureId())){
			featureIds.add(feature.getFeatureId());
			features.add(feature);
		}
	}
	
//	public void setStakeImportance(){
//		stakeImp = new double[stakeholders.size()];
//		for (int i = 0; i < noOfStakeholders; i++){
//			stakeImp[i] = stakeholders.get(i).getImportance();
//		}
//	}
	
	public void setParameterMatrix(){
		noOfFeatures = features.size();
		noOfReleases = capacity.length;
		valueMatrix = new int[noOfFeatures];
		effortMatrix = new double[noOfFeatures];
//		urgencyMatrix = new int[noOfFeatures][noOfStakeholders * noOfReleases];
		for (int i = 0; i < noOfFeatures; i++){
			effortMatrix[i] = features.get(i).getEffort();
			valueMatrix[i] = features.get(i).getValue();
//			for (int j = 0; j < noOfStakeholders; j++){
//				valueMatrix[i][j] = features.get(i).getFeaturesValueVector().get(j);
//			}
//			int entries = noOfStakeholders * noOfReleases;
//			for (int j = 0; j < entries; j++){
//				urgencyMatrix[i][j] = features.get(i).getUrgencyVector().get(j);
//			}
				
		}
		
	}

	public List<String> getFeatureIds() {
		return featureIds;
	}

	public void setFeatureIds(List<String> featureIds) {
		this.featureIds = featureIds;
	}

	public List<Feature> getFeatures() {
		return features;
	}

	public void setFeatures(List<Feature> features) {
		this.features = features;
	}
	
	public Feature getFeature(String id){
		int index = featureIds.indexOf(id);
		if (index < 0)
			return null;
		return features.get(index);
	}
	
	
	
	
	
	
	

}
