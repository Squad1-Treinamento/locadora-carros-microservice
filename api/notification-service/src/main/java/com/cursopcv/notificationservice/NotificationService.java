package com.cursopcv.notificationservice;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import tools.jackson.databind.ObjectMapper;

@Service
public class NotificationService {

    private final ObjectMapper objectMapper;
    private final String queueUrl;
    private final SqsClient sqsClient;

    public NotificationService(ObjectMapper objectMapper, String queueUrl, SqsClient sqsClient) {
        this.objectMapper = objectMapper;
        this.queueUrl = queueUrl;
        this.sqsClient = sqsClient;
    }

    public String notificarCadastro(String message) {
        sendMessage(message);
        System.out.println("Notificando sobre cadastro: " + message);
        return "";
    }

    public String notificarReserva(String message) {
        sendMessage(message);
        System.out.println("Notificando reserva: " + message);
        return "";
    }

    public String notificarAluguel(String message) {
        sendMessage(message);
        System.out.println("Notificando aluguel: " + message);
        return "";
    }

    public void sendMessage(String message) {
        try {
            sqsClient.sendMessage(
                    SendMessageRequest.builder()
                            .queueUrl(queueUrl)
                            .messageBody(message)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
