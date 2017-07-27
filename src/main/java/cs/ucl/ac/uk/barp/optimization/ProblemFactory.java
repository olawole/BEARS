package cs.ucl.ac.uk.barp.optimization;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.IntegerSolution;

import cs.ucl.ac.uk.barp.problem.Barp;
import cs.ucl.ac.uk.barp.problem.BarpCertain;
import cs.ucl.ac.uk.barp.project.Project;

public class ProblemFactory {

	public static Problem<IntegerSolution> getProblem(String problemType, Project project){
		if (problemType == null){
			return null;
		}
		if (problemType.equalsIgnoreCase("Barp")){
			return new Barp(project);
		}
		if (problemType.equalsIgnoreCase("BarpCertain")){
			return new BarpCertain(project);
		}
		return null;
	}

}
