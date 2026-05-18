package com.cursopcv.notificationconsumerservice;

import com.cursopcv.notificationcontracts.dto.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import tools.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;
import java.util.List;

@Component
public class ConsumerListener {


    private final ObjectMapper mapper;
    private final String queueUrl;
    private final SqsClient client;
    private final JavaMailSender mailSender;

    public ConsumerListener(String queueUrl, SqsClient client, JavaMailSender mailSender) {
        this.queueUrl = queueUrl;
        this.client = client;
        this.mailSender = mailSender;
        this.mapper = new ObjectMapper();
    }

    @Scheduled(fixedRate = 5000)
    public void pollMessages() {
        System.out.println("🔁 Verificando mensagens na fila...");

        List<Message> messages = receiveMessages();

        for (Message message : messages) {
            NotificationMessage notification =  mapper.readValue(message.body(), NotificationMessage.class);
            
            SimpleMailMessage simpleMailMessage = createMailMessage(notification);
            
            try {
                mailSender.send(simpleMailMessage);

                client.deleteMessage(r -> r
                        .queueUrl(queueUrl)
                        .receiptHandle(message.receiptHandle()));

                System.out.println("✅ Email enviado para " + "user@email.com");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private SimpleMailMessage createMailMessage(NotificationMessage notification) {
        SimpleMailMessage mailMessage = null;
        switch (notification.tipo()) {
            case PERSONALIZADA -> mailMessage = handlePersonalizadaMail(notification);
            case CADASTRO -> mailMessage = handleCadastroMail(notification);
            case RESERVA -> mailMessage = handleReservaMail(notification);
            case ALUGUEL -> mailMessage = handleAluguelMail(notification);
            default -> System.out.println("⚠️ Tipo de notificação desconhecido: " + notification.tipo());
        }
        return mailMessage;
    }


    private SimpleMailMessage handlePersonalizadaMail(NotificationMessage notification) {
        SimpleMailMessage mail = new SimpleMailMessage();
        CustomNotificationRequest customMessage = mapper.convertValue(notification.payload(), CustomNotificationRequest.class);
        mail.setTo(customMessage.email());
        mail.setSubject(customMessage.subject());
        mail.setText(customMessage.text());
        return mail;
    }

    private SimpleMailMessage handleAluguelMail(NotificationMessage notification) {
        SimpleMailMessage mail = new SimpleMailMessage();
        AluguelNotificationRequest aluguelMessage = mapper.convertValue(notification.payload(), AluguelNotificationRequest.class);
        mail.setTo(aluguelMessage.pessoa().email());
        mail.setSubject(String.format("Aluguel confirmado, %s! Tudo pronto para sua locação", aluguelMessage.pessoa().nome()));
        mail.setText(String.format("""
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
                """,
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
                ));
        return mail;
    }

    private SimpleMailMessage handleReservaMail(NotificationMessage notification) {
        SimpleMailMessage mail = new SimpleMailMessage();
        ReservaNotificationRequest reservaMessage = mapper.convertValue(notification.payload(), ReservaNotificationRequest.class);
        mail.setTo(reservaMessage.pessoa().email());
        mail.setSubject(String.format("Reserva realizada com sucesso, %s!", reservaMessage.pessoa().nome()));
        mail.setText(String.format("""
            
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
            """,
                reservaMessage.pessoa().nome(),
                reservaMessage.carro().modelo().fabricante().nome(),
                reservaMessage.carro().modelo().descricao(),
                reservaMessage.carro().modelo().categoria(),
                reservaMessage.carro().placa(),
                reservaMessage.carro().cor(),
                reservaMessage.carro().valorDiaria().toPlainString(),
                reservaMessage.carro().acessorios().stream()
                        .map(a -> "- " + a.descricao())
                        .reduce("", (a, b) -> a + "\n" + b))
        );
        return mail;
    }

    private SimpleMailMessage handleCadastroMail(NotificationMessage notification) {
        SimpleMailMessage mail = new SimpleMailMessage();
        CadastroNotificationRequest cadastroMessage = mapper.convertValue(notification.payload(), CadastroNotificationRequest.class);
        mail.setTo(cadastroMessage.email());
        mail.setSubject(String.format("Cadastro concluído com sucesso, %s!", cadastroMessage.nome()));
        mail.setText(String.format("""
                Olá %s,
                
                Seu cadastro na nossa locadora foi realizado com sucesso!
                
                Confira abaixo os dados cadastrados:
                Nome: %s
                CPF: %s
                Data de Nascimento: %s
                Matrícula: %s
                Número da CNH: %s
                E-mail: %s
                
                Agora você já pode acessar nossa plataforma e utilizar nossos serviços.
                Explore nossa lista de veículos, realize reservas e efetue locações com facilidade.
                
                Ficamos à disposição.
                
                Atenciosamente,
                Equipe da Locadora
                """,
                cadastroMessage.nome(),
                cadastroMessage.nome(),
                cadastroMessage.cpf(),
                new SimpleDateFormat("dd/MM/yyyy").format(cadastroMessage.dataNascimento()),
                cadastroMessage.matricula(),
                cadastroMessage.numeroCNH(),
                cadastroMessage.email()
        ));
        return mail;
    }

    private List<Message> receiveMessages() {
        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(5)
                .build();
        return client.receiveMessage(request).messages();
    }
}
