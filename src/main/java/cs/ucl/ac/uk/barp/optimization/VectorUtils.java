package cs.ucl.ac.uk.barp.optimization;

import java.util.ArrayList;

/**
 * @author David Stefan
 */
public class VectorUtils {
    
    public static int[] unique(int[] v) {
        ArrayList<Integer> unique = new ArrayList<>();
        unique.add(v[0]);
        for (int i = 1; i < v.length; i++) {
            if (unique.get(unique.size() - 1) != v[i]) {
                unique.add(v[i]);
            }
        }
        int[] u = new int[unique.size()];
        for (int i = 0; i < unique.size(); i++) {
            u[i] = unique.get(i);
        }
        return u;
    }

    public static double[] diff(double[] a, double[] b) {
        double[] c = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            c[i] = a[i] - b[i];
        }
        return c;
    }

    public static double[] add(double[] a, double[] b) {
        double[] c = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            c[i] = a[i] + b[i];
        }
        return c;
    }

    public static double[] div(double[] a, double[] b) {
        double[] c = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            c[i] = a[i] / b[i];
        }
        return c;
    }

    public static double[] div(double[] a, double b) {
        double[] c = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            c[i] = a[i] / b;
        }
        return c;
    }

    public static int whichMin(double[] v) {
        int which = 0;
        for (int i = 1; i < v.length; i++) {
            if (v[which] > v[i]) {
                which = i;
            }
        }
        return which;
    }

    public static int whichMax(double[] v) {
        int which = 0;
        for (int i = 1; i < v.length; i++) {
            if (v[which] < v[i]) {
                which = i;
            }
        }
        return which;
    }

    public static double[] cumsum(double[] v) {
        double[] sum = new double[v.length];
        double value = 0.0;
        for (int i = 0; i < v.length; i++) {
            sum[i] = v[i] + value;
            value = sum[i];
        }
        return sum;
    }

    public static double[] rowMax(double[][] m) throws Exception {

        if (m.length == 0 || m[0].length == 0) {
            throw new Exception("Nothing to compare.");
        }

        int rows = m.length;
        int cols = m[0].length;
        double[] res = new double[rows];

        for (int row = 0; row < rows; row++) {
            double val = m[row][0];
            for (int col = 1; col < cols; col++) {
                val = Math.max(val, m[row][col]);
            }
            res[row] = val;
        }

        return res;
    }

    public static double[] colMax(double[][] m) throws Exception {

        if (m.length == 0 || m[0].length == 0) {
            throw new Exception("Nothing to compare.");
        }

        int rows = m.length;
        int cols = m[0].length;
        double[] res = new double[cols];

        for (int col = 0; col < cols; col++) {
            double val = m[0][col];
            for (int row = 1; row < rows; row++) {
                val = Math.max(val, m[row][col]);
            }
            res[col] = val;
        }

        return res;
    }

    public static double[] colMeans(double[][] m) throws Exception {

        if (m.length == 0 || m[0].length == 0) {
            throw new Exception("Nothing to compare.");
        }

        int rows = m.length;
        int cols = m[0].length;
        double[] means = new double[cols];
        double sum;

        for (int col = 0; col < cols; col++) {
            sum = 0;
            for (int row = 0; row < rows; row++) {
                sum += m[row][col];
            }
            means[col] = sum / rows;
        }

        return means;
    }

    public static double[] rowMeans(double[][] m) throws Exception {

        if (m.length == 0 || m[0].length == 0) {
            throw new Exception("Nothing to compare.");
        }

        int rows = m.length;
        double[] means = new double[rows];

        for (int row = 0; row < rows; row++) {
            means[row] = mean(m[row]);
        }

        return means;
    }

    public static double mean(double[] v) {

        int N = 0;
        double sum = 0;

        for (; N < v.length; N++) {
            sum += v[N];
        }

        return sum / N;
    }

    public static double max(double[] v) {

        double res = v[0];

        for (int N = 0; N < v.length; N++) {
            res = Math.max(res, v[N]);
        }

        return res;
    }

    public static double[][] transpose(double[][] m) {

        if (m.length == 0) {
            // Nothing to transpose
            return m;
        }

        int rows = m.length;
        int cols = m[0].length;

        double[][] tm = new double[cols][rows];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                tm[col][row] = m[row][col];
            }
        }

        return tm;
    }
    
    public static void quickSort(int[] v) {
        int left = 0;
        int right = v.length - 1;
        quickSort(v, left, right);
    }
    
    private static void quickSort(int[] v, int left, int right) {

        int i = left;
        int j = right;
        double item1;

        item1 = v[(left + right) / 2];

        while (i < j) {
            while (v[i] < item1 && i < right) {
                i++;
            }
            while (v[j] > item1 && j > left) {
                j--;
            }
            if (i < j) {
                swap(v, i, j);
            }
            if (i <= j) {
                i++;
                j--;
            }
        }
        if (j > left) {
            quickSort(v, left, j);
        }
        if (i < right) {
            quickSort(v, i, right);
        }
    }

    public static void quickSort(double[] v) {
        int left = 0;
        int right = v.length - 1;
        quickSort(v, left, right);
    }

    private static void quickSort(double[] v, int left, int right) {

        int i = left;
        int j = right;
        double item1;

        item1 = v[(left + right) / 2];

        while (i < j) {
            while (v[i] < item1 && i < right) {
                i++;
            }
            while (v[j] > item1 && j > left) {
                j--;
            }
            if (i < j) {
                swap(v, i, j);
            }
            if (i <= j) {
                i++;
                j--;
            }
        }
        if (j > left) {
            quickSort(v, left, j);
        }
        if (i < right) {
            quickSort(v, i, right);
        }
    }

    public static void quickSortPivot(double[] v, double[][] m) {
        int left = 0;
        int right = v.length - 1;
        quickSort(v, m, left, right);
    }

    private static void quickSort(double[] v, double[][] m, int left, int right) {

        int i = left;
        int j = right;
        double item1;

        item1 = v[(left + right) / 2];

        while (i < j) {
            while (v[i] < item1 && i < right) {
                i++;
            }
            while (v[j] > item1 && j > left) {
                j--;
            }
            if (i < j) {
                swap(v, i, j);
                swap(m, i, j);
            }
            if (i <= j) {
                i++;
                j--;
            }
        }
        if (j > left) {
            quickSort(v, m, left, j);
        }
        if (i < right) {
            quickSort(v, m, i, right);
        }
    }

    private static void swap(double[][] m, int i, int j) {

        int rows = m.length;
        double temp;

        for (int row = 0; row < rows; row++) {
            temp = m[row][i];
            m[row][i] = m[row][j];
            m[row][j] = temp;
        }
    }

    private static void swap(double[] v, int i, int j) {
        double temp = v[i];
        v[i] = v[j];
        v[j] = temp;
    }
    
    private static void swap(int[] v, int i, int j) {
        int temp = v[i];
        v[i] = v[j];
        v[j] = temp;
    }

    /**
     * Produce a string representation of given vector.
     *
     * @param v
     * @return A string representation of given vector.
     */
    public static String vectorToString(double[] v) {

        String vStr = "";
        int elems = v.length;

        for (int i = 0; i < elems; i++) {
            vStr += v[i];
            vStr += i == elems - 1 ? "" : ";";
        }

        return vStr;
    }

    public static double[] zeros(int length) {
        return fill(0, length);
    }

    public static double[] fill(double val, int length) {
        double[] v = new double[length];
        for (int i = 0; i < length; i++) {
            v[i] = 0;
        }
        return v;
    }

    public static double[][] fill(double val, int rows, int cols) {

        double[][] m = new double[rows][cols];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                m[row][col] = val;
            }
        }
        return m;
    }

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
