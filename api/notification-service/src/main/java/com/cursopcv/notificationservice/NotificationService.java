package com.cursopcv.notificationservice;

import com.cursopcv.notificationcontracts.dto.AluguelNotificationRequest;
import com.cursopcv.notificationcontracts.dto.CadastroNotificationRequest;
import com.cursopcv.notificationcontracts.dto.NotificationMessage;
import com.cursopcv.notificationcontracts.dto.ReservaNotificationRequest;
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

    public String notificarCadastro(CadastroNotificationRequest cadastrado) {
        sendMessage(new NotificationMessage(NotificationMessage.MessageType.CADASTRO, cadastrado));
        System.out.println("Notificando sobre cadastro: " + cadastrado);
        return "";
    }

    public String notificarReserva(ReservaNotificationRequest reserva) {
        sendMessage(new NotificationMessage(NotificationMessage.MessageType.RESERVA, reserva));
        System.out.println("Notificando reserva: " + reserva);
        return "";
    }

    public String notificarAluguel(AluguelNotificationRequest aluguel) {
        sendMessage(new NotificationMessage(NotificationMessage.MessageType.ALUGUEL, aluguel));
        System.out.println("Notificando aluguel: " + aluguel);
        return "";
    }

    public void sendMessage(NotificationMessage message) {
        String messageJson =  objectMapper.writeValueAsString(message);
        try {
            sqsClient.sendMessage(
                    SendMessageRequest.builder()
                            .queueUrl(queueUrl)
                            .messageBody(messageJson)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
