package com.cursopcv.notificationservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/send")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<String> notificarCadastro(@RequestBody String message) {
        return ResponseEntity.ok(notificationService.notificarCadastro(message));
    }

    @PostMapping("/reserva")
    public ResponseEntity<String> notificarReserva(@RequestBody String message) {
        return ResponseEntity.ok(notificationService.notificarReserva(message));
    }

    @PostMapping("/aluguel")
    public ResponseEntity<String> notificarAluguel(@RequestBody String message) {
        return ResponseEntity.ok(notificationService.notificarAluguel(message));
    }

    @GetMapping
    public String notificar(@RequestParam String message) {
        try {
            notificationService.sendMessage(message);
        } catch (Exception e) {
            return e.getMessage();
        }
        return "Notificações enviadas para fila!";
    }
}
