package cs.ucl.ac.uk.barp.objective;

import cs.ucl.ac.uk.barp.project.Project;
import cs.ucl.ac.uk.barp.release.ReleasePlan;

public class BusinessValueObjective extends AbstractObjective implements Objective {
	
	public BusinessValueObjective(String name, boolean isminimization) {
		this.name = name;
		this.isMinimisation = isminimization;
	}
	

	@Override
	public double computeObjeciveValue(Project project, ReleasePlan plan) {
		// TODO Auto-generated method stub
		return 0;
	}

}
