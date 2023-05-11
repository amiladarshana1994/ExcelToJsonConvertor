package org.restrata.s3Operations;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.EnumSet;

@Component
@EnableScheduling
public class FileOperationsReadFromS3 {

    @Value("${s3.bucket.accessKey}")
    private String accessKey;
    @Value("${s3.bucket.secretKey}")
    private String secretKey;
    @Value("${s3.bucket.bucketName}")
    private String bucketName;
    @Value("${s3.bucket.bucketRegion}")
    private Regions bucketRegion;
    @Value("${s3.bucket.dirUpload}")
    private String dirUpload;
    @Value("${s3.bucket.dirStatus}")
    private String dirStatus;
    @Value("${s3.bucket.dirProcessed}")
    private String dirProcessed;
    @Value("${s3.bucket.dirError}")
    private String dirError;

    @Value("${s3.sns.topic.arn}")
    private String snsTopicARN ;
    @Value("${s3.sns.queue.arn}")
    private String sqsQueueARN ;

    @PostConstruct
    public void init() {
        //connectToAws();
    }
    AmazonS3 s3client;
    private void connectToAws(){
        AWSCredentials credentials = new BasicAWSCredentials(accessKey,secretKey);
        s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(bucketRegion)
                .build();
        System.out.println("connected to s3 bucket "+accessKey+" "+secretKey);
    }

    private void enableNotificationOnABucket(){
        try {
            BucketNotificationConfiguration notificationConfiguration = new BucketNotificationConfiguration();

            // Add an SNS topic notification.
            notificationConfiguration.addConfiguration("snsTopicConfig",
                    new TopicConfiguration(snsTopicARN, EnumSet.of(S3Event.ObjectCreated)));

            // Add an SQS queue notification.
            notificationConfiguration.addConfiguration("sqsQueueConfig",
                    new QueueConfiguration(sqsQueueARN, EnumSet.of(S3Event.ObjectCreated)));

            // Create the notification configuration request and set the bucket notification configuration.
            SetBucketNotificationConfigurationRequest request = new SetBucketNotificationConfigurationRequest(
                    bucketName, notificationConfiguration);
            s3client.setBucketNotificationConfiguration(request);
            System.out.println("enable SNS topic notification "+snsTopicARN);
            System.out.println("enable SQS queue notification "+sqsQueueARN);
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }
    }

    //@Scheduled(fixedRate = 10000)
    private void readForNewRiskFileUploadsAndProcess(){
        if(s3client == null)connectToAws();
        System.out.println("accessing bucket "+bucketName+" "+dirUpload);
        getFileObjectsInDir().getObjectSummaries().forEach(obj -> {
            new S3ProcessUploadedFile().readForNewRiskFileUploadsAndProcessByFileKey(s3client, bucketName, dirUpload, dirProcessed, dirError, obj.getKey());
        });
    }
    private ObjectListing getFileObjectsInDir() {
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
                .withBucketName(bucketName)
                .withPrefix(dirUpload + "/");
        return s3client.listObjects(listObjectsRequest);
    }

    private void gets3BucketByNameAndProcess() {
        Bucket uploads = selectThes3Bucket(s3client);
        if(uploads!=null){
            System.out.println(uploads);
            ObjectListing objectListing = s3client.listObjects(bucketName);
            for(S3ObjectSummary os : objectListing.getObjectSummaries()) {
                System.out.println(os);
            }
        }
    }

    private Bucket selectThes3Bucket(AmazonS3 s3client) {
        return s3client.listBuckets().stream()
                .filter(bucket -> bucket.getName().equals(bucketName))
                .findFirst()
                .orElse(null);
    }
}