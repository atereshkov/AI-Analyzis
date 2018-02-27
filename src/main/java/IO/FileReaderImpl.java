package IO;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class FileReaderImpl implements FileReaderI {

    public float[][] getIndicatorsValues(String fileName, String sheetName, int indicatorsCount) {
        XSSFSheet excelSheet = null;

        try {
            XSSFWorkbook excelBook = new XSSFWorkbook(new FileInputStream(fileName));
            excelSheet = excelBook.getSheet(sheetName);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        Integer peopleCount = excelSheet.getPhysicalNumberOfRows() - 1;
        float[][] resultMatrix = new float[indicatorsCount][peopleCount];

        for (int i = 0; i < indicatorsCount; i++) {
            for (int j = 0; j < peopleCount; j++) {
                XSSFRow row = excelSheet.getRow(j + 1);
                resultMatrix[i][j] = Float.parseFloat(row.getCell(i + 1).getRawValue());
            }

        }

        return resultMatrix;
    }

    public List<String> getIndicators(String fileName, String sheetName, int indicatorsCount) {
        XSSFSheet excelSheet = null;

        try {
            XSSFWorkbook excelBook = new XSSFWorkbook(new FileInputStream(fileName));
            excelSheet = excelBook.getSheet(sheetName);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        List<String> indicators = new ArrayList<>();

        for (int i = 0; i < indicatorsCount; i++) {
            XSSFRow row = excelSheet.getRow(i + 1);
            indicators.add(row.getCell(0).toString());
        }

        return indicators;
    }

}
