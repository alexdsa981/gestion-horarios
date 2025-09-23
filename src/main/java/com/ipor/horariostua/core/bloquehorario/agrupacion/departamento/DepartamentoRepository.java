package com.ipor.horariostua.core.bloquehorario.agrupacion.departamento;

import com.ipor.horariostua.core.bloquehorario.agrupacion.Agrupacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {
    List<Departamento> findByIsActiveTrue();
}
