package com.cursopcv.notificationconsumerservice.templates;

import com.cursopcv.notificationcontracts.dto.ReservaNotificationRequest;
import tools.jackson.databind.ObjectMapper;

public class ReservaMailTemplate extends MailTemplate {

    private final String SUBJECT = "Reserva realizada com sucesso, %s!";
    private final String TEXT = """
            
            Olá %s,
            
            Sua reserva foi realizada com sucesso!
            
            Detalhes da reserva:
            Veículo: %s %s
            Categoria: %s
            Placa: %s
            Cor: %s
            Valor da diária: R$ %s
            
            Acessórios incluídos:
            %s
            
            Para garantir sua locação, pedimos que prossiga com a confirmação e pagamento.
            
            Caso tenha dúvidas, nossa equipe está à disposição.
            
            Atenciosamente,
            Equipe da Locadora
            """;

    public ReservaMailTemplate(Object payload) {
        ObjectMapper mapper = new ObjectMapper();
        ReservaNotificationRequest reservaMessage =
                mapper.convertValue(payload, ReservaNotificationRequest.class);
        this.email = reservaMessage.pessoa().email();
        this.subject = String.format(SUBJECT, reservaMessage.pessoa().nome());
        this.text = String.format(TEXT,
                reservaMessage.pessoa().nome(),
                reservaMessage.carro().modelo().fabricante().nome(),
                reservaMessage.carro().modelo().descricao(),
                reservaMessage.carro().modelo().categoria(),
                reservaMessage.carro().placa(),
                reservaMessage.carro().cor(),
                reservaMessage.carro().valorDiaria().toPlainString(),
                reservaMessage.carro().acessorios().stream()
                        .map(a -> "- " + a.descricao())
                        .reduce("", (a, b) -> a + "\n" + b));
    }
}
