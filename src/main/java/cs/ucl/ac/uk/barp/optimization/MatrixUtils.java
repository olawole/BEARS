package cs.ucl.ac.uk.barp.optimization;


/**
 * @author David Stefan
 */
public class MatrixUtils {

    public static double[][] addRow(double[][] m, double[] row) throws Exception {
        
        if (m.length == 0 || m[0].length == 0) {
            throw new Exception("Matrix dimensions need be non-zero.");
        }
        
        if (m[0].length != row.length) {
            throw new Exception("Matrix cols dimension must match row length.");
        }
        
        int rows = m.length;
        int cols = m[0].length;
        double[][] mNew = new double[rows + 1][cols];
  
        System.arraycopy(m, 0, mNew, 0, rows - 1);
        mNew[rows] = row;
        
        return mNew;
    }
    
    public static String matrixToString(double[][] m) {

        String mStr = "";
        int rows = m.length;
        int cols = m[0].length;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                mStr += m[row][col];
                mStr += col == cols - 1 ? "" : ";";
            }
            mStr += "\n";
        }

        return mStr;
    }
}
