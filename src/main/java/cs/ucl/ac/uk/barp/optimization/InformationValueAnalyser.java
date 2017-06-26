package cs.ucl.ac.uk.barp.optimization;

import java.util.HashMap;

import cs.ucl.ac.uk.barp.project.utilities.ConfigSetting;
import cs.ucl.ac.uk.barp.release.OptimalSolutions;
import cs.ucl.ac.uk.barp.workitem.WorkItem;

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
		double[][] objSim = optimal.simulate();
		// compute evtpi
		double evtpi = InformationAnalysis.evpi(objSim);
		System.out.println("\nEVTPI = " + evtpi);
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
			}
			else {
				optimal.addEvppi(wItem.getItemId(), 0);
			}
			
		}
	}
}
