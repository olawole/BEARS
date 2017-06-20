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
import cs.ucl.ac.uk.barp.workitem.WorkItem;

public class ProjectParser {

	public ProjectParser() {
		// TODO Auto-generated constructor stub
	}
	
	public static Project parseCSVToProject(String filePath, String distributionType) throws Exception{
		Project myProject = new Project();
		String[] nextLine;
		CSVReader reader = new CSVReader(new FileReader(filePath));
		reader.readNext();
		while((nextLine = reader.readNext()) != null){
			String itemId = nextLine[0];
			Double effort[] = parseDouble(nextLine[1].split(","));
			Double value[] = parseDouble(nextLine[2].split(","));
			Distribution bValueDis = DistributionFactory.getDistribution(distributionType, value);
			Distribution effortDis = DistributionFactory.getDistribution(distributionType, effort);
			WorkItem wItem = new WorkItem(itemId, effortDis, bValueDis);
			List<String> precursor = Arrays.asList(nextLine[3].split(","));
			wItem.setPrecursors(precursor);
			if (bValueDis instanceof PointDistribution){
				wItem.setPriority(bValueDis.sample() / effortDis.sample());
			}
			myProject.add(wItem);
		}
		reader.close();
		myProject.setWorkItemVector();
		return myProject;
	}
	
	private static Double[] parseDouble(String[] stringValue){
		Double[] values = new Double[stringValue.length];
		for(int i = 0; i < values.length; i++){
			values[i] = Double.parseDouble(stringValue[i]);
		}
		return values;
	}

}
