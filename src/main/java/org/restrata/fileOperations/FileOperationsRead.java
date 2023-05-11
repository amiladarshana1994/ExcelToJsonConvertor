package org.restrata.fileOperations;

import org.json.JSONObject;
import org.restrata.utils.Convertor;

import java.util.List;

public class FileOperationsRead {
    public static void main(String[] args) {
        String filePath = "C:\\Users\\Dell\\Downloads\\BSOC_samble_database.xlsx";
        List<JSONObject> dalaList = new Convertor().convertExcelToJsonFromFilePath(filePath);
        dalaList.forEach(value-> {
            System.out.println(value.toString());
        });
    }
}