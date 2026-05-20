package com.cursopcv.notificationconsumerservice.templates;

import com.cursopcv.notificationcontracts.dto.CustomNotificationRequest;
import tools.jackson.databind.ObjectMapper;

public class PersonalizadaMailTemplate extends MailTemplate {

    public PersonalizadaMailTemplate(Object payload) {
        ObjectMapper mapper = new ObjectMapper();
        CustomNotificationRequest customMessage =
                mapper.convertValue(payload, CustomNotificationRequest.class);
        this.email = customMessage.email();
        this.subject = customMessage.subject();
        this.text = customMessage.text();
    }
}
