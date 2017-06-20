package cs.ucl.ac.uk.elicitation;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

public class TestRConnection {

	public TestRConnection() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		RConnection c = null;
		
		try {
			c = new RConnection();
			//REXP t = c.eval("R.version.string");
			c.eval("library(SHELF)");
			c.eval("v<-matrix(c(20, 60, 80, 20, 25, 35), 3, 2)");
			c.eval("p<-c(0.25, 0.5,0.75)");
			c.eval("myfit<-fitdist(vals = v, probs = p, lower = 0, upper = 100)");
			System.out.println(c.eval("myfit$ssq$Gamma").asString());
			//double[] d= c.eval("rnorm(100)").asDoubles();
			double[] dataX = new double[]{12,34,5,6,7};
			double[] dataY = new double[]{2.5,7,9,3,4};
			c.assign("x", dataX);
			c.assign("y", dataY);
			//RList l = c.eval("lowess(x,y)").asList();
			//double[] lx = l.at("x").asDoubles();
			//double[] ly = l.at("y").asDoubles();
			System.out.println(c.eval("y").asStrings()[3]);
//			String vector = "c(10,2,3,4)";
//			connection.eval("meanVal=mean("+vector+")");
//			double mean = connection.eval("meanVal").asDouble();
//			System.out.println("The mean of the given vector is ="+ mean);
		}
		catch (RserveException e){
			e.printStackTrace();
		}
		catch (REXPMismatchException e){
			e.printStackTrace();
		} catch (REngineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			c.close();
		}

	}

}
