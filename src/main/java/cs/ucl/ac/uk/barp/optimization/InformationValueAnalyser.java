package cs.ucl.ac.uk.barp.optimization;

import java.util.HashMap;

import org.apache.commons.math3.linear.RealMatrix;

import cs.ucl.ac.uk.barp.project.utilities.ConfigSetting;
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
		evtpi = InformationValue.evpi(objSim);
		System.out.println("EVTPI = " + evtpi);
		// compute evppi for each quality variable in params
		for (String key : wItems.keySet()){
			WorkItem wItem = wItems.get(key) ;
			if (wItem.getValue() != null){
				double[] paramSim = wItem.getAverageSimulation();
				if (paramSim.length != ConfigSetting.NUMBER_OF_SIMULATION){
					System.out.println("ERROR");
				}
		        double evppi = InformationAnalysis.evppi(paramSim, objSim);
		        optimal.addEvppi(wItem.getItemId(), evppi);
//		        double evppi1 = InformationValue.evppi(paramSim, objSim);
//		        optimal.addEvppi(wItem.getItemId()+"A", evppi1);
			}
			else {
				optimal.addEvppi(wItem.getItemId(), 0);
			}
			
		}
	}
}
