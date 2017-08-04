package cs.ucl.ac.uk.barp.release.view;

import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

import org.jfree.ui.RefineryUtilities;

import cs.ucl.ac.uk.barp.release.OptimalSolutions;
import cs.ucl.ac.uk.barp.release.ReleasePlan;
import cs.ucl.ac.uk.barp.workitem.WorkItem;

public class CashAnalysisView extends View {
	
	private int investmentHorizon;
	private int noOfReleases;
	private double intRate;
	private double[] budget;
	
	public CashAnalysisView(OptimalSolutions solutions, int investPeriods, double[] budget, double intRate) {
		this.solutions = solutions;
		this.solutions.attachView(this);
		this.investmentHorizon = investPeriods;
		noOfReleases = budget.length;
		this.budget = budget;
		this.intRate = intRate;
	}

	@Override
	public void update() {
		HashMap<String, double[]> analysisResult = new HashMap<String, double[]>();
		solutions.getSolutions().forEach(solution->{
			analysisResult.put(solution.planToString(), analysisTable(solution));
		});
		int[] period = IntStream.rangeClosed(1, investmentHorizon).toArray();
		try {
			AnalysisCurve curve = new AnalysisCurve("Cash Flow Analysis", analysisResult, period);
			curve.pack();
			RefineryUtilities.centerFrameOnScreen(curve);
			curve.setVisible(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		double[] value = analysisTable(solutions.getSolutions().get(0));
//		int[] period = IntStream.rangeClosed(1, investmentHorizon).toArray();
//		String legend = solutions.getSolutions().get(0).planToString();
//		try {
//			AnalysisCurve curve = new AnalysisCurve("Analysis", period, value, legend);
//			curve.pack();
//			RefineryUtilities.centerFrameOnScreen(curve);
//			curve.setVisible(true);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	/**
	 * @param plan
	 * @return An array of rolling value over the investment periods
	 */
	public double[] analysisTable(ReleasePlan plan){
		double[][] table = new double[noOfReleases][investmentHorizon];
		for (int i = 1; i <= noOfReleases; i++){
			if (plan.getPlan().get(i) == null){
				continue;
			}
			List<WorkItem> currentItems = plan.getPlan().get(i).getwItems();
			table[i-1][i-1] = -budget[i-1];
			for (int j = 0; j < investmentHorizon-i; j++){
				double netReleaseValue = 0;
				for (WorkItem item : currentItems){
					if (item.getAveragePeriodValue() == null){
						continue;
					}
					double value = item.getAveragePeriodValue()[j];
					netReleaseValue += value / Math.pow(intRate + 1, i+j);
				}	
				table[i-1][i+j] = netReleaseValue;
			}	
		}
		
		double[] sumTableColumn = new double[investmentHorizon];
		for (int k = 0; k < investmentHorizon; k++){
			double sum = 0;
			for (int l = 0; l < noOfReleases; l++){
				sum += table[l][k];
			}
			sumTableColumn[k] = sum;
		}
		
		double[] rollingValue = new double[investmentHorizon];
		for (int k = 0; k < investmentHorizon; k++){
			rollingValue[k] = (k == 0) ? sumTableColumn[k] : rollingValue[k-1] + sumTableColumn[k];
		}
		return rollingValue;
		
	}

}
