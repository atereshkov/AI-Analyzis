package IO;

import java.util.List;

public interface FileReaderI {

    float[][] getIndicatorsValues(String fileName, String sheetName, int indicatorsCount);
    List<String> getIndicators(String fileName, String sheetName, int indicatorsCount);

}