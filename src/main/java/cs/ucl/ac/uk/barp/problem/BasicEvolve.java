package cs.ucl.ac.uk.barp.problem;

import org.uma.jmetal.problem.impl.AbstractIntegerProblem;
import org.uma.jmetal.solution.IntegerSolution;

public class BasicEvolve extends AbstractIntegerProblem {
	
	int noOfFeatures;
	int noOfReleases;
	
	int[][] values;
	
	double[] effort;
	
	int[][] priority;
	
	double[] capacity;
	
	int[] stakeholderImportance;

	public BasicEvolve() {
		// TODO Auto-generated constructor stub
	}
	
	public BasicEvolve(int[][] values, double[] effort, int[][] priority, double[] capacity){
		noOfFeatures = effort.length;
		noOfReleases = capacity.length;
		this.values = values;
		this.effort = effort;
		this.capacity = capacity;
		this.priority = priority;
		
		setNumberOfVariables(noOfFeatures);
	    setNumberOfObjectives(1);
	    setName("BasicEvolve");
	}

	@Override
	public void evaluate(IntegerSolution solution) {
		// TODO Auto-generated method stub
		
	}

}
