package cs.ucl.ac.uk.evolve;

import java.util.HashMap;
import java.util.List;
import org.uma.jmetal.solution.IntegerSolution;

//import cs.ucl.ac.uk.barp.objective.Objective;
import cs.ucl.ac.uk.evolve.EvolveProject;

public class Plan {
	
	private HashMap<Integer, Release> releases;
	//private List<Objective> objectives;
	private double satisfaction;
	private double effort;

	public Plan() {
		// TODO Auto-generated constructor stub
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
		// TODO Auto-generated method stub
		return this.releases.get(currentIteration);
	}

	private boolean containsKey(int currentIteration) {
		// TODO Auto-generated method stub
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
	

}
