package cs.ucl.ac.uk.barp.objective;

import cs.ucl.ac.uk.barp.project.Project;
import cs.ucl.ac.uk.barp.release.ReleasePlan;

public interface Objective {
	//double computeValue(IntegerSolution solution, Project projectId);
	boolean isMinimization();
	double getObjectiveValue();
	void setObjectiveValue(double value);
	double computeObjeciveValue(Project project, ReleasePlan plan);
}
