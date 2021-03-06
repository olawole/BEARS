package cs.ucl.ac.uk.barp.release;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.uma.jmetal.solution.IntegerSolution;

import cs.ucl.ac.uk.barp.model.Project;
import cs.ucl.ac.uk.barp.model.Release;
import cs.ucl.ac.uk.barp.model.ReleasePlan;
import cs.ucl.ac.uk.barp.model.WorkItem;
import cs.ucl.ac.uk.barp.project.utilities.ConfigSetting;
import cs.ucl.ac.uk.barp.release.view.View;

public class OptimalSolutions {

	private List<ReleasePlan> solutions;
	private List<String> uniqueSolutions;
	private List<View> views;
	HashMap<String, Double> evppi;

	public OptimalSolutions() {
		solutions = new ArrayList<ReleasePlan>();
		views = new ArrayList<View>();
		evppi = new HashMap<String, Double>();
		uniqueSolutions = new ArrayList<String>();
	}

	public void setSolutions(List<IntegerSolution> optimalSol, Project projectId) {
		optimalSol.forEach(solution -> {
			//System.out.println(solution.toString());
			addReleasePlan(solution, projectId);
		});
		removeDuplicate();
		findParetoOptimal();
	}
	
	public void removeDuplicate(){
		List<ReleasePlan> sol = new ArrayList<ReleasePlan>(solutions);
		solutions = new ArrayList<ReleasePlan>();
		sol.forEach(plan->{
			if (!contains(plan)){
				solutions.add(plan);
			}
		});
	}

	public void addEvppi(String label, double value) {
		evppi.put(label, value);
	}

	public void updateViews() {
		views.forEach(view -> {
			view.update();
		});

	}

	public void attachView(View view) {
		views.add(view);
	}

	private void addReleasePlan(IntegerSolution solution, Project projectId) {
		ReleasePlan rPlan = new ReleasePlan(solution, projectId);
		rPlan.sortWorkItemsByPriority();
		List<WorkItem> workSequence = rPlan.getWorkSequence();
		rPlan = rPlan.actualPlan(workSequence, projectId.getEffortCapacity());
		String planString = rPlan.planToString();
		if (!uniqueSolutions.contains(planString)){
			rPlan.setBusinessValue(Math.abs(solution.getObjective(0)));
			//rPlan.setInvestmentRisk(solution.getObjective(1));
			rPlan.setExpectedPunctuality((1 - solution.getObjective(1)) * 100);
//			rPlan.setExpectedPunctuality(solution.getObjective(1));
			solutions.add(rPlan);
			uniqueSolutions.add(planString);
		}	
	}

	public List<ReleasePlan> getSolutions() {
		return solutions;
	}

	public void findParetoOptimal() {
		List<ReleasePlan> rPlans = new ArrayList<ReleasePlan>(solutions);
		solutions = new ArrayList<ReleasePlan>();
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

	public boolean all(ReleasePlan plan1, ReleasePlan plan2) {
		boolean value = false;
		if (plan1.getBusinessValue() >= plan2.getBusinessValue()
				&& plan1.getExpectedPunctuality() >= plan2.getExpectedPunctuality()) {
			value = true;
		}
		return value;
	}

	public boolean any(ReleasePlan plan1, ReleasePlan plan2) {
		boolean value = false;
		if (plan1.getBusinessValue() > plan2.getBusinessValue()
				|| plan1.getExpectedPunctuality() >= plan2.getExpectedPunctuality()) {
			value = true;
		}
		return value;
	}

	public boolean dominates(ReleasePlan plan1, ReleasePlan plan2) {
		boolean dominate = false;

		if (all(plan1, plan2) && any(plan1, plan2)) {
			dominate = true;
		}

		return dominate;
	}
	
	public boolean isEqual(ReleasePlan plan1, ReleasePlan plan2){
		boolean equal = false;
		if (plan1.getBusinessValue() == plan2.getBusinessValue()
				&& plan1.getExpectedPunctuality() == plan2.getExpectedPunctuality()) {
			equal = true;
		}
		return equal;
	}
	
	public boolean contains(ReleasePlan plan){
		for (ReleasePlan p : solutions){
			if (isEqual(p, plan)){
				return true;
			}
		}
		return false;
	}
	public void sortBusinessValue() {
		for (int i = 1; i < solutions.size(); i++) {
			Double index = solutions.get(i).getBusinessValue();
			int j = i;
			while (j > 0 && solutions.get(j - 1).getBusinessValue() < index) {
				Collections.swap(solutions, j, j - 1);
				j--;
			}
		}
	}

	public double[][] simulate() {
		double[][] objValues = new double[solutions.size()][];
		int n = 0;
		for (ReleasePlan rPlan : solutions){
			double[] npv = new double[ConfigSetting.NUMBER_OF_SIMULATION];
			for (int j = 0; j < ConfigSetting.NUMBER_OF_SIMULATION; j++) {
				double sanpv = 0;
				for (int i = 1; i <= rPlan.getPlan().size(); i++) {
					Release release = rPlan.getRelease(i);
					if (release != null) {
						for (WorkItem wi : release.getwItems()) {
							if (wi.getValue() != null)
								sanpv += wi.getSanpv()[j][i];
						}
					}
				}
				npv[j] = sanpv;
			}
			objValues[n++] = npv;
		}

		return objValues;
	}
	
	public void printEvppi(){
		evppi.forEach((key,value)->{
			System.out.println(key + " = " + value);
		});
	}

}
