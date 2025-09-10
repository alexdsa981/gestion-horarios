package com.ipor.horariostua.core.bloquehorario.sede;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SedeRepository extends JpaRepository<Sede, Long> {
    List<Sede> findByIsActiveTrue();
}
