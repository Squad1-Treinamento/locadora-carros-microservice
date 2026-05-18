package com.cursopcv.notificationconsumerservice.templates;

import com.cursopcv.notificationcontracts.dto.NotificationMessage;
import tools.jackson.databind.ObjectMapper;

public abstract class MailTemplate {

    String email;
    String subject;
    String text;

    public String getEmail() {
        return email;
    }

    public String getSubject() {
        return subject;
    }

    public String getText() {
        return text;
    }

    public static MailTemplate builder(NotificationMessage notificationMessage) {
        switch (notificationMessage.tipo()) {
            case PERSONALIZADA -> {
                return new PersonalizadaMailTemplate(notificationMessage.payload());
            }
            case CADASTRO -> {
                return new CadastroMailTemplate(notificationMessage.payload());
            }
            case RESERVA -> {
                return new ReservaMailTemplate(notificationMessage.payload());
            }
            case ALUGUEL -> {
                return new AluguelMailTemplate(notificationMessage.payload());
            }
            default -> System.out.println("⚠️ Tipo de notificação desconhecido: " + notificationMessage.tipo());
        }
        return null;
    }
}
