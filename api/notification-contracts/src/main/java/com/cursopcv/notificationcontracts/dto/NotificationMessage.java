package com.cursopcv.notificationcontracts.dto;

public record NotificationMessage(
        MessageType tipo,
        Object payload
) {
    public enum MessageType {
        PERSONALIZADA,
        CADASTRO,
        RESERVA,
        ALUGUEL
    }
}
