package com.cursopcv.pessoaservice.service;

import com.cursopcv.notificationcontracts.dto.CadastroNotificationRequest;
import com.cursopcv.notificationcontracts.dto.NotificationMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
@Service
public class NotificationClient {
    private static final Logger logger = LoggerFactory.getLogger(NotificationClient.class);

    private final SqsClient sqsClient;
    private final String queueUrl;
    private final ObjectMapper objectMapper;

    public NotificationClient(SqsClient sqsClient, String queueUrl, ObjectMapper objectMapper) {
        this.sqsClient = sqsClient;
        this.queueUrl = queueUrl;
        this.objectMapper = objectMapper;
    }

    public void notificarCadastro(CadastroNotificationRequest cadastroRequest) {
        try {
            NotificationMessage message = new NotificationMessage(
                    NotificationMessage.MessageType.CADASTRO,
                    cadastroRequest
            );
            String messageJson = objectMapper.writeValueAsString(message);

            sqsClient.sendMessage(
                    SendMessageRequest.builder()
                            .queueUrl(queueUrl)
                            .messageBody(messageJson)
                            .build());

            logger.info("Notificação de cadastro enviada para fila SQS: {}", cadastroRequest.nome());
        } catch (Exception e) {
            logger.warn("Falha ao notificar cadastro de motorista [{}]: {}",
                    cadastroRequest.nome(), e.getMessage());
        }
    }
}