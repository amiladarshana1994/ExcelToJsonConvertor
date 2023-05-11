package org.restrata.utils;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Convertor {
    public List<JSONObject> convertExcelToJsonFromFilePath(String filePath){

        try {
            FileInputStream dataStream = new FileReader().readFileFromPath(filePath);
            return convertFileInputStreamToJsonList(dataStream);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<JSONObject> convertExcelToJsonFromMultiPartFile(MultipartFile excel){
        try {
            InputStream dataStream = excel.getInputStream();
            return convertInputStreamToJsonList(dataStream);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<JSONObject> convertS3ObjectInputStreamToJsonList(S3ObjectInputStream dataStream) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(dataStream);
        return convertWorkBookToJsonList(workbook);
    }

    private List<JSONObject> convertFileInputStreamToJsonList(FileInputStream dataStream) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(dataStream);
        return convertWorkBookToJsonList(workbook);
    }

    private List<JSONObject> convertInputStreamToJsonList(InputStream dataStream) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(dataStream);
        return convertWorkBookToJsonList(workbook);
    }

    private static List<JSONObject> convertWorkBookToJsonList(XSSFWorkbook workbook) {
        List<JSONObject> results = new ArrayList<>();
        XSSFSheet sheet = workbook.getSheetAt(0);
        XSSFRow header = sheet.getRow(0);
        int propertyCount = header.getPhysicalNumberOfCells();

        for(int i=1; i<sheet.getPhysicalNumberOfRows();i++) {
            try {
                XSSFRow row = sheet.getRow(i);
                JSONObject rowJson = new JSONObject();
                for(int j=0;j<propertyCount;j++) {
                    rowJson.put(
                            header.getCell(j).toString().toLowerCase()
                                    .replaceAll(" ","_")
                                    .replaceAll("-","_")
                                    .replaceAll("/","_"),
                            row.getCell(j).toString());
                }
                results.add(rowJson);
            }catch (Exception ignore){}
        }
        return results;
    }
}