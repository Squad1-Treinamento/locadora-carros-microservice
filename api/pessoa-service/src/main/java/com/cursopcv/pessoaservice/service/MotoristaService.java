package com.cursopcv.pessoaservice.service;

import com.cursopcv.notificationcontracts.dto.CadastroNotificationRequest;
import com.cursopcv.pessoaservice.dto.PessoaRequest;
import com.cursopcv.pessoaservice.dto.PessoaResponse;
import com.cursopcv.pessoaservice.mapper.PessoaMapper;
import com.cursopcv.pessoaservice.model.Motorista;
import com.cursopcv.pessoaservice.model.Pessoa;
import com.cursopcv.pessoaservice.repository.MotoristaRepository;
import com.cursopcv.pessoaservice.repository.PessoaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MotoristaService {
    private final MotoristaRepository motoristaRepository;
    private final PessoaRepository pessoaRepository;
    private final PessoaMapper pessoaMapper;
    private final NotificationClient notificationClient;
    private final PessoaService pessoaService;

    public MotoristaService(MotoristaRepository motoristaRepository, PessoaRepository pessoaRepository, PessoaMapper pessoaMapper, NotificationClient notificationClient, PessoaService pessoaService) {
        this.motoristaRepository = motoristaRepository;
        this.pessoaRepository = pessoaRepository;
        this.pessoaMapper = pessoaMapper;
        this.notificationClient = notificationClient;
        this.pessoaService = pessoaService;
    }

    public PessoaResponse cadastrar(PessoaRequest motorista) {
        if(pessoaRepository.existsByCpf(motorista.cpf())){
            throw new EntityNotFoundException("Motorista já existente!");
        }

        if(motorista.numeroCNH() == null){
            throw new EntityNotFoundException("Motorista sem CNH cadastrada!");
        }

        if(motorista.email() == null){
            throw new EntityNotFoundException("Motorista sem email cadastrado!");
        }

        Pessoa pessoaNova = pessoaMapper.toEntity(motorista);
        Motorista motoristaSalvo = motoristaRepository.save((Motorista) pessoaNova);

        CadastroNotificationRequest cadastroRequest = PessoaMapper.toCadastroNotificationRequest(motoristaSalvo);
        notificationClient.notificarCadastro(cadastroRequest);

        return pessoaMapper.toResponse(motoristaSalvo);
    }

    public PessoaResponse buscarPorId(Integer id) {

        Pessoa pessoa = pessoaRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        return pessoaMapper.toResponse(pessoa);
    }

    public PessoaResponse atualizarPorId(Integer id, PessoaRequest request) {
        Motorista motoristaExistente = motoristaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Motorista não existe!"));

        if (!motoristaExistente.getCpf().equals(request.cpf())) {
            if (pessoaService.existePorCpf(request.cpf())) {
                throw new IllegalArgumentException("O novo CPF informado já pertence a outro usuário.");
            }
            motoristaExistente.setCpf(request.cpf());
        }

        if(!motoristaExistente.getEmail().equals(request.email())){
            if(pessoaService.existePorEmail(request.email())){
                throw new IllegalArgumentException("O novo email informado já pertence a outro usuário.");
            }
            motoristaExistente.setEmail(request.email());
        }

        motoristaExistente.setNome(request.nome());
        motoristaExistente.setDataNascimento(request.dataNascimento());
        motoristaExistente.setSexo(request.sexo());

        Motorista motoristaSalvo = motoristaRepository.save(motoristaExistente);

        return pessoaMapper.toResponse(motoristaSalvo);
    }
}

