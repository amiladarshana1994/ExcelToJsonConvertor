package org.restrata.s3Operations;

import com.amazon.sqs.javamessaging.AmazonSQSMessagingClientWrapper;
import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;

@Component
public class S3NotificationEventInitializer {
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
    @Value("${s3.sns.queue.name}")
    private String sqsQueueName ;
    Session session;
    SQSConnectionFactory connectionFactory;
    SQSConnection connection;
    AWSCredentials credentials;
    AmazonSQS sqsClient;
    AmazonS3 s3client;
    @PostConstruct
    public void init() {
        connection = createConnection();
        if(connection!=null){
            try {
                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                createQueueConsumerAndAddToListener();
            }
            catch (InterruptedException | javax.jms.JMSException e){
                e.printStackTrace();
            }
        }
    }

    private void createQueueConsumerAndAddToListener() throws InterruptedException, javax.jms.JMSException {
        createSQSQueue(sqsQueueName);
        Queue queue = session.createQueue(sqsQueueName);
        MessageConsumer consumer = session.createConsumer(queue);
        consumer.setMessageListener(new S3NotificationEventListener(new S3Configurations(bucketName, bucketRegion, dirUpload, dirStatus, dirProcessed, dirError, snsTopicARN, sqsQueueARN, sqsQueueName, session, connectionFactory, connection, sqsClient, s3client)));
        // Start receiving incoming messages.
        connection.start();
        // Wait for 1 second. The listener onMessage() method is invoked when a message is received.
        Thread.sleep(1000);
    }

    public SQSConnection createConnection() {
        try {
            credentials = new BasicAWSCredentials(accessKey,secretKey);
            sqsClient = AmazonSQSClientBuilder
                    .standard()
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .withRegion(bucketRegion)
                    .build();
            s3client = AmazonS3ClientBuilder
                    .standard()
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .withRegion(bucketRegion)
                    .build();
            connectionFactory = new SQSConnectionFactory(new ProviderConfiguration(),sqsClient);
            System.out.println("connected to s3 bucket "+accessKey+" "+secretKey);
            return connectionFactory.createConnection();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("connected to s3 bucket "+accessKey+" "+secretKey+" exception "+e.getMessage());
        }
        return null;
    }

    private void createSQSQueue(String queueName){
        // Get the wrapped client
        AmazonSQSMessagingClientWrapper client = connection.getWrappedAmazonSQSClient();

        // Create an SQS queue named MyQueue, if it doesn't already exist
        try {
            if (!client.queueExists(queueName)) {
                client.createQueue(queueName);
                System.out.println("create new queue to s3 bucket "+accessKey+" "+secretKey+" "+queueName);
            }
            else System.out.println("queue exist s3 bucket "+accessKey+" "+secretKey+" "+queueName);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
