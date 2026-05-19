package com.cursopcv.aluguelservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Value("${services.pessoa-service.url}")
    private String pessoaServiceUrl;

    @Value("${services.carro-service.url}")
    private String carroServiceUrl;

    @Value("${services.notification-service.url}")
    private String notificationServiceUrl;

    @Bean("pessoaWebClient")
    public WebClient pessoaWebClient() {
        return WebClient.builder()
                .baseUrl(pessoaServiceUrl)
                .build();
    }



    @Bean("carroWebClient")
    public WebClient carroWebClient() {
        return WebClient.builder()
                .baseUrl(carroServiceUrl)
                .build();
    }

    @Bean("notificationWebClient")
    public WebClient notificationWebClient() {
        return WebClient.builder()
                .baseUrl(notificationServiceUrl)
                .build();
    }
}
