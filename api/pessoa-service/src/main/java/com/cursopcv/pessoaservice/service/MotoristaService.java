package com.cursopcv.pessoaservice.service;

import com.cursopcv.pessoaservice.dto.PessoaRequest;
import com.cursopcv.pessoaservice.dto.PessoaResponse;
import com.cursopcv.pessoaservice.mapper.PessoaMapper;
import com.cursopcv.pessoaservice.model.Motorista;
import com.cursopcv.pessoaservice.model.Pessoa;
import com.cursopcv.pessoaservice.repository.MotoristaRepository;
import com.cursopcv.pessoaservice.repository.PessoaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class MotoristaService {
    @Autowired
    private MotoristaRepository motoristaRepository;
    private PessoaRepository pessoaRepository;
    private PessoaMapper pessoaMapper;

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

}
