import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeatureSelection {

    Float[] getCompactnessCoefficients(float[][] healthyMatrix, float[][] sickMatrix, int featuresNumber) {
        Float[] coeffs = new Float[featuresNumber];

        for (int i = 0; i < featuresNumber; i++) {
            float healthyMax = getMaxValue(healthyMatrix[i]);
            float sickMin = getMinValue(sickMatrix[i]);
            coeffs[i] = healthyMax - sickMin;
        }

        return coeffs;
    }

    float[][] splitMatrix(float[][] matrix, int columnsNumber, int startIndex, int endIndex) {
        int rowsNumber = matrix.length;
        float[][] newMatrix = new float[rowsNumber][columnsNumber];

        for (int i = 0; i < rowsNumber; i++) {
            newMatrix[i] = Arrays.copyOfRange(matrix[i], startIndex, endIndex);
        }

        return newMatrix;
    }

    float[][] matrixNormalization(float[][] matrix) {
        int rows = matrix.length;
        int columns = matrix[0].length;
        float[][] newMatrix = new float[rows][columns];

        for (int i = 0; i < rows; i++) {
            float min = getMinValue(matrix[i]);
            float max = getMaxValue(matrix[i]);
            for (int j = 0; j < columns; j++) {
                newMatrix[i][j] = (matrix[i][j] - min) / (max - min);
            }
        }

        return newMatrix;
    }

    float[][] concatMatrixByColumns(float[][] firstMatrix, float[][] secondMatrix) {
        int newLength = firstMatrix[0].length + secondMatrix[0].length;
        int rowsNumber = firstMatrix.length;
        float[][] newMatrix = new float[rowsNumber][newLength];

        for (int i = 0; i < rowsNumber; i++) {
            float[] resultArray = new float[newLength];
            System.arraycopy(firstMatrix[i], 0, resultArray, 0, firstMatrix[i].length);
            System.arraycopy(secondMatrix[i], 0, resultArray, firstMatrix[i].length, secondMatrix[i].length);
            newMatrix[i] = resultArray;
        }

        return newMatrix;
    }

    Map<String, Float> convertToHashMap(List<String> indicators, Float[] coeffs) {
        Map<String, Float> map = new HashMap<>();

        for (int i = 0; i < indicators.size(); i++) {
            map.put(indicators.get(i), coeffs[i]);
        }

        return map;
    }

    private float getMinValue(float[] row) {
        float min = row[0];

        for (float el : row) {
            if (el < min) {
                min = el;
            }
        }

        return min;
    }

    private float getMaxValue(float[] row) {
        float max = row[0];

        for (float el : row) {
            if (el > max) {
                max = el;
            }
        }

        return max;
    }

}
