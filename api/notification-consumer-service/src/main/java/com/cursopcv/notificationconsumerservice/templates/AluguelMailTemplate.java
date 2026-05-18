package com.cursopcv.notificationconsumerservice.templates;

import com.cursopcv.notificationcontracts.dto.AluguelNotificationRequest;
import tools.jackson.databind.ObjectMapper;

public class AluguelMailTemplate extends MailTemplate {

    private final String SUBJECT = "Aluguel confirmado, %s! Tudo pronto para sua locação";
    private final String TEXT = """
                Olá %s,
                
                Seu aluguel foi confirmado com sucesso!
                
                Detalhes do veículo:
                Veículo: %s %s
                Categoria: %s
                Placa: %s
                Cor: %s
                Valor da diária: R$ %s
                
                Acessórios incluídos:
                %s
                
                O pagamento foi processado e sua locação está garantida.
                
                Desejamos uma excelente experiência com o veículo.
                
                Em caso de dúvidas ou necessidade de suporte, estamos à disposição.
                
                Atenciosamente,
                Equipe da Locadora
                """;

    public AluguelMailTemplate(Object payload) {
        ObjectMapper mapper = new ObjectMapper();
        AluguelNotificationRequest aluguelMessage =
                mapper.convertValue(payload, AluguelNotificationRequest.class);
        this.email = aluguelMessage.pessoa().email();
        this.subject = String.format(SUBJECT, aluguelMessage.pessoa().nome());
        this.text = String.format(TEXT,
                aluguelMessage.pessoa().nome(),
                aluguelMessage.carro().modelo().fabricante().nome(),
                aluguelMessage.carro().modelo().descricao(),
                aluguelMessage.carro().modelo().categoria(),
                aluguelMessage.carro().placa(),
                aluguelMessage.carro().cor(),
                aluguelMessage.carro().valorDiaria().toPlainString(),
                aluguelMessage.carro().acessorios().stream()
                        .map(a -> "- " + a.descricao())
                        .reduce("", (a, b) -> a + "\n" + b)
        );
    }
}
