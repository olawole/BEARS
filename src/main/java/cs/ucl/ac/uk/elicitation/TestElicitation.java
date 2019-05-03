package cs.ucl.ac.uk.elicitation;

import java.io.FileReader;
import java.io.IOException;

import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import com.opencsv.CSVReader;

public class TestElicitation {

	public TestElicitation() {
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String[] args) {
		RConnection c = null;		
		try {
			c = new RConnection();
			c.eval("library(SHELF)");
			CSVReader reader = new CSVReader(new FileReader("elicit.csv"));
			reader.readNext();
			String[] nextLine;
			while((nextLine = reader.readNext()) != null){
				String id = nextLine[0];
				String vars = nextLine[1] + "," + nextLine[2] + "," + nextLine[3];
				c.eval("v<-matrix(c("+vars+"), 3, 3)");
				c.eval("p<-c(0.25, 0.5,0.75)");
				c.eval("myfit<-fitdist(vals = v, probs = p, lower = 0, upper = " + nextLine[4] +")");
				String[] t = c.eval("myfit$best.fitting$best.fit").asStrings();
				System.out.println(t[0] + " " + t[1] + " " + t[2]);
			}
			reader.close();
		} catch (IOException | RserveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (REXPMismatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
