package cs.ucl.ac.uk.barp.optimization;

import java.util.HashMap;
import java.util.Map.Entry;

import cs.ucl.ac.uk.barp.distribution.Distribution;
import cs.ucl.ac.uk.barp.workitem.WorkItem;
import cs.ucl.ac.uk.barp.project.utilities.ConfigSetting;


public class MCSimulator {

	public static void simulate (HashMap<String, WorkItem> workItems, int period, double intRate) throws Exception{
		if(workItems.isEmpty()){
			throw new Exception("No Project data to simulate");
		}
		for(Entry<String, WorkItem> entry : workItems.entrySet()){
			//Simulate cash distribution
			WorkItem wItem = entry.getValue();
			if(wItem.getValue() != null){
				Distribution value = wItem.getValue();
				Distribution effort = wItem.getEffort();
				double[][] valuesim = new double[ConfigSetting.NUMBER_OF_SIMULATION][period];
				double[] effortSim = new double[ConfigSetting.NUMBER_OF_SIMULATION];
				double sum = 0;
				double sumVal = 0;
				//double[] avgsim = new double[period];
				for(int j = 0; j < ConfigSetting.NUMBER_OF_SIMULATION; j++){ //create samples without loop
					valuesim[j] = value.sample(period);
					effortSim[j] = Math.abs(effort.sample());
					sum += effortSim[j];
					sumVal += Math.abs(valuesim[j][0]);
				}
				
				wItem.setValueSimulation(valuesim);
				wItem.setAverageValue(sumVal / ConfigSetting.NUMBER_OF_SIMULATION);
				wItem.setEffortSimulation(effortSim);
				wItem.setAverageEffort(sum / ConfigSetting.NUMBER_OF_SIMULATION);
				wItem.setPriority(wItem.getAverageValue() / wItem.getAverageEffort());
			}
			else {
				Distribution effort = wItem.getEffort();
				double[] effortSim = new double[ConfigSetting.NUMBER_OF_SIMULATION];
				double sum = 0;
				for(int j = 0; j < ConfigSetting.NUMBER_OF_SIMULATION; j++){ //create samples without loop
					effortSim[j] = Math.abs(effort.sample());
					sum += effortSim[j];
				}
				wItem.setEffortSimulation(effortSim);
				wItem.setAverageEffort(sum / ConfigSetting.NUMBER_OF_SIMULATION);
				wItem.setAverageValue(0);
				wItem.setPriority(wItem.getAverageValue() / wItem.getAverageEffort());
			}
		}
		simulate_sanpv(workItems, period, intRate);
	}

	private static void simulate_sanpv(HashMap<String, WorkItem> workItems, int period, double intRate) throws Exception {
		if(workItems.isEmpty()){
			throw new Exception("No Project data to simulate");
		}
		for(Entry<String, WorkItem> wItem : workItems.entrySet()){
			double[][] sanpvsim = new double[ConfigSetting.NUMBER_OF_SIMULATION][period];
			if(wItem.getValue().getValue() != null){
				double[][] valuesim = wItem.getValue().getValueSimulation();
				for(int i = 0; i < ConfigSetting.NUMBER_OF_SIMULATION; i++){
					for(int j = 0; j < period; j++){
						sanpvsim[i][j] = getSaNpv(j, valuesim[i], intRate, period);
					}
				}
			}
			wItem.getValue().setSanpv(sanpvsim);
		}		
	}

	private static double getSaNpv(int skipPeriods, double[] value, double intRate, int period) {
		if (skipPeriods < 0) {
            throw new IllegalArgumentException("Invalid startPeriod: "
                    + skipPeriods);
        }

        double npv = 0.0F;
        for (int p = 1; p < period - skipPeriods; p++) {
            int per = (skipPeriods + p);
            npv += Math.abs(value[p]) / Math.pow(intRate + 1, per);
        }
        return npv;
	}
	
	

}
