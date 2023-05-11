package org.restrata.fileOperations;

import org.restrata.dto.ConverterResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/file")
public class FileOperationsController {

    @Autowired
    private FileOperationService fileOperationService;

    @PostMapping("/convert")
    ResponseEntity<ConverterResponse> processExcel(@RequestParam("file") MultipartFile excel) {
        return fileOperationService.processExcel(excel);
    }
}
