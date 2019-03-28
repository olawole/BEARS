package cs.ucl.ac.uk.barp.project.utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.solution.Solution;

import cs.ucl.ac.uk.barp.model.ReleasePlan;

public class ParetoOptimalUtil {

	public ParetoOptimalUtil() {
		// TODO Auto-generated constructor stub
	}
	
	public static List<ReleasePlan> removeDuplicate(List<ReleasePlan> solutions){
		List<ReleasePlan> sol = new ArrayList<ReleasePlan>(solutions);
		solutions = new ArrayList<ReleasePlan>();
		for (ReleasePlan plan : sol){
			if (!contains(solutions, plan)){
				solutions.add(plan);
			}
		}
		return solutions;
	}
	
	public static boolean contains(List<ReleasePlan> sol, ReleasePlan plan){
		for (ReleasePlan p : sol){
			if ((p.getBusinessValue() == plan.getBusinessValue()) && 
					(plan.getExpectedPunctuality() == p.getExpectedPunctuality())){
				return true;
			}
		}
		return false;
	}
	
	public static List<ReleasePlan> findParetoOptimal(List<ReleasePlan> solutions) {
		List<ReleasePlan> rPlans = new ArrayList<ReleasePlan>(solutions);
		solutions = new ArrayList<ReleasePlan>();
		for (int i = 0; i < rPlans.size(); i++) {
			boolean pareto = true;
			for (int j = 0; j < rPlans.size(); j++) {
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
		return solutions;
	}
	
	public static boolean all(ReleasePlan plan1, ReleasePlan plan2) {
		boolean value = false;
		if (plan1.getBusinessValue() <= plan2.getBusinessValue()
				&& plan1.getExpectedPunctuality() <= plan2.getExpectedPunctuality()) {
			value = true;
		}
		return value;
	}

	public static boolean any(ReleasePlan plan1, ReleasePlan plan2) {
		boolean value = false;
		if (plan1.getBusinessValue() < plan2.getBusinessValue()
				|| plan1.getExpectedPunctuality() < plan2.getExpectedPunctuality()) {
			value = true;
		}
		return value;
	}

	public static boolean dominates(ReleasePlan plan1, ReleasePlan plan2) {
		boolean dominate = false;

		if (all(plan1, plan2) && any(plan1, plan2)) {
			dominate = true;
		}

		return dominate;
	}
	
	public static List<ReleasePlan> findParetoOptimalSRP(List<ReleasePlan> solutions) {
		List<ReleasePlan> rPlans = new ArrayList<ReleasePlan>(solutions);
		solutions = new ArrayList<ReleasePlan>();
		for (int i = 0; i < rPlans.size(); i++) {
			boolean pareto = true;
			for (int j = 0; j < rPlans.size(); j++) {
				if (i == j)
					continue;
				if (dominatesSRP(rPlans.get(j), rPlans.get(i))) {
					pareto = false;
					break;
				}
			}
			if (pareto) {
				solutions.add(rPlans.get(i));
			}
		}
		return solutions;
	}
	
	public static boolean allSRP(ReleasePlan plan1, ReleasePlan plan2) {
		boolean value = false;
		if (plan1.getSatisfaction() >= plan2.getSatisfaction()
				&& plan1.getExceedProbability() >= plan2.getExceedProbability()) {
			value = true;
		}
		return value;
	}

	public static boolean anySRP(ReleasePlan plan1, ReleasePlan plan2) {
		boolean value = false;
		if (plan1.getSatisfaction() > plan2.getSatisfaction()
				|| plan1.getExceedProbability() > plan2.getExceedProbability()) {
			value = true;
		}
		return value;
	}

	public static boolean dominatesSRP(ReleasePlan plan1, ReleasePlan plan2) {
		boolean dominate = false;

		if (allSRP(plan1, plan2) && anySRP(plan1, plan2)) {
			dominate = true;
		}

		return dominate;
	}
	
	public static void sortBusinessValue(List<ReleasePlan> solutions) {
		for (int i = 1; i < solutions.size(); i++) {
			Double index = solutions.get(i).getBusinessValue();
			int j = i;
			while (j > 0 && solutions.get(j - 1).getBusinessValue() < index) {
				Collections.swap(solutions, j, j - 1);
				j--;
			}
		}
	}
	
	public static List<IntegerSolution> sortValuePoint(List<IntegerSolution> solutions, int size) {
		// remove duplicates
		List<IntegerSolution> uniqueSol = new ArrayList<IntegerSolution>();
		List<Double> uniqueNo = new ArrayList<Double>();
		for (int j = 0; j < solutions.size(); j++){
			if(!uniqueNo.contains(solutions.get(j).getObjective(0))){
				uniqueNo.add(solutions.get(j).getObjective(0));
				uniqueSol.add(solutions.get(j));
			}
		}
		
		for (int i = 1; i < uniqueSol.size(); i++) {
			double index = uniqueSol.get(i).getObjective(0);
			int j = i;
			while (j > 0 && uniqueSol.get(j - 1).getObjective(0) < index) {
				Collections.swap(uniqueSol, j, j - 1);
				j--;
			}
		}
		if (uniqueSol.size() <= size){
			return uniqueSol;
		}
		List<IntegerSolution> result = new ArrayList<IntegerSolution>(uniqueSol.subList(0, size));
		return result;
	}
	
	public static List<IntegerSolution> findParetoOptimalSol(List<IntegerSolution> solutions) {
		List<IntegerSolution> rPlans = new ArrayList<IntegerSolution>(solutions);
		solutions = new ArrayList<IntegerSolution>();
		for (int i = 0; i < rPlans.size(); i++) {
			boolean pareto = true;
			for (int j = 0; j < rPlans.size(); j++) {
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
		return solutions;
	}
	
	public static boolean all(IntegerSolution plan1, IntegerSolution plan2) {
		boolean value = false;
		if (plan1.getObjective(0) <= plan2.getObjective(0)
				&& plan1.getObjective(1) <= plan2.getObjective(1)) {
			value = true;
		}
		return value;
	}

	public static boolean any(IntegerSolution plan1, IntegerSolution plan2) {
		boolean value = false;
		if (plan1.getObjective(0) < plan2.getObjective(0)
				|| plan1.getObjective(1) < plan2.getObjective(1)) {
			value = true;
		}
		return value;
	}

	public static boolean dominates(IntegerSolution plan1, IntegerSolution plan2) {
		boolean dominate = false;

		if (all(plan1, plan2) && any(plan1, plan2)) {
			dominate = true;
		}

		return dominate;
	}

}
