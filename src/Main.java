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

        // Apply the simplex algorithm to the tableau
        RealMatrix resultMatrix = simplexAlgorithm(tableau);

        ArrayList<Double> results = getResults(numVariables, resultMatrix);

        // Retrieve the results from the result matrix
        double maxProfit = resultMatrix.getEntry(resultMatrix.getRowDimension() - 1, resultMatrix.getColumnDimension() - 1) * -1;
        double resultVar1 = results.get(0);
        double resultVar2 = results.get(1);
        double resultVar3 = results.get(2);

        // Print the results
        System.out.println("Der Maximale Gewinn ist " + maxProfit + ", wenn von\n  Teddybär1 " + resultVar1 + " Einheiten, von\n  Teddybär2 " + resultVar2 + " Einheiten und von\n  Teddybär3 " + resultVar3 + " Einheiten produziert werden.");
    }

    private static ArrayList<Double> getResults(int numVariables, RealMatrix resultMatrix) {
        ArrayList<Double> results = new ArrayList<>();

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
