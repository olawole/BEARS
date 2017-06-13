package cs.ucl.ac.uk.barp.optimization;

import java.util.HashMap;

import org.apache.commons.math3.linear.RealMatrix;

import cs.ucl.ac.uk.barp.release.OptimalSolutions;
import cs.ucl.ac.uk.barp.workitem.WorkItem;
import org.apache.commons.math3.linear.MatrixUtils;

public class InformationValueAnalyser {
	/**
	 * Computes and store expected value of perfect information (evtpi) and expected value of partial perfect information (evppi) in the AnalysisResult  object.
	 * @param result analysis result.
	 * @param objective model objectives.
	 * @param solutions list of optimal solutions
	 * @param params list of model parameters.
	 * @throws Exception 
	 */
	public static void computeInformationValue(OptimalSolutions optimal, HashMap<String, WorkItem> wItems) throws Exception{
		//result.addEviObjective (objective);
		double[][] objSim = optimal.simulate();
		// compute evtpi
		double evtpi = InformationAnalysis.evpi(objSim);
		System.out.println("EVTPI = " + evtpi);
		// compute evppi for each quality variable in params
		for (String key : wItems.keySet()){
			WorkItem wItem = wItems.get(key) ;
			if (wItem.getValue() != null){
				RealMatrix matrix = MatrixUtils.createRealMatrix(wItem.getValueSimulation());
				double[] paramSim = matrix.getColumn(0);
		        double evppi = InformationAnalysis.evppi(paramSim, objSim);
		        optimal.addEvppi(wItem.getItemId(), evppi);
			}
			else {
				optimal.addEvppi(wItem.getItemId(), 0);
			}
			
		}
	}
}
