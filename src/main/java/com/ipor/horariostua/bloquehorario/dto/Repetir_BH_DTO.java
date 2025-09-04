package com.ipor.horariostua.bloquehorario.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Repetir_BH_DTO {

    private Long id;
    private List<LocalDate> fechas;


}
