package org.restrata.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class FileReader {
    public FileInputStream readFileFromPath(String filePath) throws FileNotFoundException {
        return new FileInputStream(new File(filePath));
    }


}
