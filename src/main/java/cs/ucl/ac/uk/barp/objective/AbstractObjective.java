package cs.ucl.ac.uk.barp.objective;

public abstract class AbstractObjective implements Objective {

	public String name;
	private double value;
	protected boolean isMinimisation;

	public AbstractObjective() {
		super();
	}

	@Override
	public boolean isMinimization() {
		// TODO Auto-generated method stub
		return isMinimisation;
	}

	@Override
	public double getObjectiveValue() {
		return value;
	}

	@Override
	public void setObjectiveValue(double value) {
		this.value = value;
	}

}