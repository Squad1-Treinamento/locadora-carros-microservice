package com.cursopcv.pessoaservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.net.URI;

@Configuration
public class SqsConfig {

    @Value("${sqs.endpoint}")
    private String sqsEndpoint;

    @Value("${sqs.queue-name}")
    private String queueName;

    @Bean
    public SqsClient sqsClient() {
        try {
            SqsClient sqsClient = SqsClient.builder()
                    .endpointOverride(URI.create(sqsEndpoint))
                    .region(Region.US_EAST_1)
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create("test", "test")))
                    .build();
            return sqsClient;
        } catch (Exception e) {
            System.out.println("Erro ao criar SQS Client: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Bean
    public String queueUrl(SqsClient sqsClient) {
        return sqsClient.getQueueUrl(r -> r.queueName(queueName)).queueUrl();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
