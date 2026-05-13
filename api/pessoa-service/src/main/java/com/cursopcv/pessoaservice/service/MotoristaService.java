package com.cursopcv.pessoaservice.service;

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
    private MotoristaRepository motoristaRepository;
    private PessoaRepository pessoaRepository;
    private PessoaMapper pessoaMapper;

    public MotoristaService(MotoristaRepository motoristaRepository, PessoaRepository pessoaRepository, PessoaMapper pessoaMapper) {
        this.motoristaRepository = motoristaRepository;
        this.pessoaRepository = pessoaRepository;
        this.pessoaMapper = pessoaMapper;
    }

    public PessoaResponse cadastrar(PessoaRequest motorista) {
        if(pessoaRepository.existsByCpf(motorista.cpf())){
            throw new EntityNotFoundException("Motorista já existente!");
        }

        if(motorista.numeroCNH() == null){
            throw new EntityNotFoundException("Motorista sem CNH cadastrada!");
        }

        Pessoa pessoaNova = pessoaMapper.toEntity(motorista);
        Motorista motoristaSalvo = motoristaRepository.save((Motorista) pessoaNova);

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
            if (pessoaRepository.existsByCpf(request.cpf())) {
                throw new IllegalArgumentException("O novo CPF informado já pertence a outro usuário.");
            }
            motoristaExistente.setCpf(request.cpf());
        }

        motoristaExistente.setNome(request.nome());
        motoristaExistente.setDataNascimento(request.dataNascimento());
        motoristaExistente.setSexo(request.sexo());

        Motorista motoristaSalvo = motoristaRepository.save(motoristaExistente);

        return pessoaMapper.toResponse(motoristaSalvo);
    }
}
