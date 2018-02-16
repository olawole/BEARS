package cs.ucl.ac.uk.barp.project.utilities;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cs.ucl.ac.uk.barp.model.ReleasePlan;

public class SolutionFileWriterUtil {
	
	static final String COMMA_SEP = " , ";

	public SolutionFileWriterUtil() {
		// TODO Auto-generated constructor stub
	}
	
	public static void generateCSVTable(List<ReleasePlan> rPlans, String type, int noOfReleases){
		String csvString = "";
		String heading = "S/N" + COMMA_SEP;
		for (int i = 0; i < noOfReleases; i++){
			heading += "Release " + (i+1) + COMMA_SEP;
		}
		heading += "ENPV('000£)" + COMMA_SEP  + "Expected Punctuality (%)" +  COMMA_SEP + "Satisfaction Score" +  COMMA_SEP + "Prob. Effort Exceed Capacity(%)";
		csvString += heading + "\n";
		int counter = 0;
		for (ReleasePlan plan : rPlans){
			String row = ++counter + COMMA_SEP;
			for (int i = 1; i <= noOfReleases; i++){
				if (plan.getPlan().get(i) == null){
					row += "" + COMMA_SEP;
					continue;
				}
					
				String s = plan.getPlan().get(i).toString();
				row += "\""+ s.replace(",", "->")+ "\"" + COMMA_SEP;
			}
			row += StatUtil.round(plan.getBusinessValue(), 2) + COMMA_SEP + StatUtil.round(plan.getExpectedPunctuality(), 2) + COMMA_SEP +  
					plan.getSatisfaction() + COMMA_SEP + plan.getExceedProbability();
			csvString += row + "\n";
		}
		try {
			FileWriter output = new FileWriter(type + ".csv");
			output.write(csvString);
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void writeAll(HashMap<String, List<ReleasePlan>> multiplePlans, int noOfReleases) {
		String csvString = "";
		String heading = "S/N" + COMMA_SEP;
		for (int i = 0; i < noOfReleases; i++){
			heading += "Release " + (i+1) + COMMA_SEP;
		}
		heading += "ENPV('000£)" + COMMA_SEP  + "Expected Punctuality (%)" +  COMMA_SEP + "Satisfaction Score" +  COMMA_SEP + "Prob. Effort Exceed Capacity(%)";
		csvString += heading + "\n";
		int counter = 0;
		for (Map.Entry<String, List<ReleasePlan>> multiplePlan : multiplePlans.entrySet()){
			csvString += multiplePlan.getKey() + "\n";
			for (ReleasePlan plan : multiplePlan.getValue()){
				String row = ++counter + COMMA_SEP;
				for (int i = 1; i <= noOfReleases; i++){
					if (plan.getPlan().get(i) == null){
						row += "" + COMMA_SEP;
						continue;
					}
						
					String s = plan.getPlan().get(i).toString();
					row += "\""+ s.replace(",", "->")+ "\"" + COMMA_SEP;
				}
				row += StatUtil.round(plan.getBusinessValue(), 2) + COMMA_SEP + StatUtil.round(plan.getExpectedPunctuality(), 2) + COMMA_SEP +  
						plan.getSatisfaction() + COMMA_SEP + plan.getExceedProbability();
				csvString += row + "\n";
			}
			csvString += "\n\n";
		}
		try {
			FileWriter output = new FileWriter("result.csv");
			output.write(csvString);
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void generateCSVTableBears0VEvolveII(List<ReleasePlan> bears0Plan, List<ReleasePlan> evolvePlan, int noOfReleases){
		String csvString = "";
		String heading = "S/N" + COMMA_SEP;
		for (int i = 0; i < noOfReleases; i++){
			heading += "Release " + (i+1) + COMMA_SEP;
		}
		heading += "Business Value('000£)" + COMMA_SEP  + "Total Effort";
		csvString += heading + "\n";
		int counter = 0;
		for (ReleasePlan plan : bears0Plan){
			String row = ++counter + COMMA_SEP;
			for (int i = 1; i <= noOfReleases; i++){
				if (plan.getPlan().get(i) == null){
					row += "" + COMMA_SEP;
					continue;
				}
					
				String s = plan.getPlan().get(i).toString();
				row += "\""+ s.replace(",", "->")+ "\"" + COMMA_SEP;
			}
			row += StatUtil.round(plan.getBusinessValue(), 2) + COMMA_SEP + plan.getRiskMeasure();
			csvString += row + "\n";
			if( counter == 10)
				break;
		}
		csvString += "\n\n";
		for (ReleasePlan plan : evolvePlan){
			String row = ++counter + COMMA_SEP;
			for (int i = 1; i <= noOfReleases; i++){
				if (plan.getPlan().get(i) == null){
					row += "" + COMMA_SEP;
					continue;
				}
					
				String s = plan.getPlan().get(i).toString();
				row += "\""+ s.replace(",", "->")+ "\"" + COMMA_SEP;
			}
			row += StatUtil.round(plan.getBusinessValue(), 2) + COMMA_SEP + plan.getRiskMeasure();
			csvString += row + "\n";
			if( counter == 20)
				break;
		}
		try {
			FileWriter output = new FileWriter("bearsEvolve.csv");
			output.write(csvString);
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
