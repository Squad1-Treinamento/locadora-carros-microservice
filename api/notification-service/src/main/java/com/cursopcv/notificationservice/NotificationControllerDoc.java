package com.cursopcv.notificationservice;

import com.cursopcv.notificationcontracts.dto.AluguelNotificationRequest;
import com.cursopcv.notificationcontracts.dto.CadastroNotificationRequest;
import com.cursopcv.notificationcontracts.dto.CustomNotificationRequest;
import com.cursopcv.notificationcontracts.dto.ReservaNotificationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

@Tag(
        name = "Notificacoes",
        description = "Endpoints para enviar notificacoes de cadastro, reserva, aluguel e mensagens personalizadas"
)
public interface NotificationControllerDoc {

    @Operation(
            summary = "Notificar cadastro",
            description = "Enfileira uma notificacao de cadastro de cliente para processamento assincrono."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mensagem enfileirada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Payload invalido"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    ResponseEntity<String> notificarCadastro(
            @RequestBody(
                    required = true,
                    description = "Dados do cadastro do cliente",
                    content = @Content(
                            schema = @Schema(implementation = CadastroNotificationRequest.class),
                            examples = @ExampleObject(
                                    name = "Cadastro",
                                    value = "{\"nome\":\"Maria Souza\",\"cpf\":\"12345678909\",\"dataNascimento\":\"1990-05-10\",\"matricula\":\"MAT-2024-001\",\"numeroCNH\":\"12345678901\",\"email\":\"maria.souza@email.com\"}"
                            )
                    )
            )
            @Valid CadastroNotificationRequest cadastrado
    );

    @Operation(
            summary = "Notificar reserva",
            description = "Enfileira uma notificacao de reserva com dados de pessoa e carro."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mensagem enfileirada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Payload invalido"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    ResponseEntity<String> notificarReserva(
            @RequestBody(
                    required = true,
                    description = "Dados da reserva",
                    content = @Content(
                            schema = @Schema(implementation = ReservaNotificationRequest.class),
                            examples = @ExampleObject(
                                    name = "Reserva",
                                    value = "{\"pessoa\":{\"nome\":\"Joao Lima\",\"cpf\":\"98765432100\",\"dataNascimento\":\"1988-01-20\",\"matricula\":\"MAT-2024-055\",\"numeroCNH\":\"99887766554\",\"email\":\"joao.lima@email.com\"},\"carro\":{\"id\":10,\"placa\":\"ABC1D23\",\"chassi\":\"9BWZZZ377VT004251\",\"cor\":\"Preto\",\"valorDiaria\":120.50,\"modelo\":{\"id\":2,\"descricao\":\"Sedan Executivo\",\"categoria\":\"SEDAN_MEDIO\",\"fabricante\":{\"id\":1,\"nome\":\"Volkswagen\"}},\"acessorios\":[{\"id\":3,\"descricao\":\"GPS\"}]}}"
                            )
                    )
            )
            @Valid ReservaNotificationRequest reserva
    );

    @Operation(
            summary = "Notificar aluguel",
            description = "Enfileira uma notificacao de aluguel com dados de pessoa e carro."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mensagem enfileirada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Payload invalido"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    ResponseEntity<String> notificarAluguel(
            @RequestBody(
                    required = true,
                    description = "Dados do aluguel",
                    content = @Content(
                            schema = @Schema(implementation = AluguelNotificationRequest.class),
                            examples = @ExampleObject(
                                    name = "Aluguel",
                                    value = "{\"pessoa\":{\"nome\":\"Carla Nunes\",\"cpf\":\"32165498700\",\"dataNascimento\":\"1992-11-03\",\"matricula\":\"MAT-2024-077\",\"numeroCNH\":\"11223344556\",\"email\":\"carla.nunes@email.com\"},\"carro\":{\"id\":15,\"placa\":\"XYZ9K88\",\"chassi\":\"9BWZZZ377VT004252\",\"cor\":\"Branco\",\"valorDiaria\":150.00,\"modelo\":{\"id\":5,\"descricao\":\"SUV Premium\",\"categoria\":\"UTILITARIO_COMERCIAL\",\"fabricante\":{\"id\":2,\"nome\":\"Toyota\"}},\"acessorios\":[{\"id\":7,\"descricao\":\"Cadeirinha\"}]}}"
                            )
                    )
            )
            @Valid AluguelNotificationRequest aluguel
    );

    @Operation(
            summary = "Notificar mensagem personalizada",
            description = "Enfileira uma notificacao personalizada com assunto e texto livres."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mensagem enfileirada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Payload invalido"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    ResponseEntity<String> notificar(
            @RequestBody(
                    required = true,
                    description = "Conteudo da notificacao personalizada",
                    content = @Content(
                            schema = @Schema(implementation = CustomNotificationRequest.class),
                            examples = @ExampleObject(
                                    name = "Personalizada",
                                    value = "{\"email\":\"cliente@email.com\",\"subject\":\"Bem-vindo\",\"text\":\"Seu cadastro foi aprovado.\"}"
                            )
                    )
            )
            @Valid CustomNotificationRequest message
    );
}

