import IO.FileReaderI;
import IO.FileReaderImpl;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    private static String fileName = "Source.xlsx";
    private static String healthySheet = "ЗДОРОВЫЕ";
    private static String sickSheet = "БОЛЬНЫЕ";
    private static String indicatorsSheet = "Признаки";

    private static int indicatorsNumber = 14;
    private static int healthyPeopleNumber = 26;
    private static int sickPeopleNumber = 28;

    public static void main(String[] args) {
        FileReaderI fileReader = new FileReaderImpl();
        FeatureSelection featureSelection = new FeatureSelection();

        float[][] healthyMatrix = fileReader.getIndicatorsValues(fileName, healthySheet, indicatorsNumber);
        float[][] sickMatrix = fileReader.getIndicatorsValues(fileName, sickSheet, indicatorsNumber);
        float[][] jointMatrix = featureSelection.concatMatrixByColumns(healthyMatrix, sickMatrix);
        float[][] normalizedMatrix = featureSelection.normalize(jointMatrix);
        float[][] healthyNormalizedMatrix = featureSelection.splitMatrix(normalizedMatrix, healthyPeopleNumber, 0, 26);
        float[][] sickNormalizedMatrix = featureSelection.splitMatrix(normalizedMatrix, sickPeopleNumber, 26, 54);

        /*
            Коэффициенты близости. Данная величина показывает степень совпадения значений одного класса с другим.
            Чем меньше значения, тем информативнее признак.
            То есть значение 0.2 говорит о том, что этот признак информативнее, чем признак со значением 0.6.
        */
        Float[] coeffs = featureSelection.getCompactnessCoefficients(healthyNormalizedMatrix, sickNormalizedMatrix, indicatorsNumber);
        List<String> features = fileReader.getIndicators(fileName, indicatorsSheet, indicatorsNumber);

        Map<String, Float> unsortedMap = featureSelection.toHashMap(features, coeffs);

        Map<String, Float> sortedMap = unsortedMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));

        for (Map.Entry<String, Float> entry : sortedMap.entrySet()) {
            System.out.println(entry.getKey() + ": "+ entry.getValue());
        }
    }
}
