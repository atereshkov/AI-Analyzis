public class MatrixHelper {

    public static float getMinValue(float[] row) {
        float min = row[0];

        for (float item : row) {
            if (item < min) {
                min = item;
            }
        }

        return min;
    }

    public static float getMaxValue(float[] row) {
        float max = row[0];

        for (float item : row) {
            if (item > max) {
                max = item;
            }
        }

        return max;
    }

}
