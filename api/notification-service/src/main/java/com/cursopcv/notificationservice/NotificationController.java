package com.cursopcv.notificationservice;

import com.cursopcv.notificationcontracts.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/send")
public class NotificationController implements NotificationControllerDoc {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    @PostMapping("/cadastro")
    public ResponseEntity<String> notificarCadastro(@RequestBody @Valid CadastroNotificationRequest cadastrado) {
        return ResponseEntity.ok(notificationService.notificarCadastro(cadastrado));
    }

    @Override
    @PostMapping("/reserva")
    public ResponseEntity<String> notificarReserva(@RequestBody @Valid ReservaNotificationRequest reserva) {
        return ResponseEntity.ok(notificationService.notificarReserva(reserva));
    }

    @Override
    @PostMapping("/aluguel")
    public ResponseEntity<String> notificarAluguel(@RequestBody @Valid AluguelNotificationRequest aluguel) {
        return ResponseEntity.ok(notificationService.notificarAluguel(aluguel));
    }

    @Override
    @PostMapping
    public ResponseEntity<String> notificar(@RequestBody @Valid CustomNotificationRequest message) {
        notificationService.sendMessage(new NotificationMessage(NotificationMessage.MessageType.PERSONALIZADA ,message));
        return ResponseEntity.ok("");
    }
}
