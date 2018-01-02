package cs.ucl.ac.uk.barp.project;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import com.opencsv.CSVReader;

import cs.ucl.ac.uk.barp.distribution.Distribution;
import cs.ucl.ac.uk.barp.distribution.DistributionFactory;
import cs.ucl.ac.uk.barp.distribution.PointDistribution;
import cs.ucl.ac.uk.barp.model.Project;
import cs.ucl.ac.uk.barp.model.WorkItem;

public class ProjectParser {

	public ProjectParser() {
		// TODO Auto-generated constructor stub
	}
	
	public static Project parseCSVToProject(String filePath, String distributionType) throws Exception{
		Project myProject = new Project();
		String[] nextLine;
		CSVReader reader = new CSVReader(new FileReader(filePath));
		reader.readNext();
		reader.readNext();
		while((nextLine = reader.readNext()) != null){
			String itemId = nextLine[0];
//			Double effort[] = parseDouble(nextLine[1].split(","));
//			Double value[] = parseDouble(nextLine[2].split(","));
			Double[] effort = new Double[]{Double.parseDouble(nextLine[1]), Double.parseDouble(nextLine[2])};
			Double[] value = new Double[]{Double.parseDouble(nextLine[3]), Double.parseDouble(nextLine[4])};
			Distribution bValueDis = DistributionFactory.getDistribution(distributionType, value);
			Distribution effortDis = DistributionFactory.getDistribution(distributionType, effort);
			WorkItem wItem = new WorkItem(itemId, effortDis, bValueDis);
			List<String> precursor = Arrays.asList(nextLine[5].split(","));
			wItem.setPrecursors(precursor);
			if (bValueDis instanceof PointDistribution){
				wItem.setAverageEffort(effortDis.sample());
				wItem.setPriority(bValueDis.sample() / effortDis.sample());
			}
			myProject.add(wItem);
		}
		reader.close();
		myProject.setWorkItemVector();
		return myProject;
	}
	
//	private static Double[] parseDouble(String[] stringValue){
//		Double[] values = new Double[stringValue.length];
//		for(int i = 0; i < values.length; i++){
//			values[i] = Double.parseDouble(stringValue[i]);
//		}
//		return values;
//	}

}
