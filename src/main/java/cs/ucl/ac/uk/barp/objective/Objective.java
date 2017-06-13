package cs.ucl.ac.uk.barp.objective;

public abstract class Objective implements IObjective {
	
	private String objectiveName;
	private boolean minimisation;
	private double objectiveValue;
	public Objective(String name, boolean isMinimization) {
		// TODO Auto-generated constructor stub
		setObjectiveName(name);
		minimisation = isMinimization;
	}

	@Override
	public boolean isMinimization() {
		// TODO Auto-generated method stub
		return minimisation;
	}

	@Override
	public String getObjectiveName() {
		// TODO Auto-generated method stub
		return objectiveName;
	}

	@Override
	public void setObjectiveName(String name) {
		// TODO Auto-generated method stub
		objectiveName = name;
	}
	
	@Override
	public double getObjectiveValue() {
		return objectiveValue;
	}
	
	@Override
	public void setObjectiveValue(double objectiveValue) {
		this.objectiveValue = objectiveValue;
	}

}
