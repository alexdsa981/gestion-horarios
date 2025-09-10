package com.ipor.horariostua.core.bloquehorario.colaborador.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AgregarColaboradorDTO {
    //datos de api
    private Long empleado;
    private String nombreCompleto;
    private String documento;
    private String fechaNacimiento;
    private String tipoCmp;
    private String tipoColegio;
    private String cmp;
    private String idEspecialidad;
    private String especialidadNombre;
    private String estadoEmpleado;

    private Long idAgrupacion;
}
