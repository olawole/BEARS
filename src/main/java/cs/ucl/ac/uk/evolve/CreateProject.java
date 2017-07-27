package cs.ucl.ac.uk.evolve;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;

public class CreateProject {

	public CreateProject(String filePath) {
		String nextLine[];
		try {
			CSVReader reader = new CSVReader(new FileReader(filePath));
			reader.readNext();
			List<Stakeholder> stakeH = new ArrayList<Stakeholder>();
			int counter = 0;
			while((nextLine = reader.readNext()) != null){
				counter++;
				int numberOfStakeholders = (nextLine.length - 2) / 2;
				String featureId = nextLine[0];
				double effort = Double.parseDouble(nextLine[1]);
				for (int i = 1; i <= numberOfStakeholders; i++){
					Stakeholder s = new Stakeholder("A"+i);
					s.addValue(counter - 1, Double.parseDouble(nextLine[i*2]));
					s.addUrgency(counter - 1, nextLine[i*2+1]);
					stakeH.add(s);
				}
				Feature f = new Feature(featureId);
				f.setEffort(effort);
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
