package cs.ucl.ac.uk.barp.optimization;

public class InfoValueQuickTest {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		double []  var = new double[] {.5, 0.23, 1.13, 2.00, 0.13};
		
		double [][] simMatrix = new double [2][5];
		double [] newThing = new double [5];
		newThing[0]= 1.33;
		newThing[1]= 0.13;
		newThing[2]= 4.05;
		newThing[3]= 6.13;
		newThing[4]= -1.39;
		
		double [] oldThing= new double [5]; 
		oldThing[0]= 1.03;
		newThing[1]= 0.96;
		newThing[2]= 1.00;
		newThing[3]= 1.06;
		newThing[4]= 1.07;
		
		simMatrix[0]=newThing;
		simMatrix[1]=oldThing;
		
		double evtpi = InformationAnalysis.evpi(simMatrix);
		
		System.out.println("evtpi is David orientation "+ evtpi);
		System.out.println("evppi emmanuel orientation "+ InformationAnalysis.evppi(var, simMatrix));
		
		
		simMatrix[0][0] = 1.33;
		simMatrix[0][1] = 0.13;
		simMatrix[0][2] = 4.05;
		simMatrix[0][3] = 6.13;
		simMatrix[0][4] =-1.39;
		
		simMatrix[1][0] = 1.03;
		simMatrix[1][1] = 0.96;
		simMatrix[1][2] = 1.00;
		simMatrix[1][3] = 1.06;
		simMatrix[1][4] = 1.07;
		
		
		double evtpi2 = InformationAnalysis.evpi(simMatrix);
		System.out.println("evtpi emmanuel orientation "+ evtpi2);
		System.out.println("evppi emmanuel orientation "+ InformationAnalysis.evppi(var, simMatrix));
		
		/*double [][] simmatrix = new double [5][2];
		simmatrix[0][0] = 1.33;
		simmatrix[1][0] = 0.13;
		simmatrix[2][0] = 4.05;
		simmatrix[3][0] = 6.13;
		simmatrix[4][0] =-1.39;
		
		simmatrix[0][1] = 1.03;
		simmatrix[1][1] = 0.96;
		simmatrix[2][1] = 1.00;
		simmatrix[3][1] = 1.06;
		simmatrix[4][1] = 1.07;
		
		
		double evtpi3 = InformationAnalysis.evpi(simmatrix);
		System.out.println("evtpi emmanuel orientation "+ evtpi3);
		System.out.println("evppi emmanuel orientation "+ InformationAnalysis.evppi(var, simmatrix));
		*/

		
	}

}
