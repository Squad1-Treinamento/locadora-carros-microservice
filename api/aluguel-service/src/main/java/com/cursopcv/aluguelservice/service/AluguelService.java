package com.cursopcv.aluguelservice.service;

import com.cursopcv.aluguelservice.dto.*;
import com.cursopcv.aluguelservice.exception.*;
import com.cursopcv.aluguelservice.mapper.AluguelMapper;
import com.cursopcv.aluguelservice.model.Aluguel;
import com.cursopcv.aluguelservice.model.StatusAluguel;
import com.cursopcv.aluguelservice.repository.AluguelRepository;
import com.cursopcv.carroservice.dto.carro.CarroResponse;
import com.cursopcv.notificationcontracts.dto.AluguelNotificationRequest;
import com.cursopcv.notificationcontracts.dto.CustomNotificationRequest;
import com.cursopcv.notificationcontracts.dto.ReservaNotificationRequest;
import com.cursopcv.pessoaservice.dto.PessoaResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AluguelService {

    private final AluguelRepository aluguelRepository;
    private final AluguelMapper aluguelMapper;
    private final WebClient pessoaWebClient;
    private final WebClient carroWebClient;
    private final WebClient notificationWebClient;
    private final String termosAluguel;

    public AluguelService(
            AluguelRepository aluguelRepository,
            AluguelMapper aluguelMapper,
            WebClient.Builder webClientBuilder,
            @Value("${aluguel.termos}") String termosAluguel) {

        this.aluguelRepository = aluguelRepository;
        this.aluguelMapper = aluguelMapper;
        this.termosAluguel = termosAluguel;
        this.pessoaWebClient = webClientBuilder.baseUrl("http://localhost:8081").build();
        this.carroWebClient = webClientBuilder.baseUrl("http://localhost:8083").build();
        this.notificationWebClient = webClientBuilder.baseUrl("http://localhost:8095").build();
    }

    public List<AluguelResponse> listarTodos() {
        return aluguelRepository.findAll().stream()
                .map(aluguelMapper::toResponse)
                .toList();
    }

    public AluguelResponse buscarPorId(Integer id) {
        Aluguel aluguel = aluguelRepository.findById(id)
                .orElseThrow(() -> new AluguelNaoEncontradoException("Aluguel com ID " + id + " não encontrado."));
        return aluguelMapper.toResponse(aluguel);
    }

    public List<AluguelResponse> listarPorMotorista(Integer idMotorista) {
        pessoaWebClient.get()
                .uri("/motoristas/{id}", idMotorista)
                .retrieve()
                .bodyToMono(PessoaResponse.class)
                .block();

        return aluguelRepository.findByIdPessoa(idMotorista).stream()
                .map(aluguelMapper::toResponse)
                .toList();
    }

    public AluguelResponse solicitarAluguel(AluguelRequest request) {
        if (!request.dataDevolucao().after(request.dataEntrega())) {
            throw new DatasInvalidasException("A data de devolução deve ser posterior à data de entrega.");
        }

        PessoaResponse motorista = pessoaWebClient.get()
                .uri("/motoristas/{id}", request.idMotorista())
                .retrieve()
                .bodyToMono(PessoaResponse.class)
                .block();

        CarroResponse carro = carroWebClient.get()
                .uri("/carros/{id}", request.idCarro())
                .retrieve()
                .bodyToMono(CarroResponse.class)
                .block();

        if (!carro.disponivel()) {
            throw new CarroIndisponivelException("O veículo com ID " + request.idCarro() + " não está disponível.");
        }

        boolean conflito = aluguelRepository
                .existsByIdCarroAndStatusAndDataEntregaLessThanEqualAndDataDevolucaoGreaterThanEqual(
                        request.idCarro(),
                        StatusAluguel.CONFIRMADO,
                        request.dataDevolucao(),
                        request.dataEntrega());

        if (conflito) {
            throw new CarroIndisponivelException("O veículo já possui um aluguel confirmado para o período informado.");
        }

        long diffMs = request.dataDevolucao().getTime() - request.dataEntrega().getTime();
        int quantidadeDias = (int) Math.max(1, TimeUnit.MILLISECONDS.toDays(diffMs));

        BigDecimal valorTotal = carro.valorDiaria()
                .multiply(BigDecimal.valueOf(quantidadeDias));

        Aluguel aluguel = aluguelMapper.toEntity(request, carro.valorDiaria(), quantidadeDias, valorTotal);
        AluguelResponse response = aluguelMapper.toResponse(aluguelRepository.save(aluguel));

        try {
            notificarReserva(motorista, carro);
        } catch (Exception e) {
            log.warn("Falha ao enviar notificação de reserva: {}", e.getMessage());
        }

        return response;
    }

    public ResumoAluguelResponse obterResumo(Integer idAluguel) {
        Aluguel aluguel = aluguelRepository.findById(idAluguel)
                .orElseThrow(() -> new AluguelNaoEncontradoException("Aluguel com ID " + idAluguel + " não encontrado."));

        if (aluguel.getStatus() == StatusAluguel.CANCELADO) {
            throw new AluguelNaoEncontradoException("O aluguel ID " + idAluguel + " está cancelado.");
        }

        CarroResponse carro = carroWebClient.get()
                .uri("/carros/{id}", aluguel.getIdCarro())
                .retrieve()
                .bodyToMono(CarroResponse.class)
                .block();

        return new ResumoAluguelResponse(
                aluguel.getId(),
                aluguel.getIdPessoa(),
                aluguel.getIdCarro(),
                carro.modelo().descricao() + " " + carro.cor(),
                carro.placa(),
                carro.cor(),
                carro.imagemUrl(),
                carro.valorDiaria(),
                aluguel.getDataEntrega(),
                aluguel.getDataDevolucao(),
                aluguel.getQuantidadeDias(),
                aluguel.getValorDiaria(),
                aluguel.getValorDiaria().multiply(BigDecimal.valueOf(aluguel.getQuantidadeDias())),
                aluguelMapper.toApoliceResponse(aluguel.getApoliceSeguro()),
                aluguel.getValorTotal(),
                termosAluguel
        );
    }

    public CheckoutResponse efetivarAluguel(Integer idAluguel, PagamentoRequest pagamentoRequest) {
        Aluguel aluguel = aluguelRepository.findById(idAluguel)
                .orElseThrow(() -> new AluguelNaoEncontradoException("Aluguel com ID " + idAluguel + " não encontrado."));

        if (aluguel.getStatus() != StatusAluguel.PENDENTE) {
            throw new PagamentoFalhouException("Apenas aluguéis com status PENDENTE podem ser confirmados.");
        }

        String numeroTransacao = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        if (pagamentoRequest.tipoPagamento() == TipoPagamento.CARTAO_CREDITO) {
            Integer parcelas = pagamentoRequest.parcelas();
            if (parcelas == null || parcelas <= 0) {
                throw new PagamentoFalhouException("Número de parcelas é obrigatório para cartão de crédito.");
            }
        }

        aluguel.setStatus(StatusAluguel.CONFIRMADO);
        aluguelRepository.save(aluguel);

        carroWebClient.patch()
                .uri("/carros/{id}/disponibilidade", aluguel.getIdCarro())
                .bodyValue(new DisponibilidadeRequest(false))
                .retrieve()
                .toBodilessEntity()
                .block();

        PessoaResponse motorista = pessoaWebClient.get()
                .uri("/motoristas/{id}", aluguel.getIdPessoa())
                .retrieve()
                .bodyToMono(PessoaResponse.class)
                .block();

        CarroResponse carro = carroWebClient.get()
                .uri("/carros/{id}", aluguel.getIdCarro())
                .retrieve()
                .bodyToMono(CarroResponse.class)
                .block();

        try {
            notificarAluguel(motorista, carro);
        } catch (Exception e) {
            log.warn("Falha ao enviar notificação de aluguel: {}", e.getMessage());
        }

        try {
            String subject = String.format("Pagamento processado - Aluguel #%s", aluguel.getId());
            StringBuilder sb = new StringBuilder();
            sb.append("Olá ").append(motorista.nome()).append(",\n\n");
            sb.append("Seu pagamento foi processado com sucesso.\n\n");
            sb.append("Método de pagamento: ").append(pagamentoRequest.tipoPagamento()).append("\n");
            if (pagamentoRequest.tipoPagamento() == TipoPagamento.CARTAO_CREDITO) {
                sb.append("Parcelas: ").append(pagamentoRequest.parcelas()).append("\n");
            }
            sb.append("\nAtenciosamente,\nEquipe da Locadora");

            CustomNotificationRequest custom = new CustomNotificationRequest(
                    motorista.email(),
                    subject,
                    sb.toString()
            );

            notificationWebClient.post()
                    .uri("/send")
                    .bodyValue(custom)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            log.warn("Falha ao enviar notificação personalizada de pagamento: {}", e.getMessage());
        }

        return new CheckoutResponse(
                aluguel.getId(),
                numeroTransacao,
                aluguel.getStatus(),
                "APROVADO",
                "Aluguel confirmado com sucesso! Sua reserva está garantida.",
                aluguel.getIdPessoa(),
                aluguel.getIdCarro(),
                carro.modelo().descricao() + " " + carro.cor(),
                carro.placa(),
                aluguel.getDataEntrega(),
                aluguel.getDataDevolucao(),
                aluguel.getQuantidadeDias(),
                aluguel.getValorTotal(),
                aluguelMapper.toApoliceResponse(aluguel.getApoliceSeguro()),
                termosAluguel
        );
    }

    public AluguelResponse cancelarAluguel(Integer idAluguel) {
        Aluguel aluguel = aluguelRepository.findById(idAluguel)
                .orElseThrow(() -> new AluguelNaoEncontradoException("Aluguel com ID " + idAluguel + " não encontrado."));

        if (aluguel.getStatus() == StatusAluguel.FINALIZADO) {
            throw new AluguelNaoEncontradoException("Não é possível cancelar um aluguel já finalizado.");
        }

        if (aluguel.getStatus() == StatusAluguel.CANCELADO) {
            throw new AluguelNaoEncontradoException("Aluguel já está cancelado.");
        }

        boolean eraConfirmado = aluguel.getStatus() == StatusAluguel.CONFIRMADO;
        aluguel.setStatus(StatusAluguel.CANCELADO);
        aluguelRepository.save(aluguel);

        if (eraConfirmado) {
            carroWebClient.patch()
                    .uri("/carros/{id}/disponibilidade", aluguel.getIdCarro())
                    .bodyValue(new DisponibilidadeRequest(true))
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        }

        return aluguelMapper.toResponse(aluguel);
    }

    public AluguelResponse finalizarAluguel(Integer idAluguel) {
        Aluguel aluguel = aluguelRepository.findById(idAluguel)
                .orElseThrow(() -> new AluguelNaoEncontradoException("Aluguel com ID " + idAluguel + " não encontrado."));

        if (aluguel.getStatus() != StatusAluguel.CONFIRMADO) {
            throw new AluguelNaoEncontradoException("Apenas aluguéis CONFIRMADOS podem ser finalizados.");
        }

        aluguel.setStatus(StatusAluguel.FINALIZADO);
        aluguelRepository.save(aluguel);

        carroWebClient.patch()
                .uri("/carros/{id}/disponibilidade", aluguel.getIdCarro())
                .bodyValue(new DisponibilidadeRequest(true))
                .retrieve()
                .toBodilessEntity()
                .block();

        return aluguelMapper.toResponse(aluguel);
    }

    private void notificarReserva(PessoaResponse motorista, CarroResponse carro) {
        ReservaNotificationRequest.Pessoa pessoa = new ReservaNotificationRequest.Pessoa(
                motorista.nome(),
                motorista.cpf(),
                motorista.dataNascimento(),
                motorista.matricula(),
                motorista.numeroCNH(),
                motorista.email()
        );

        Set<ReservaNotificationRequest.Carro.Acessorio> acessorios = carro.acessorios().stream()
                .map(a -> new ReservaNotificationRequest.Carro.Acessorio(a.id(), a.descricao()))
                .collect(Collectors.toSet());

        ReservaNotificationRequest.Carro.ModeloCarro.FabricanteResponse fabricante =
                new ReservaNotificationRequest.Carro.ModeloCarro.FabricanteResponse(
                        carro.modelo().fabricante().id(),
                        carro.modelo().fabricante().nome()
                );

        ReservaNotificationRequest.Carro.ModeloCarro.Categoria categoria =
                ReservaNotificationRequest.Carro.ModeloCarro.Categoria.valueOf(
                        carro.modelo().categoria().name()
                );

        ReservaNotificationRequest.Carro.ModeloCarro modelo = new ReservaNotificationRequest.Carro.ModeloCarro(
                carro.modelo().id(),
                carro.modelo().descricao(),
                categoria,
                fabricante
        );

        ReservaNotificationRequest.Carro carroNotification = new ReservaNotificationRequest.Carro(
                carro.id(),
                carro.placa(),
                carro.chassi(),
                carro.cor(),
                carro.valorDiaria(),
                modelo,
                acessorios
        );

        ReservaNotificationRequest reservaNotification = new ReservaNotificationRequest(pessoa, carroNotification);

        notificationWebClient.post()
                .uri("/send/reserva")
                .bodyValue(reservaNotification)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    private void notificarAluguel(PessoaResponse motorista, CarroResponse carro) {
        AluguelNotificationRequest.Pessoa pessoa = new AluguelNotificationRequest.Pessoa(
                motorista.nome(),
                motorista.cpf(),
                motorista.dataNascimento(),
                motorista.matricula(),
                motorista.numeroCNH(),
                motorista.email()
        );

        Set<AluguelNotificationRequest.Carro.Acessorio> acessorios = carro.acessorios().stream()
                .map(a -> new AluguelNotificationRequest.Carro.Acessorio(a.id(), a.descricao()))
                .collect(Collectors.toSet());

        AluguelNotificationRequest.Carro.ModeloCarro.FabricanteResponse fabricante =
                new AluguelNotificationRequest.Carro.ModeloCarro.FabricanteResponse(
                        carro.modelo().fabricante().id(),
                        carro.modelo().fabricante().nome()
                );

        AluguelNotificationRequest.Carro.ModeloCarro.Categoria categoria =
                AluguelNotificationRequest.Carro.ModeloCarro.Categoria.valueOf(
                        carro.modelo().categoria().name()
                );

        AluguelNotificationRequest.Carro.ModeloCarro modelo = new AluguelNotificationRequest.Carro.ModeloCarro(
                carro.modelo().id(),
                carro.modelo().descricao(),
                categoria,
                fabricante
        );

        AluguelNotificationRequest.Carro carroNotification = new AluguelNotificationRequest.Carro(
                carro.id(),
                carro.placa(),
                carro.chassi(),
                carro.cor(),
                carro.valorDiaria(),
                modelo,
                acessorios
        );

        AluguelNotificationRequest aluguelNotification = new AluguelNotificationRequest(pessoa, carroNotification);

        notificationWebClient.post()
                .uri("/send/aluguel")
                .bodyValue(aluguelNotification)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    private record DisponibilidadeRequest(boolean disponivel) {}
}