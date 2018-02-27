import IO.FileReaderI;
import IO.FileReaderImpl;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    private static String fileName = "Source.xlsx";
    private static String fileName2 = "Source2m.xlsx";
    private static String healthySheet = "ЗДОРОВЫЕ";
    private static String sickSheet = "БОЛЬНЫЕ";
    private static String indicatorsSheet = "Признаки";

    private static int indicatorsCount = 14;
    private static int healthyPeopleCount = 26;
    private static int sickPeopleCount = 28;

    public static void main(String[] args) {
        FileReaderI fileReader = new FileReaderImpl();
        FeatureSelection featureSelection = new FeatureSelection();

        float[][] healthyMatrix = fileReader.getIndicatorsValues(fileName, healthySheet, indicatorsCount);
        float[][] sickMatrix = fileReader.getIndicatorsValues(fileName, sickSheet, indicatorsCount);
        float[][] jointMatrix = featureSelection.concatMatrixByColumns(healthyMatrix, sickMatrix);
        float[][] normalizedMatrix = featureSelection.normalize(jointMatrix);
        float[][] healthyNormalizedMatrix = featureSelection.splitMatrix(normalizedMatrix, healthyPeopleCount, 0, healthyPeopleCount);
        float[][] sickNormalizedMatrix = featureSelection.splitMatrix(normalizedMatrix, sickPeopleCount, healthyPeopleCount, healthyPeopleCount + sickPeopleCount);

        List<String> indicators = fileReader.getIndicators(fileName, indicatorsSheet, indicatorsCount);

        System.out.println("Здоровые");
        print(healthyMatrix, true, indicators, true);

        System.out.println("");
        System.out.println("Больные");
        print(sickMatrix, true, indicators, false);


        System.out.println("");
        System.out.println("======================================================================");
        System.out.println("");
        System.out.println("Здоровые (норм.)");
        print(healthyNormalizedMatrix, true, indicators, true);

        System.out.println("");
        System.out.println("Больные (норм.)");
        print(sickNormalizedMatrix, true, indicators, false);

        /*
            Коэффициенты близости - разница между наибольшим здоровым и наименьшим больным.
            Данная величина показывает степень "существенного" различия в рассматриваемых выборках (признаках).
            Чем меньше значение, тем информативнее признак.
            То есть значение 0.2 говорит о том, что этот признак информативнее, чем признак со значением 0.6.
        */
        System.out.println("");
        System.out.println("Коэффициенты:");
        Float[] coefficients = featureSelection.getDistanceCoefficients(healthyNormalizedMatrix, sickNormalizedMatrix, indicators);

        Map<String, Float> unsortedMap = featureSelection.toHashMap(indicators, coefficients);
        Map<String, Float> sortedMap = unsortedMap.entrySet().stream()
                //.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));

        printResults(sortedMap);
    }

    static void printResults(Map<String, Float> map) {
        DecimalFormat df = new DecimalFormat("#0.000");
        DecimalFormat df2 = new DecimalFormat("#0.0");

        System.out.println("");
        System.out.println("Результаты (наиболее информативные признаки, > 50%):");

        for (Map.Entry<String, Float> entry : map.entrySet()) {
            String key = entry.getKey();
            Float value = (1 - entry.getValue()) * 100;
            if (entry.getValue() < 0.5) {
                System.out.println(key + ": " + df2.format(value) + "% (" + df.format(entry.getValue()) + ")");
            }
        }

        System.out.println("");
        System.out.println("Результаты (наименее информативные признаки, < 50%):");

        for (Map.Entry<String, Float> entry : map.entrySet()) {
            String key = entry.getKey();
            Float value = (1 - entry.getValue()) * 100;
            if (entry.getValue() > 0.5) {
                System.out.println(key + ": " + df2.format(value) + "% (" + df.format(entry.getValue()) + ")");
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
