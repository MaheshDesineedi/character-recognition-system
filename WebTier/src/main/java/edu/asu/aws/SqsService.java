package edu.asu.aws;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.*;
import edu.asu.utils.Properties;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqsService {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
    static Properties prop = new Properties();
    public void publish(String fileName, String message) {

        AmazonSQS sqs = AmazonSQSClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .build();

        final Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
        messageAttributes.put("filename", new MessageAttributeValue()
                .withDataType("String")
                .withStringValue(fileName));

        SendMessageRequest msgReq = new SendMessageRequest()
                .withQueueUrl(prop.getMsgReqQueue())
                .withMessageBody(message)
                .withMessageAttributes(messageAttributes)
                .withDelaySeconds(0);

        sqs.sendMessage(msgReq);
    }

    public String consume() throws InterruptedException {

        AmazonSQS sqs = AmazonSQSClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .build();

        GetQueueAttributesRequest request = new GetQueueAttributesRequest()
                .withQueueUrl(prop.getMsgResQueue())
                .withAttributeNames("ApproximateNumberOfMessages");
        GetQueueAttributesResult result = sqs.getQueueAttributes(request);
        while(true){
            ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(prop.getMsgResQueue())
                    .withMaxNumberOfMessages(1);
            List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
            if (messages.isEmpty()){
                continue;
            } else {
                String res[]=messages.get(0).getBody().split(",");
                DeleteMessageRequest deleteRequest = new DeleteMessageRequest(prop.getMsgResQueue(), messages.get(0).getReceiptHandle());
                sqs.deleteMessage(deleteRequest);
                LocalDateTime now = LocalDateTime.now();
                System.out.println("Classification result at "+dtf.format(now)+" is "+ res[0]+": "+ res[1]);
                return res[0]+": "+ res[1];
            }

        }
    }
}
