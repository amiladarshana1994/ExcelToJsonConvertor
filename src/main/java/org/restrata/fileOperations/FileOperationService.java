package org.restrata.fileOperations;

import org.json.JSONArray;
import org.json.JSONObject;
import org.restrata.dto.ConverterResponse;
import org.restrata.utils.Convertor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class FileOperationService {
    public ResponseEntity<ConverterResponse> processExcel(MultipartFile excel){
        try {
            List<JSONObject> dalaList = new Convertor().convertExcelToJsonFromMultiPartFile(excel);
            List<String> stringList = new ArrayList<>();
            dalaList.forEach(value-> {
                stringList.add(value.toString());
                System.out.println(value.toString());
            });
            return new ResponseEntity<>(
                    ConverterResponse.builder()
                            .jsonObjects(stringList)
                            .build()
                    , HttpStatus.CREATED);
        }
        catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
