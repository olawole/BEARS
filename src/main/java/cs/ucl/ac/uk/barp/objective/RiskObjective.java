package cs.ucl.ac.uk.barp.objective;

import cs.ucl.ac.uk.barp.model.Project;
import cs.ucl.ac.uk.barp.model.ReleasePlan;

public class RiskObjective extends AbstractObjective implements Objective {

	public RiskObjective(String name, boolean isminimization){
		this.name = name;
		this.isMinimisation = isminimization;
	}

	@Override
	public double computeObjeciveValue(Project project, ReleasePlan plan) {
		// TODO Auto-generated method stub
		return 0;
	}

}
