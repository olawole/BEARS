package cs.ucl.ac.uk.barp.objective;


public interface IObjective {
	//double computeValue(IntegerSolution solution, Project projectId);
	boolean isMinimization();
	String getObjectiveName();
	void setObjectiveName(String name);
	double getObjectiveValue();
	void setObjectiveValue(double value);
}
