package com.cursopcv.pessoaservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class PessoaServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PessoaServiceApplication.class, args);
    }

    @Bean
    public WebClient webClient(@Value("${notification.service.url}") String notificationServiceUrl) {
        return WebClient.builder()
                .baseUrl(notificationServiceUrl)
                .build();
    }
}
