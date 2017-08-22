package cs.ucl.ac.uk.evolve;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.uma.jmetal.solution.IntegerSolution;

import cs.ucl.ac.uk.barp.project.utilities.StatUtil;
import cs.ucl.ac.uk.barp.release.view.View;

public class Pareto {

	private List<Plan> solutions;
	private List<View> views;

	public Pareto() {
		solutions = new ArrayList<Plan>();
		views = new ArrayList<View>();
	}

	public void setSolutions(List<IntegerSolution> optimalSol, EvolveProject projectId) {
		optimalSol.forEach(solution -> {
			//System.out.println(solution.toString());
			addReleasePlan(solution, projectId);
		});
		removeDuplicate();
		findParetoOptimal();
	}
	
	public void removeDuplicate(){
		List<Plan> sol = new ArrayList<Plan>(solutions);
		solutions = new ArrayList<Plan>();
		sol.forEach(plan->{
			if (!contains(plan)){
				solutions.add(plan);
			}
		});
	}


	public void updateViews() {
		views.forEach(view -> {
			view.update();
		});

	}

	public void attachView(View view) {
		views.add(view);
	}

	private void addReleasePlan(IntegerSolution solution, EvolveProject projectId) {
		Plan rPlan = new Plan(solution, projectId);
//		String planString = planToString(rPlan);
		if (!solutions.contains(rPlan)){
			rPlan.setSatisfaction(Math.abs(solution.getObjective(0)));
			rPlan.setEffort(StatUtil.sum(projectId.capacity) - solution.getObjective(1));
			solutions.add(rPlan);
//			uniqueSolutions.add(planString);
		}	
	}

	public List<Plan> getSolutions() {
		return solutions;
	}

	public void findParetoOptimal() {
		List<Plan> rPlans = new ArrayList<Plan>(solutions);
		solutions = new ArrayList<Plan>();
		for (int i = 0; i < rPlans.size(); i++) {
			boolean pareto = true;
			for (int j = 1; j < rPlans.size(); j++) {
				if (i == j)
					continue;
				if (dominates(rPlans.get(j), rPlans.get(i))) {
					pareto = false;
					break;
				}
			}
			if (pareto) {
				solutions.add(rPlans.get(i));
			}
		}
		sortBusinessValue();
		updateViews();
	}

	public boolean all(Plan plan1, Plan plan2) {
		boolean value = false;
		if (plan1.getSatisfaction() >= plan2.getSatisfaction()
				&& plan1.getEffort() <= plan2.getEffort()) {
			value = true;
		}
		return value;
	}

	public boolean any(Plan plan1, Plan plan2) {
		boolean value = false;
		if (plan1.getSatisfaction() > plan2.getSatisfaction()
				|| plan1.getEffort() < plan2.getEffort()) {
			value = true;
		}
		return value;
	}

	public boolean dominates(Plan plan1, Plan plan2) {
		boolean dominate = false;

		if (all(plan1, plan2) && any(plan1, plan2)) {
			dominate = true;
		}

		return dominate;
	}
	
	public boolean isEqual(Plan plan1, Plan plan2){
		boolean equal = false;
		if (plan1.getSatisfaction() == plan2.getSatisfaction()
				&& plan1.getEffort() == plan2.getEffort()) {
			equal = true;
		}
		return equal;
	}
	
	public boolean contains(Plan plan){
		for (Plan p : solutions){
			if (isEqual(p, plan)){
				return true;
			}
		}
		return false;
	}
	public void sortBusinessValue() {
		for (int i = 1; i < solutions.size(); i++) {
			Double index = solutions.get(i).getSatisfaction();
			int j = i;
			while (j > 0 && solutions.get(j - 1).getSatisfaction() < index) {
				Collections.swap(solutions, j, j - 1);
				j--;
			}
		}
	}

	
//	public String planToString(Plan plan){
//		String s = "";
//		for (Map.Entry<Integer, Release> entry : plan.getPlan().entrySet()) {
//			if (entry.getValue().isEmpty())
//				continue;
//			for (WorkItem wItem : entry.getValue().getwItems()) {
//				s += s.equals("") ? wItem.getItemId() : "," + wItem.getItemId();
//			}
//		}
//		return s;
//	}

}
