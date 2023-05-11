package org.restrata.utils;

public class FileTypeChecker {

    public boolean isExcelFile(String fileName){
        if(fileName!=null && (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")))
            return true;
        return false;
    }
}
