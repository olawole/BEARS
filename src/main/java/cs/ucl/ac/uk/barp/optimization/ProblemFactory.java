package cs.ucl.ac.uk.barp.optimization;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.IntegerSolution;

import cs.ucl.ac.uk.barp.model.Project;
import cs.ucl.ac.uk.barp.problem.BEARS;
import cs.ucl.ac.uk.barp.problem.BEARS1;
import cs.ucl.ac.uk.barp.problem.BEARSRigid;

public class ProblemFactory {

	public static Problem<IntegerSolution> getProblem(String problemType, Project project){
		if (problemType == null){
			return null;
		}
		if (problemType.equalsIgnoreCase("Barp")){
			return new BEARS(project);
		}
		if (problemType.equalsIgnoreCase("BearsRigid")){
			return new BEARSRigid(project);
		}
		
		if (problemType.equalsIgnoreCase("Bears1")){
			return new BEARS1(project);
		}
		return null;
	}

}
