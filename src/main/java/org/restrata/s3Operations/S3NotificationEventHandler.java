package org.restrata.s3Operations;

import com.amazonaws.util.Base64;
import org.json.JSONObject;

import javax.jms.*;

public class S3NotificationEventHandler {
    S3Configurations s3Configurations;

    public S3NotificationEventHandler(S3Configurations s3Configurations) {
        this.s3Configurations = s3Configurations;
    }

    public void handleReceivedQueueMessage(Message message){
        try {
            System.out.println("notification received");
            if( message instanceof TextMessage ) {
                TextMessage txtMessage = ( TextMessage ) message;
                processMessage(new JSONObject(txtMessage.getText()));
            } else if( message instanceof BytesMessage){
                BytesMessage byteMessage = ( BytesMessage ) message;
                byte[] bytes = new byte[(int)byteMessage.getBodyLength()];
                byteMessage.readBytes(bytes);
                System.out.println( "BytesMessage\t" +  Base64.encodeAsString( bytes ) );
            } else if( message instanceof ObjectMessage) {
                ObjectMessage objMessage = (ObjectMessage) message;
                System.out.println( "ObjectMessage\t" + objMessage.getObject() );
            }
        } catch (JMSException e) {
            System.out.println("notification received and process exception "+e.getMessage());
            e.printStackTrace();
        }
    }

    private void processMessage(JSONObject message){
        try {
            System.out.println("process start ");
            message.getJSONArray("Records").forEach(record -> {
                JSONObject recordObj = (JSONObject)record;
                if(recordObj.getString("eventName").equals("ObjectCreated:Put"))
                    processUploadFileEvent(recordObj.getJSONObject("s3").getJSONObject("object"));
            });
        }
        catch (Exception e){
            System.out.println("process exception "+e.getMessage());
            e.printStackTrace();
        }
    }

    private void processUploadFileEvent(JSONObject s3Upload){
        String objectKey = s3Upload.getString("key");
        System.out.println("processUploadFileEvent foe key : "+objectKey);
        new S3ProcessUploadedFile().readForNewRiskFileUploadsAndProcessByFileKey(
                this.s3Configurations.getS3client(),
                this.s3Configurations.getBucketName(),
                this.s3Configurations.getDirUpload(),
                this.s3Configurations.getDirProcessed(),
                this.s3Configurations.getDirError(),
                objectKey);
    }
}
