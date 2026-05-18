package com.cursopcv.notificationconsumerservice;

import com.cursopcv.notificationcontracts.dto.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.List;

@Component
public class ConsumerListener {

    private final String queueUrl;
    private final SqsClient client;
    private final MailSender mailSender;

    public ConsumerListener(String queueUrl, SqsClient client, MailSender mailSender) {
        this.queueUrl = queueUrl;
        this.client = client;
        this.mailSender = mailSender;
    }

    @Scheduled(fixedRate = 5000)
    public void pollMessages() {
        System.out.println("🔁 Verificando mensagens na fila...");

        List<Message> messages = receiveMessages();

        for (Message message : messages) {
            try {
                mailSender.send(message);

                client.deleteMessage(r -> r
                        .queueUrl(queueUrl)
                        .receiptHandle(message.receiptHandle()));

                System.out.println("Message consumed:" + message.messageId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private List<Message> receiveMessages() {
        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(5)
                .build();
        return client.receiveMessage(request).messages();
    }
}
