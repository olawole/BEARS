package cs.ucl.ac.uk.srprisk;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.opencsv.CSVReader;

import cs.ucl.ac.uk.barp.project.utilities.StatUtil;

public class SRPRiskProjectUtil {

	public SRPRiskProjectUtil(String filePath) {
		
	}
	
	public static SRPRiskProject readFeatures(SRPRiskProject project, String filePath){
		String nextLine[];
		try {
			CSVReader reader = new CSVReader(new FileReader(filePath));
			reader.readNext();
			while((nextLine = reader.readNext()) != null){
				String featureId = nextLine[0];
				List<String> effortDis = Arrays.asList(nextLine[1].split(","));
				double mean = Double.parseDouble(effortDis.get(0));
				double sd = Double.parseDouble(effortDis.get(1));
				LogNDistribution effort = new LogNDistribution(mean, sd);
				List<String> precursor = Arrays.asList(nextLine[2].split(","));
				SRPFeature feature = new SRPFeature(featureId);
				feature.setEffort(effort);
				feature.setPrecursors(precursor);
				project.addFeature(feature);
			}
			reader.close();
			return project;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	public static void readStakeholderValue(SRPRiskProject project, String filePath, double[] importance){
		String nextLine[];
		try {
			CSVReader reader = new CSVReader(new FileReader(filePath));
//			List<Stakeholder> stake = new ArrayList<Stakeholder>();
			String[] head = reader.readNext();
//			for (int j = 1; j < head.length; j++){
//				stake.add(new Stakeholder(head[j], importance[j-1]));
//			}
//			project.stakeholders = stake;
//			project.noOfStakeholders = stake.size();
			while((nextLine = reader.readNext()) != null){
				String featureId = nextLine[0];
//				List<Integer> stakeValues = new ArrayList<Integer>();
//				for (int i = 1; i < nextLine.length; i++){
//					stakeValues.add(Integer.parseInt(nextLine[i]));
//				}
				int index = project.getFeatureIds().indexOf(featureId);
				if (index >= 0){
					project.getFeatures().get(index).setValue(Integer.parseInt(nextLine[2]));
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
//	public static void readUrgency(EvolveProject project, String filePath){
//		String nextLine[];
//		try {
//			CSVReader reader = new CSVReader(new FileReader(filePath));
//			reader.readNext();
//			while((nextLine = reader.readNext()) != null){
//				String featureId = nextLine[0];
//				List<Integer> urgency = new ArrayList<Integer>();
//				for (int i = 1; i < nextLine.length; i++){
//					Integer[] val = parseInteger(nextLine[i].split(","));
//					if(StatUtil.sum(val) != 9){
//						reader.close();
//						throw new Exception("The sum of urgency values must be equal to 9");
//					}
//					urgency.addAll(Arrays.asList(val));
//				}
//				int index = project.getFeatureIds().indexOf(featureId);
//				if (index >= 0){
//					project.getFeatures().get(index).setUrgencyVector(urgency);
//				}
//			}
//			reader.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
//	private static Integer[] parseInteger(String[] stringValue){
//		Integer[] values = new Integer[stringValue.length];
//		for(int i = 0; i < values.length; i++){
//			values[i] = Integer.parseInt(stringValue[i]);
//		}
//		return values;
//	}

}
