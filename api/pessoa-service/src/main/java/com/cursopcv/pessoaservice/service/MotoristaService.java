package com.cursopcv.pessoaservice.service;

import com.cursopcv.pessoaservice.model.Motorista;
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

    public Motorista cadastrar(@RequestBody Motorista motorista) {
        if(pessoaRepository.existsByCpf(motorista.getCpf()) == null){
            throw new EntityNotFoundException("Motorista não existente");
        }

        if(motorista.getNumeroCNH() == null){
            throw new EntityNotFoundException("Motorista sem CNH cadastrada!");
        }

        Motorista motoristaSalvar = new Motorista();

        motoristaSalvar.setCpf(motorista.getCpf());
        motoristaSalvar.setNumeroCNH(motorista.getNumeroCNH());
        motoristaSalvar.setNome(motorista.getNome());

        return motoristaRepository.save(motoristaSalvar);
    }

}
