package com.ipor.horariostua.sede;

import com.ipor.horariostua.colaborador.Colaborador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SedeRepository extends JpaRepository<Sede, Long> {
    List<Sede> findByIsActiveTrue();

}
