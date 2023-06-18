import org.apache.commons.math4.legacy.linear.MatrixUtils;
import org.apache.commons.math4.legacy.linear.RealMatrix;
import org.apache.commons.math4.legacy.linear.RealVector;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        // linear system of equations
        double[][] tableau = {{10, 5, 30, 1, 0, 150}, {50, 200, 400, 0, 1, 1000}, {50, 150, 500, 0, 0, 0}};

        // Number of variables to consider
        int numVariables = 3;

        // start variables
        boolean finished = false;
        ArrayList<Double> results = null;
        Double[][] tmpResults = new Double[numVariables][2];
        // fill array
        for (int i = 0; i < tmpResults.length; i++) {
            tmpResults[i][0] = 0.0;
            tmpResults[i][1] = 0.0;
        }

        while (!finished) {

            // Apply the simplex algorithm to the tableau
            RealMatrix resultMatrix = simplexAlgorithm(tableau);

            // Retrieve the results from the result matrix
            results = getResults(numVariables, resultMatrix);
            finished = true;


            // Check if all Values are INT
            for (int i = 1; i < results.size(); i++) {

                // if not INT update tableau
                if (results.get(i) % 1 != 0) {
                    finished = false;

                    // Update resources in tableau
                    // Step 1: Remove used up resources
                    tableau[0][5] = tableau[0][5] - (Math.floor(results.get(i)) * tableau[0][i-1]);
                    tableau[1][5] = tableau[1][5] - (Math.floor(results.get(i)) * tableau[1][i-1]);

                    // Step 2: Update tmpResults
                    // Value-Pair: #Items / #Revenue
                    tmpResults[i-1][0] = Math.floor(results.get(i));
                    tmpResults[i-1][1] = Math.floor(results.get(i)) * tableau[2][i-1];

                    // Step 3: Remove calculated variables
                    tableau[0][i-1] = 0;
                    tableau[1][i-1] = 0;
                    tableau[2][i-1] = 0;

                } else if (results.get(i) % 1 == 0 && results.get(i) > 0) {
                    tmpResults[i-1][0] = results.get(i);
                    tmpResults[i-1][1] = results.get(0);

                }
            }

        }

        // Sum revenue
        double revenue = 0;
        for (Double[] tmpResult : tmpResults) {
            revenue += tmpResult[1];
        }

        // Update revenue in results
        results.set(0, revenue);

        // Update no. of items in results
        for (int i = 1; i < results.size(); i++) {
            results.set(i, tmpResults[i-1][0]);
        }

        // Print the results
        System.out.println("Der Maximale Gewinn ist " + results.get(0) + ", wenn von");

        for (int i = 0; i < numVariables; i++) {
            System.out.println(" Teddybär" + (i+1) + " " + results.get(i+1));
        }

        System.out.println("Einheiten produziert werden.");
    }

    private static ArrayList<Double> getResults(int numVariables, RealMatrix resultMatrix) {
        ArrayList<Double> results = new ArrayList<>();

        // add Maxprofit Element to the list
        results.add((resultMatrix.getEntry(resultMatrix.getRowDimension() - 1,
                resultMatrix.getColumnDimension() - 1)) * -1);

        for (int col = 0; col < numVariables; col++) {
            RealVector columnVector = resultMatrix.getColumnVector(col);
            // saves the row with the 1 in the column (col)
            int targetRow = -1;

            for (int row = 0; row < resultMatrix.getRowDimension() - 1; row++) {
                // Check if value Element is 1 or 0 and not the only 1
                if ((columnVector.getEntry(row) == 1 || columnVector.getEntry(row) == 0) && targetRow == -1) {
                    if (columnVector.getEntry(row) == 1) {
                        targetRow = row;
                    }
                } else {
                    // If there is anything besides 1 and 0 go to the next column
                    break;
                }
            }
            if (targetRow != -1) {
                results.add(resultMatrix.getEntry(targetRow, resultMatrix.getColumnDimension() - 1));
            } else {
                results.add(0.0);
            }
        }
        return results;
    }

    private static RealMatrix simplexAlgorithm(double[][] tableau) {
        // Convert the 2D array to a RealMatrix
        RealMatrix matrix = MatrixUtils.createRealMatrix(tableau);

        boolean finished = false;
        int pivotRow = 0;
        int pivotColumn;
        double pivotElement;

        while (!finished) {
            // Find the pivot column
            pivotColumn = getPivotColumn(matrix);
            double[] lastRow;

            // Find the pivot row
            pivotRow = getPivotRow(matrix, pivotColumn, pivotRow);

            // Calculate the pivot element
            pivotElement = matrix.getEntry(pivotRow, pivotColumn);

            // Update the pivot row
            for (int column = 0; column < matrix.getColumnDimension(); column++) {
                matrix.setEntry(pivotRow, column, matrix.getEntry(pivotRow, column) / pivotElement);
            }

            // Update other rows
            for (int row = 0; row < matrix.getRowDimension(); row++) {
                if (row != pivotRow) {
                    subtractRows(matrix, pivotColumn, pivotRow, row);
                }
            }

            // Check for termination
            finished = true;
            lastRow = matrix.getRow(matrix.getRowDimension() - 1);

            for (double value : lastRow) {
                if (value > 0) {
                    finished = false;
                    break;
                }
            }
        }
        return matrix;
    }

    private static void subtractRows(RealMatrix matrix, int pivotColumn, int pivotRow, int row) {
        double factor = matrix.getEntry(row, pivotColumn);

        for (int column = 0; column < matrix.getColumnDimension(); column++) {
            matrix.setEntry(row, column, matrix.getEntry(row, column) - factor * matrix.getEntry(pivotRow, column));
        }
    }

    private static int getPivotRow(RealMatrix matrix, int pivotColumn, int pivotRow) {
        double[] column = matrix.getColumn(pivotColumn);
        double minRatio = Double.MAX_VALUE;

        for (int i = 0; i < matrix.getRowDimension() - 1; i++) {
            double ratio = matrix.getEntry(i, matrix.getColumnDimension() - 1) / column[i];
            if (column[i] > 0 && ratio < minRatio) {
                minRatio = ratio;
                pivotRow = i;
            }
        }
        return pivotRow;
    }

    private static int getPivotColumn(RealMatrix matrix) {
        int pivotColumn = 0;
        double[] lastRow = matrix.getRow(matrix.getRowDimension() - 1);

        for (int i = 1; i < lastRow.length - 1; i++) {
            if (lastRow[i] > lastRow[pivotColumn]) {
                pivotColumn = i;
            }
        }
        return pivotColumn;
    }

}
