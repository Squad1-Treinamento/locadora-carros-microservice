package com.cursopcv.notificationconsumerservice;

import com.cursopcv.notificationconsumerservice.templates.MailTemplate;
import com.cursopcv.notificationcontracts.dto.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.model.Message;
import tools.jackson.databind.ObjectMapper;

@Component
public class MailSender {

    private final ObjectMapper mapper;
    private final JavaMailSender mailSender;

    public MailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
        this.mapper = new ObjectMapper();
    }

    public void send(Message message) {
        NotificationMessage notification = mapper.readValue(message.body(), NotificationMessage.class);

        SimpleMailMessage simpleMailMessage = createMailMessage(notification);

        try {
            mailSender.send(simpleMailMessage);
            System.out.println("✅ Email enviado:\n" + simpleMailMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private SimpleMailMessage createMailMessage(NotificationMessage notification) {
        MailTemplate mailTemplate = MailTemplate.builder(notification);

        if(mailTemplate == null) {
            throw new RuntimeException("Não foi possível criar a estrutura do Email para a notificação do tipo: " + notification.tipo());
        }

        SimpleMailMessage mail = new  SimpleMailMessage();
        mail.setTo(mailTemplate.getEmail());
        mail.setSubject(mailTemplate.getSubject());
        mail.setText(mailTemplate.getText());
        return mail;
    }
}
