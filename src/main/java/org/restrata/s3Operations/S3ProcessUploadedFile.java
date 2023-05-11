package org.restrata.s3Operations;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.json.JSONObject;
import org.restrata.utils.Convertor;
import org.restrata.utils.FileTypeChecker;

import java.util.List;

public class S3ProcessUploadedFile {
    public void readForNewRiskFileUploadsAndProcessByFileKey(AmazonS3 s3client, String bucketName, String dirUpload, String dirProcessed, String dirError, String fileKey) {
        if(!fileKey.equals(dirUpload + "/") && new FileTypeChecker().isExcelFile(fileKey)){
            S3Object s3object = s3client.getObject(bucketName, fileKey);
            S3ObjectInputStream inputStream = s3object.getObjectContent();
            try {
                List<JSONObject> dalaList = new Convertor().convertS3ObjectInputStreamToJsonList(inputStream);
                System.out.println("stream object from bucket "+bucketName+" "+ fileKey);
                dalaList.forEach(value-> {
                    System.out.println(value.toString());
                });
                moveFileToAnotherDir(s3client, bucketName, dirUpload, dirProcessed, fileKey);
            }
            catch (Exception e){
                moveFileToAnotherDir(s3client, bucketName, dirUpload, dirError, fileKey);
                System.out.println("stream object from bucket "+bucketName+" "+ fileKey+" exception "+e.getMessage());
                e.printStackTrace();
            }
        }
        else if(!fileKey.equals(dirUpload + "/")){
            moveFileToAnotherDir(s3client, bucketName, dirUpload, dirError, fileKey);
            System.out.println("stream object from bucket " + bucketName + " " + fileKey + " not an excel file");
        }
    }

    private void moveFileToAnotherDir(AmazonS3 s3client, String bucketName, String fromDir, String toDir, String fileAbsName) {
        String fileName = getFileNameFromPath(fileAbsName);
        System.out.println("moveFileToAnotherDir : bucketName = " + bucketName + ", fromDir = " + fromDir + ", toDir = " + toDir + ", fileName = " + fileName);
        s3client.copyObject(
                bucketName,
                fromDir + "/" + fileName,
                bucketName,
                toDir + "/" + fileName
        );
        s3client.deleteObject(bucketName, fileAbsName);
    }

    private String getFileNameFromPath(String key){
        if(key!=null){
            String[] data = key.split("/");
            if(data.length>1) return data[data.length-1];
        }
        return key;
    }
}
