package org.restrata.s3Operations;

import javax.jms.Message;
import javax.jms.MessageListener;

public class S3NotificationEventListener implements MessageListener {

    S3NotificationEventHandler handler;

    public S3NotificationEventListener(S3Configurations s3Configurations) {
        this.handler = new S3NotificationEventHandler(s3Configurations);
    }

    @Override
    public void onMessage(Message message) {
        this.handler.handleReceivedQueueMessage(message);
    }
}

