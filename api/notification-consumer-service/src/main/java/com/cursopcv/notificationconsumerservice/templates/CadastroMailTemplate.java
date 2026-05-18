package com.cursopcv.notificationconsumerservice.templates;

import com.cursopcv.notificationcontracts.dto.CadastroNotificationRequest;
import tools.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;

public class CadastroMailTemplate extends MailTemplate {

    private final String SUBJECT = "Cadastro concluído com sucesso, %s!";
    private final String TEXT = """
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
                """;

    public CadastroMailTemplate(Object payload) {
        ObjectMapper mapper = new ObjectMapper();
        CadastroNotificationRequest cadastroMessage =
                mapper.convertValue(payload, CadastroNotificationRequest.class);
        this.email = cadastroMessage.email();
        this.subject = String.format(SUBJECT, cadastroMessage.nome());
        this.text = String.format(TEXT,
                cadastroMessage.nome(),
                cadastroMessage.nome(),
                cadastroMessage.cpf(),
                new SimpleDateFormat("dd/MM/yyyy").format(cadastroMessage.dataNascimento()),
                cadastroMessage.matricula(),
                cadastroMessage.numeroCNH(),
                cadastroMessage.email()
        );
    }
}
