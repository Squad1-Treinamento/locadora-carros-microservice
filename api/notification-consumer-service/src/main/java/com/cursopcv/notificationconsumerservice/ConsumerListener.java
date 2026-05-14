package com.cursopcv.notificationconsumerservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component
public class ConsumerListener {


    private final ObjectMapper mapper;
    private final String queueUrl;
    private final SqsClient client;
    private final JavaMailSender mailSender;

    public ConsumerListener(String queueUrl, SqsClient client, JavaMailSender mailSender) {
        this.queueUrl = queueUrl;
        this.client = client;
        this.mailSender = mailSender;
        this.mapper = new ObjectMapper();
    }

    @Scheduled(fixedRate = 5000)
    public void pollMessages() {
        System.out.println("🔁 Verificando mensagens na fila...");

        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(5)
                .build();
        List<Message> messages = client.receiveMessage(request).messages();

        for (Message msg : messages) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo("user@email.com");
                message.setSubject("Texto enviado por SQS");
                message.setText(msg.body());
                mailSender.send(message);

                client.deleteMessage(r -> r
                        .queueUrl(queueUrl)
                        .receiptHandle(msg.receiptHandle()));

                System.out.println("✅ Email enviado para " + "user@email.com");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
