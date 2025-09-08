package com.ipor.horariostua.core.bloquehorario.colaborador;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Colaborador {
    @Id
    private Long id;

    private Boolean isActive;
    private String nombreCompleto;
    private String numeroDocumento;
    private String colegioProfesional;
    private String numeroColegiatura;
    private String especialidad;
    private String servicio;


}
