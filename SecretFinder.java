import java.io.FileReader;
import java.util.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


/**
 * This program reads polynomial root points from a JSON file,
 * decodes the y-values from various bases,
 * and solves for the constant term (secret) of the polynomial
 * using the matrix method (linear system solving with Gaussian elimination).
 */
public class SecretFinder {

    /** 
     * Class to store a point (x, y) 
     */
    static class Point {
        int x;
        long y;

        Point(int x, long y) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * Convert the y-value from its string representation and base to a decimal long.
     * @param value The string representation of the value (e.g., "111")
     * @param base The base in which the value is represented (e.g., 2)
     * @return The decoded number as a decimal long
     */
    static long decodeValue(String value, int base) {
        return Long.parseLong(value, base);
    }

    /**
     * Read all points from the JSON input file.
     * Also reads 'k' (minimum points needed to solve the polynomial).
     
     */
    static List<Point> readPoints(String filename, int[] kHolder) throws Exception {
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(new FileReader(filename));
        JSONObject keys = (JSONObject) json.get("keys");
        
        int n = ((Long) keys.get("n")).intValue();
        int k = ((Long) keys.get("k")).intValue();
        kHolder[0] = k;

        List<Point> points = new ArrayList<>();

        // Iterate over all keys except "keys" to gather points
        for (Object keyObj : json.keySet()) {
            String key = keyObj.toString();
            if ("keys".equals(key)) continue;

            JSONObject pointData = (JSONObject) json.get(key);
            int x = Integer.parseInt(key);
            int base = Integer.parseInt(pointData.get("base").toString());
            String value = pointData.get("value").toString();

            long y = decodeValue(value, base);
            points.add(new Point(x, y));
        }

        return points;
    }

    static long[] solveByMatrix(List<Point> points) {
        int n = points.size();  // Should be 3 for quadratic polynomial
        double[][] A = new double[n][n];
        double[] B = new double[n];

        for (int i = 0; i < n; i++) {
            int x = points.get(i).x;
            B[i] = points.get(i).y;
            A[i][0] = x * x;  // a*x^2 coefficient
            A[i][1] = x;      // b*x coefficient
            A[i][2] = 1;      // c coefficient (constant term)
        }

        double[] solution = gaussianElimination(A, B);

        long[] result = new long[n];
        for (int i = 0; i < n; i++) {
            result[i] = Math.round(solution[i]);
        }
        return result;
    }

    
    static double[] gaussianElimination(double[][] A, double[] B) {
        int n = B.length;

        // Forward elimination phase
        for (int i = 0; i < n; i++) {
            // Partial pivoting
            int maxRow = i;
            for (int j = i + 1; j < n; j++) {
                if (Math.abs(A[j][i]) > Math.abs(A[maxRow][i])) {
                    maxRow = j;
                }
            }

            // Swap rows if needed
            double[] tempRow = A[i];
            A[i] = A[maxRow];
            A[maxRow] = tempRow;

            double tempVal = B[i];
            B[i] = B[maxRow];
            B[maxRow] = tempVal;

            // Eliminate below
            for (int j = i + 1; j < n; j++) {
                double factor = A[j][i] / A[i][i];
                B[j] -= factor * B[i];
                for (int k = i; k < n; k++) {
                    A[j][k] -= factor * A[i][k];
                }
            }
        }

        // Back substitution phase
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double sum = B[i];
            for (int j = i + 1; j < n; j++) {
                sum -= A[i][j] * x[j];
            }
            x[i] = sum / A[i][i];
        }
        return x;
    }

    /**
     * Generate all combinations of size k from the list of points.
   
     */
    static List<List<Point>> combinations(List<Point> points, int k) {
        List<List<Point>> result = new ArrayList<>();
        combineHelper(points, 0, k, new ArrayList<>(), result);
        return result;
    }

    static void combineHelper(List<Point> points, int start, int k, List<Point> current, List<List<Point>> result) {
        if (current.size() == k) {
            result.add(new ArrayList<>(current));
            return;
        }
        for (int i = start; i < points.size(); i++) {
            current.add(points.get(i));
            combineHelper(points, i + 1, k, current, result);
            current.remove(current.size() - 1);
        }
    }

    public static void main(String[] args) throws Exception {
        final String filename = "input.json";  // Name of the JSON file must be here

        int[] kHolder = new int[1];
        List<Point> points = readPoints(filename, kHolder);
        int k = kHolder[0];

        System.out.println("Decoded points from JSON:");
        for (Point p : points) {
            System.out.println("x = " + p.x + ", y = " + p.y);
        }
        System.out.println("Number of points required (k): " + k);

        // Generate all combinations of points with size k
        List<List<Point>> allCombinations = combinations(points, k);

        Set<Long> secretCandidates = new HashSet<>();

        System.out.println("\nSolving for secret constant (c) using matrix method on all combinations:");

        for (List<Point> combo : allCombinations) {
            long[] coeffs = solveByMatrix(combo);
            long c = coeffs[2]; // constant term is c
            secretCandidates.add(c);

            System.out.print("Combination points: ");
            for (Point pt : combo) System.out.print("(" + pt.x + "," + pt.y + ") ");
            System.out.println("Secret c = " + c);
        }

        if (secretCandidates.size() == 1) {
            System.out.println("\nAll combinations gave the SAME secret constant c = " + secretCandidates.iterator().next());
        } else {
            System.out.println("\nDifferent secret constants found: " + secretCandidates);
            System.out.println("Shares might be inconsistent or some might be incorrect.");
        }
    }
}
