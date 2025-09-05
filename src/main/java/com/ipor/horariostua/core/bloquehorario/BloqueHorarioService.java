package com.ipor.horariostua.core.bloquehorario;

import com.ipor.horariostua.core.bloquehorario.agrupacion.AgrupacionService;
import com.ipor.horariostua.core.bloquehorario.colaborador.ColaboradorService;
import com.ipor.horariostua.core.bloquehorario.dto.Recibido_BH_DTO;
import com.ipor.horariostua.core.bloquehorario.dto.Repetir_BH_DTO;
import com.ipor.horariostua.core.bloquehorario.horariolaboral.HorarioLaboralService;
import com.ipor.horariostua.core.bloquehorario.sede.SedeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BloqueHorarioService {
    @Autowired
    BloqueHorarioRepository bloqueHorarioRepository;
    @Autowired
    ColaboradorService colaboradorService;
    @Autowired
    AgrupacionService agrupacionService;
    @Autowired
    HorarioLaboralService horarioLaboralService;
    @Autowired
    SedeService sedeService;


    public BloqueHorario agregar(Recibido_BH_DTO dto){
        BloqueHorario bloqueHorario = new BloqueHorario();
        bloqueHorario.setHorarioLaboral(horarioLaboralService.getUltimoHorarioLaboral());
        bloqueHorario.setColaborador(colaboradorService.getColaboradorPorId(dto.getIdColaborador()));
        bloqueHorario.setFecha(dto.getFecha());
        bloqueHorario.setHoraInicio(dto.getHoraInicio());
        bloqueHorario.setHoraFin(dto.getHoraFin());

        bloqueHorario.setAgrupacion(agrupacionService.getAgrupacionPorId(1L)); // O usa dto.getIdAgrupacion() si corresponde

        bloqueHorario.setSede(sedeService.getSedePorId(dto.getIdSede()));
        bloqueHorario.setGrupoAnidado(bloqueHorarioRepository.findMaxGrupoAnidado() + 1);

        bloqueHorarioRepository.save(bloqueHorario);
        return bloqueHorario;
    }

    public void eliminar(Long id){
        bloqueHorarioRepository.deleteById(id);
    }

    public BloqueHorario editar(Recibido_BH_DTO dto, Long id){
        BloqueHorario bloque = bloqueHorarioRepository.findById(id).get();
        bloque.setColaborador(colaboradorService.getColaboradorPorId(dto.getIdColaborador()));
        bloque.setSede(sedeService.getSedePorId(dto.getIdSede()));
        bloque.setHoraInicio(dto.getHoraInicio());
        bloque.setHoraFin(dto.getHoraFin());
        bloque.setGrupoAnidado(bloqueHorarioRepository.findMaxGrupoAnidado() + 1);
        bloqueHorarioRepository.save(bloque);
        return bloque;
    }

    public List<BloqueHorario> repetir(Repetir_BH_DTO dto) {
        BloqueHorario bloqueRepetir = bloqueHorarioRepository.findById(dto.getId()).get();
        List<BloqueHorario> listaRepeticionActual = bloqueHorarioRepository.findByGrupoAnidado(bloqueRepetir.getGrupoAnidado());
        Integer grupoAnidado = bloqueRepetir.getGrupoAnidado();

        // Obtén fechas como Sets para fácil comparación
        Set<LocalDate> fechasRecibidas = new HashSet<>(dto.getFechas());
        Set<LocalDate> fechasActuales = listaRepeticionActual.stream()
                .map(BloqueHorario::getFecha)
                .collect(Collectors.toSet());

        List<BloqueHorario> bloquesResultantes = new ArrayList<>();

        // AGREGAR: fechas recibidas que no están en las actuales
        for (LocalDate fecha : fechasRecibidas) {
            if (!fechasActuales.contains(fecha)) {
                BloqueHorario repetido = new BloqueHorario();
                repetido.setSede(bloqueRepetir.getSede());
                repetido.setColaborador(bloqueRepetir.getColaborador());
                repetido.setHoraFin(bloqueRepetir.getHoraFin());
                repetido.setHoraInicio(bloqueRepetir.getHoraInicio());
                repetido.setFecha(fecha);
                repetido.setAgrupacion(bloqueRepetir.getAgrupacion());
                repetido.setHorarioLaboral(bloqueRepetir.getHorarioLaboral());
                repetido.setGrupoAnidado(grupoAnidado);
                bloquesResultantes.add(bloqueHorarioRepository.save(repetido));
            }
        }

        // ELIMINAR: fechas actuales que no están en las recibidas
        for (BloqueHorario bloque : listaRepeticionActual) {
            if (!fechasRecibidas.contains(bloque.getFecha())) {
                bloqueHorarioRepository.delete(bloque);
                // No lo agregues a bloquesResultantes porque ya no estará
            } else {
                bloquesResultantes.add(bloque); // sigue presente
            }
        }
        return bloquesResultantes;
    }

    public List<BloqueHorario> listarTodo(){
        return bloqueHorarioRepository.findAll();
    }

    public List<LocalDate> listarFechasRepeticion(Long id){
        BloqueHorario bloqueRepeticion = bloqueHorarioRepository.findById(id).get();
        List<LocalDate> listaFechasRepeticion = new ArrayList<>();
        List<BloqueHorario> listaAnidados = bloqueHorarioRepository.findByGrupoAnidado(bloqueRepeticion.getGrupoAnidado());
        if (!listaAnidados.isEmpty()){
            for (BloqueHorario bloque : listaAnidados){
                listaFechasRepeticion.add(bloque.getFecha());
            }
        } else {
            listaFechasRepeticion.add(bloqueRepeticion.getFecha());
        }
        return listaFechasRepeticion;
    }

}
