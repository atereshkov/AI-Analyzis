import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

public class ConsoleOutput {

    static void printResults(Map<String, Float> map) {
        DecimalFormat df = new DecimalFormat("#0.000");
        DecimalFormat df2 = new DecimalFormat("#0.0");

        System.out.println("");
        System.out.println("Результаты (наиболее информативные признаки, > 50%):");

        for (Map.Entry<String, Float> entry : map.entrySet()) {
            String key = entry.getKey();
            Float value = (1 - entry.getValue()) * 100;
            if (entry.getValue() < 0.5) {
                System.out.println(key + ": " + df2.format(value) + "% (diff: " + df.format(entry.getValue()) + ")");
            }
        }

        System.out.println("");
        System.out.println("Результаты (наименее информативные признаки, < 50%):");

        for (Map.Entry<String, Float> entry : map.entrySet()) {
            String key = entry.getKey();
            Float value = (1 - entry.getValue()) * 100;
            if (entry.getValue() > 0.5) {
                System.out.println(key + ": " + df2.format(value) + "% (diff: " + df.format(entry.getValue()) + ")");
            }
        }
    }

    static void print(float[][] matrix, Boolean format, List<String> indicators, Boolean health) {
        DecimalFormat df = new DecimalFormat("#0.00");
        Integer i = 0;
        for (float[] row : matrix) {
            System.out.print(indicators.get(i) + " ");
            for (float item : row) {
                if (format) {
                    System.out.print(df.format(item) + " ");
                } else {
                    System.out.print(item + " ");
                }
            }
            if (health) {
                System.out.print(" (max: " + MatrixHelper.getMaxValue(row)  + ")");
            } else {
                System.out.print(" (min: " + MatrixHelper.getMinValue(row)  + ")");
            }
            System.out.println();
            i++;
        }
    }

}
