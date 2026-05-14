package com.cursopcv.notificationconsumerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class NotificationConsumerServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificationConsumerServiceApplication.class, args);
    }
}
