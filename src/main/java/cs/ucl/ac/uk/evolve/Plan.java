package cs.ucl.ac.uk.evolve;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uma.jmetal.solution.IntegerSolution;

//import cs.ucl.ac.uk.barp.objective.Objective;
import cs.ucl.ac.uk.evolve.EvolveProject;

public class Plan {
	
	private HashMap<Integer, Release> releases;
	//private List<Objective> objectives;
	private double satisfaction;
	private double effort;

	public Plan() {
		releases = new HashMap<Integer, Release>();
	}
	
	public void addRelease(int releaseId, Release newRelease){
		releases.put(releaseId, newRelease);
	}
	
	/**
	 * Copy constructor to generate iteration plan from a solution
	 * @param solution
	 * @param projectId
	 */
	public Plan (IntegerSolution solution, EvolveProject projectId){
		releases = new HashMap<Integer, Release>();
		List<String> vector = projectId.getFeatureIds();
		for (int j = 1; j <= projectId.noOfReleases; j++){
			Release release = new Release();
			for (int i = 0; i < solution.getNumberOfVariables(); i++){
				if(solution.getVariableValue(i) == j){
					String featureId = vector.get(i);
					release.addItemToRelease(projectId.getFeature(featureId));
				}
			}
			addRelease(j, release);
		}
	}
	

	
	

	public Release getRelease(int currentIteration) {
		return this.releases.get(currentIteration);
	}

	public boolean containsKey(int currentIteration) {
		return this.releases.containsKey(currentIteration);
	}
	
	public HashMap<Integer, Release> getPlan(){
		return releases;
	}

	public double getSatisfaction() {
		return satisfaction;
	}

	public void setSatisfaction(double satisfaction) {
		this.satisfaction = satisfaction;
	}

	public double getEffort() {
		return effort;
	}

	public void setEffort(double effort) {
		this.effort = effort;
	}
	
	public String planToString(){
		String s = "";
		for (Map.Entry<Integer, Release> entry : getPlan().entrySet()) {
			if (entry.getValue().isEmpty())
				continue;
			for (Feature feature : entry.getValue().getFeaturesInRelease()) {
				s += s.equals("") ? feature.getFeatureId() : "," + feature.getFeatureId();
			}
			s += "->";
		}
		return s;
	}
	

}
