package com.cursopcv.pessoaservice.service;

import com.cursopcv.notificationcontracts.dto.CadastroNotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class NotificationClient {
    private static final Logger logger = LoggerFactory.getLogger(NotificationClient.class);

    private final WebClient webClient;

    public NotificationClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public void notificarCadastro(CadastroNotificationRequest cadastroRequest) {
        try {
            webClient.post()
                    .uri("/send/cadastro")
                    .bodyValue(cadastroRequest)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException e) {
            logger.warn("Falha ao notificar cadastro de motorista [{}]: HTTP {} - {}",
                    cadastroRequest.nome(), e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.warn("Falha ao notificar cadastro de motorista [{}]: {}",
                    cadastroRequest.nome(), e.getMessage());
        }
    }
}

