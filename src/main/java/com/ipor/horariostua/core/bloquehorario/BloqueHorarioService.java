package com.ipor.horariostua.core.bloquehorario;

import com.ipor.horariostua.core.bloquehorario.agrupacion.AgrupacionService;
import com.ipor.horariostua.core.bloquehorario.agrupacion.colaboradores.DetalleColaboradorAgrupacionService;
import com.ipor.horariostua.core.bloquehorario.colaborador.ColaboradorService;
import com.ipor.horariostua.core.bloquehorario.bloquehorarioDTO.Recibido_BH_DTO;
import com.ipor.horariostua.core.bloquehorario.bloquehorarioDTO.Repetir_BH_DTO;
import com.ipor.horariostua.core.bloquehorario.horariolaboral.HorarioLaboralService;
import com.ipor.horariostua.core.bloquehorario.sede.SedeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
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


    public BloqueHorario getPorId(Long id){
        return bloqueHorarioRepository.findById(id).get();
    }

    public List<BloqueHorario> listarPorAgrupacionId(Long idAgrupacion){
        return bloqueHorarioRepository.findByAgrupacionId(idAgrupacion);
    }

    public List<BloqueHorario> listarPorAnoMesSede(int ano, int mes, Long sedeId){
        LocalDate desde = LocalDate.of(ano, mes, 1);
        LocalDate hasta = desde.withDayOfMonth(desde.lengthOfMonth());
        return bloqueHorarioRepository.findByFechaBetweenAndSedeId(desde, hasta, sedeId);
    }

    public List<BloqueHorario> listarPorRangoFechaYagrupacion(Long idAgrupacion, LocalDate desde, LocalDate hasta ){
       return bloqueHorarioRepository.findByAgrupacionIdAndFechaBetween(idAgrupacion, desde, hasta);
    }

    public List<BloqueHorario> listarPorFechaYagrupacion(Long idAgrupacion, LocalDate fecha ){
        return bloqueHorarioRepository.findByAgrupacionIdAndFecha(idAgrupacion, fecha);
    }

    public boolean hayCruceCrearHorario(Long idColaborador, LocalDate fecha, LocalTime nuevoInicio, LocalTime nuevoFin) {
        List<BloqueHorario> bloques = bloqueHorarioRepository.findByColaboradorIdAndFecha(idColaborador, fecha);

        for (BloqueHorario existente : bloques) {
            LocalTime existenteInicio = existente.getHoraInicio();
            LocalTime existenteFin = existente.getHoraFin();

            // Verifica traslape: (nuevoInicio < existenteFin) && (nuevoFin > existenteInicio)
            if (nuevoInicio.isBefore(existenteFin) && nuevoFin.isAfter(existenteInicio)) {
                return true; // Hay cruce
            }
        }
        return false; // No hay cruce
    }
    public boolean hayCruceHorarioEditar(Long idColaborador, LocalDate fecha, LocalTime nuevoInicio, LocalTime nuevoFin, Long idBloqueEditar) {
        List<BloqueHorario> bloques = bloqueHorarioRepository.findByColaboradorIdAndFecha(idColaborador, fecha);

        for (BloqueHorario existente : bloques) {
            if (existente.getId().equals(idBloqueEditar)) continue; // Ignora el que se está editando
            LocalTime existenteInicio = existente.getHoraInicio();
            LocalTime existenteFin = existente.getHoraFin();
            if (nuevoInicio.isBefore(existenteFin) && nuevoFin.isAfter(existenteInicio)) {
                return true;
            }
        }
        return false;
    }


    public BloqueHorario agregar(Recibido_BH_DTO dto){
        if (hayCruceCrearHorario(dto.getIdColaborador(), dto.getFecha(), dto.getHoraInicio(), dto.getHoraFin())) {
            throw new IllegalArgumentException("El colaborador ya tiene un bloque horario que se cruza en esa fecha.");
        }
        BloqueHorario bloqueHorario = new BloqueHorario();
        bloqueHorario.setHorarioLaboral(horarioLaboralService.getUltimoHorarioLaboral());
        bloqueHorario.setColaborador(colaboradorService.getColaboradorPorId(dto.getIdColaborador()));
        bloqueHorario.setFecha(dto.getFecha());
        bloqueHorario.setHoraInicio(dto.getHoraInicio());
        bloqueHorario.setHoraFin(dto.getHoraFin());

        bloqueHorario.setAgrupacion(agrupacionService.getAgrupacionPorId(dto.getIdAgrupacion()));

        bloqueHorario.setSede(sedeService.getSedePorId(dto.getIdSede()));
        bloqueHorario.setGrupoAnidado(bloqueHorarioRepository.findMaxGrupoAnidado() + 1);

        bloqueHorarioRepository.save(bloqueHorario);
        return bloqueHorario;
    }

    public void eliminar(Long id){
        bloqueHorarioRepository.deleteById(id);
    }

    public BloqueHorario editar(Recibido_BH_DTO dto, Long id){
        if (hayCruceHorarioEditar(dto.getIdColaborador(), dto.getFecha(), dto.getHoraInicio(), dto.getHoraFin(), id)) {
            throw new IllegalArgumentException("El colaborador ya tiene un bloque horario que se cruza en esa fecha.");
        }
        BloqueHorario bloque = bloqueHorarioRepository.findById(id).get();
        bloque.setFecha(dto.getFecha());
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

    public double  sumarHorasBloques(List<BloqueHorario> bloques) {
        double total = 0;
        for (BloqueHorario bloque : bloques) {
            if (bloque.getHoraInicio() != null && bloque.getHoraFin() != null) {
                int minutos = (int) java.time.Duration.between(bloque.getHoraInicio(), bloque.getHoraFin()).toMinutes();
                total += minutos;
            }
        }
        return total / 60;
    }

    public List<BloqueHorario> bloquesDeColaboradorPorMes(Long colaboradorId, Long agrupacionId, LocalDate inicioMes, LocalDate finMes){
        return bloqueHorarioRepository.findByColaboradorIdAndAgrupacionIdAndFechaBetween(colaboradorId, agrupacionId, inicioMes, finMes);
    }

}
