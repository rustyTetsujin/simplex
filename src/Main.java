import org.apache.commons.math4.legacy.linear.RealMatrix;
import org.apache.commons.math4.legacy.linear.MatrixUtils;

public class Main {
    public static void main(String[] args) {
        double[][] tableau = {{10, 5, 30, 1, 0, 150}, {50, 200, 400, 0, 1, 1000}, {50, 150, 500, 0, 0, 0}};

        // Apply the simplex algorithm to the tableau
        RealMatrix resultMatrix = simplexAlgorithm(tableau);

        // Retrieve the results from the result matrix
        double maxProfit = resultMatrix.getEntry(resultMatrix.getRowDimension() - 1, resultMatrix.getColumnDimension() - 1) * -1;
        double variableA = resultMatrix.getEntry(0, resultMatrix.getColumnDimension() - 1); // Spalten Check einbauen (wenn nur eine 1 und sonst 0, dann Wert der letzten Spalte, sonst 0)
        double variableB = resultMatrix.getEntry(1, resultMatrix.getColumnDimension() - 1); // Spalten Check einbauen
        double variableC = resultMatrix.getEntry(2, resultMatrix.getColumnDimension() - 1); // Spalten Check einbauen

        // Print the results
        System.out.println("Der Maximale Gewinn ist " + maxProfit + ", wenn von  Teddybär1 " + variableA + " Einheiten, von  Teddybär2 " + variableB + " Einheiten und von  Teddybär3 " + variableC + " Einheiten produziert werden.");
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
                //if (column[i] > 0 && ratio < minRatio) {
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
