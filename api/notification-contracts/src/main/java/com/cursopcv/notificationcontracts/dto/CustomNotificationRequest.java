package com.cursopcv.notificationcontracts.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CustomNotificationRequest(
        @Email
        String email,

        @NotBlank
        String subject,

        @NotBlank
        String text
) {
}
