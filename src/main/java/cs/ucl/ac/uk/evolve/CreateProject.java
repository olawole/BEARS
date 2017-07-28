package cs.ucl.ac.uk.evolve;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;

public class CreateProject {

	public CreateProject(String filePath) {
		
	}
	
	public EvolveProject readFeatures(String filePath){
		String nextLine[];
		try {
			EvolveProject project = new EvolveProject();
			CSVReader reader = new CSVReader(new FileReader(filePath));
			reader.readNext();
			while((nextLine = reader.readNext()) != null){
				String featureId = nextLine[0];
				double effort = Double.parseDouble(nextLine[1]);
				Feature feature = new Feature(featureId);
				feature.setEffort(effort);
				project.addFeature(feature);
			}
			reader.close();
			return project;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	
	public void readStakeholderValue(EvolveProject project, String filePath){
		String nextLine[];
		try {
			CSVReader reader = new CSVReader(new FileReader(filePath));
			List<Stakeholder> stake = new ArrayList<Stakeholder>();
			String[] head = reader.readNext();
			for (int j = 1; j < head.length; j++){
				stake.add(new Stakeholder(head[j], 1));
			}
			while((nextLine = reader.readNext()) != null){
				String featureId = nextLine[0];
				List<Double> stakeValues = new ArrayList<Double>();
				for (int i = 1; i < nextLine.length; i++){
					stakeValues.add(Double.parseDouble(nextLine[i]));
				}
				int index = project.featureIds.indexOf(featureId);
				if (index >= 0){
					project.features.get(index).setFeaturesValueVector(stakeValues);
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void readUrgency(EvolveProject project, String filePath){
		String nextLine[];
		try {
			CSVReader reader = new CSVReader(new FileReader(filePath));
			reader.readNext();
			while((nextLine = reader.readNext()) != null){
				String featureId = nextLine[0];
				List<String> urgency = new ArrayList<String>();
				for (int i = 1; i < nextLine.length; i++){
					urgency.add(nextLine[i]);
				}
				int index = project.featureIds.indexOf(featureId);
				if (index >= 0){
					project.features.get(index).setUrgencyVector(urgency);
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
