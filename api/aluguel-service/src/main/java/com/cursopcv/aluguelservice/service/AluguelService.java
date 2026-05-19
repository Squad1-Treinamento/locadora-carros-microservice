package com.cursopcv.aluguelservice.service;

import com.cursopcv.aluguelservice.dto.*;
import com.cursopcv.aluguelservice.exception.*;
import com.cursopcv.aluguelservice.mapper.AluguelMapper;
import com.cursopcv.aluguelservice.model.Aluguel;
import com.cursopcv.aluguelservice.model.StatusAluguel;
import com.cursopcv.aluguelservice.repository.AluguelRepository;
import com.cursopcv.carroservice.dto.carro.CarroResponse;
import com.cursopcv.pessoaservice.dto.PessoaResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
        this.notificationWebClient = webClientBuilder.baseUrl("http://localhost:8085").build();
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

        pessoaWebClient.get()
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
                .multiply(BigDecimal.valueOf(quantidadeDias))
                .add(request.apoliceSeguro().custoApolice());

        Aluguel aluguel = aluguelMapper.toEntity(request, carro.valorDiaria(), quantidadeDias, valorTotal);
        return aluguelMapper.toResponse(aluguelRepository.save(aluguel));
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

        BigDecimal custoApolice = aluguel.getApoliceSeguro() != null
                ? aluguel.getApoliceSeguro().getCustoApolice()
                : BigDecimal.ZERO;

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
                custoApolice,
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

        if (pagamentoRequest.valor().compareTo(aluguel.getValorTotal()) != 0) {
            throw new PagamentoFalhouException("Valor do pagamento não corresponde ao total do aluguel.");
        }

        String numeroTransacao = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        if (pagamentoRequest.numeroCartao().endsWith("0000")) {
            throw new PagamentoFalhouException("Cartão recusado pela operadora.");
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
            notificationWebClient.post()
                    .uri("/send/aluguel")
                    .bodyValue(new NotificacaoRequest(motorista, carro))
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            log.warn("Falha ao enviar notificação: {}", e.getMessage());
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

    private record DisponibilidadeRequest(boolean disponivel) {}

    private record NotificacaoRequest(PessoaResponse pessoa, CarroResponse carro) {}
}