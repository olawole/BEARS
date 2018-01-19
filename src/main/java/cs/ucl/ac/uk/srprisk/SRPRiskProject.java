package cs.ucl.ac.uk.srprisk;

import java.util.ArrayList;
import java.util.List;

public class SRPRiskProject {
	
	int noOfReleases;
	int noOfFeatures;
	int noOfStakeholders;
	
	private List<String> featureIds;
	
	private List<SRPFeature> features;
	
//	List<Stakeholder> stakeholders;
	
	public double[] capacity;
	
	public int[] valueMatrix;
	
//	public int[][] valueMatrix;
	
//	public int[][] urgencyMatrix;
	
	public double effortMatrix[][];
	
//	public double stakeImp[];
	
	public double releaseImp[];	

	public SRPRiskProject() {
	//	this.capacity = capacity;
	//	noOfReleases = capacity.length;
		noOfFeatures = 0;
		featureIds = new ArrayList<String>();
		features = new ArrayList<SRPFeature>();
//		stakeholders = new ArrayList<Stakeholder>();
	}
	
	//public EvolveProject(double[] releaseImp, )
	
	public void addFeature(SRPFeature feature){
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
	
	public void setParameterMatrix(int noOfSimulation){
		noOfFeatures = features.size();
		noOfReleases = capacity.length;
		valueMatrix = new int[noOfFeatures];
		effortMatrix = new double[noOfFeatures][noOfSimulation];
//		urgencyMatrix = new int[noOfFeatures][noOfStakeholders * noOfReleases];
		for (int i = 0; i < noOfFeatures; i++){
			effortMatrix[i] = features.get(i).getEffort().sample(noOfSimulation);
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
		
	public void setParameter(int noOfSimulation) {
		noOfFeatures = features.size();
		noOfReleases = capacity.length;
		valueMatrix = new int[noOfFeatures];
		effortMatrix = new double[noOfFeatures][noOfSimulation];
		// urgencyMatrix = new int[noOfFeatures][noOfStakeholders *
		// noOfReleases];
		for (int i = 0; i < noOfFeatures; i++) {
			effortMatrix[i] = features.get(i).getEffortSim();
			valueMatrix[i] = features.get(i).getValue();
		}

	}

	public List<String> getFeatureIds() {
		return featureIds;
	}

	public void setFeatureIds(List<String> featureIds) {
		this.featureIds = featureIds;
	}

	public List<SRPFeature> getFeatures() {
		return features;
	}

	public void setFeatures(List<SRPFeature> features) {
		this.features = features;
	}
	
	public SRPFeature getFeature(String id){
		int index = featureIds.indexOf(id);
		if (index < 0)
			return null;
		return features.get(index);
	}
	
	
	
	
	
	
	

}
