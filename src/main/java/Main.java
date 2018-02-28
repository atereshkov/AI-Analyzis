import IO.FileReaderI;
import IO.FileReaderImpl;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.net.Inet4Address;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    private static String fileName = "Source.xlsx";
    //private static String fileName = "Source2m.xlsx";
    private static String healthySheet = "ЗДОРОВЫЕ";
    private static String sickSheet = "БОЛЬНЫЕ";
    private static String indicatorsSheet = "Признаки";

    private static int indicatorsCount = 14;
    private static int healthyPeopleCount = 26;
    private static int sickPeopleCount = 28;

    public static void main(String[] args) {
        // Mode:
        // 0 - normal
        // 1 - Euclidean
        runAI(0);
        runAI(1);
    }

    private static void runAI(Integer mode) {
        printModeDivider(mode);

        FileReaderI fileReader = new FileReaderImpl();
        IndicatorProcessor featureSelection = new IndicatorProcessor();

        float[][] healthyMatrix = fileReader.getIndicatorsValues(fileName, healthySheet, indicatorsCount);
        float[][] sickMatrix = fileReader.getIndicatorsValues(fileName, sickSheet, indicatorsCount);
        float[][] jointMatrix = featureSelection.concatMatrixByColumns(healthyMatrix, sickMatrix);

        float[][] normalizedMatrix;
        if (mode == 0) {
            normalizedMatrix = featureSelection.normalize(jointMatrix);
        } else {
            normalizedMatrix = featureSelection.normalizeEuclidean(jointMatrix);
        }
        float[][] healthyNormalizedMatrix = featureSelection.splitMatrix(normalizedMatrix, healthyPeopleCount, 0, healthyPeopleCount);
        float[][] sickNormalizedMatrix = featureSelection.splitMatrix(normalizedMatrix, sickPeopleCount, healthyPeopleCount, healthyPeopleCount + sickPeopleCount);

        List<String> indicators = fileReader.getIndicators(fileName, indicatorsSheet, indicatorsCount);

        System.out.println("Здоровые");
        ConsoleOutput.print(healthyMatrix, true, indicators, true);

        System.out.println("");
        System.out.println("Больные");
        ConsoleOutput.print(sickMatrix, true, indicators, false);


        System.out.println("");
        System.out.println("======================================================================");
        System.out.println("");
        System.out.println("Здоровые (норм.)");
        ConsoleOutput.print(healthyNormalizedMatrix, true, indicators, true);

        System.out.println("");
        System.out.println("Больные (норм.)");
        ConsoleOutput.print(sickNormalizedMatrix, true, indicators, false);

        /*
            Distance - разница между наибольшим здоровым и наименьшим больным.
            Данная величина показывает степень "существенного" различия в рассматриваемых выборках (признаках).
            Чем меньше значение (больше разница между больными), тем информативнее признак.
        */
        System.out.println("");
        System.out.println("Коэффициенты:");
        Float[] coefficients = featureSelection.getDistance(healthyNormalizedMatrix, sickNormalizedMatrix, indicators, healthyMatrix, sickMatrix);

        Map<String, Float> unsortedMap = featureSelection.toHashMap(indicators, coefficients);
        Map<String, Float> sortedMap = unsortedMap.entrySet().stream()
                //.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));

        ConsoleOutput.printResults(sortedMap);
    }

    private static void printModeDivider(Integer mode) {
        System.out.println();
        System.out.println("======================================================================================");
        if (mode == 0) {
            System.out.println("Mode normal");
        } else if (mode == 1) {
            System.out.println("Mode Euclidean");
        }
        System.out.println();
    }

}
