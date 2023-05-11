package org.restrata.s3Operations;


import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.sqs.AmazonSQS;
import lombok.Getter;
import lombok.Setter;

import javax.jms.Session;

@Getter
@Setter
public class S3Configurations {
    private String bucketName;
    private Regions bucketRegion;
    private String dirUpload;
    private String dirStatus;
    private String dirProcessed;
    private String dirError;

    private String snsTopicARN ;
    private String sqsQueueARN ;
    private String sqsQueueName ;

    private Session session;
    private SQSConnectionFactory connectionFactory;
    private SQSConnection connection;
    private AmazonSQS sqsClient;
    private AmazonS3 s3client;

    public S3Configurations(String bucketName, Regions bucketRegion, String dirUpload, String dirStatus, String dirProcessed, String dirError, String snsTopicARN, String sqsQueueARN, String sqsQueueName, Session session, SQSConnectionFactory connectionFactory, SQSConnection connection, AmazonSQS sqsClient, AmazonS3 s3client) {
        this.bucketName = bucketName;
        this.bucketRegion = bucketRegion;
        this.dirUpload = dirUpload;
        this.dirStatus = dirStatus;
        this.dirProcessed = dirProcessed;
        this.dirError = dirError;
        this.snsTopicARN = snsTopicARN;
        this.sqsQueueARN = sqsQueueARN;
        this.sqsQueueName = sqsQueueName;
        this.session = session;
        this.connectionFactory = connectionFactory;
        this.connection = connection;
        this.sqsClient = sqsClient;
        this.s3client = s3client;
    }
}
