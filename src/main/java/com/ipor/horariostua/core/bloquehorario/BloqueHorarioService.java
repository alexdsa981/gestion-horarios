package com.ipor.horariostua.core.bloquehorario;

import com.ipor.horariostua.core.bloquehorario.agrupacion.AgrupacionService;
import com.ipor.horariostua.core.bloquehorario.agrupacion.colaboradores.DetalleColaboradorAgrupacionService;
import com.ipor.horariostua.core.bloquehorario.almuerzo.Almuerzo;
import com.ipor.horariostua.core.bloquehorario.almuerzo.AlmuerzoService;
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
    @Autowired
    AlmuerzoService almuerzoService;


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
        bloqueHorario.setIsTurnoNoche(Boolean.FALSE);

        //ALMUERZO
        if (dto.getHoraInicio() != null && dto.getHoraInicioAlmuerzo() != null){
            Almuerzo almuerzo = new Almuerzo();
            almuerzo.setHoraInicio(dto.getHoraInicioAlmuerzo());
            almuerzo.setHoraFin(dto.getHoraFinAlmuerzo());
            almuerzo = almuerzoService.save(almuerzo);

            bloqueHorario.setAlmuerzo(almuerzo);
        }
        bloqueHorarioRepository.save(bloqueHorario);
        return bloqueHorario;
    }


    public BloqueHorario agregarTN(Recibido_BH_DTO dto){
        if (hayCruceCrearHorario(dto.getIdColaborador(), dto.getFecha(), dto.getHoraInicio(), dto.getHoraFin())) {
            throw new IllegalArgumentException("El colaborador ya tiene un bloque horario que se cruza en esa fecha.");
        }

        BloqueHorario bloqueHorario = new BloqueHorario();
        bloqueHorario.setHorarioLaboral(horarioLaboralService.getUltimoHorarioLaboral());
        bloqueHorario.setColaborador(colaboradorService.getColaboradorPorId(dto.getIdColaborador()));
        bloqueHorario.setFecha(dto.getFecha());
        bloqueHorario.setHoraInicio(LocalTime.of(20,0));
        bloqueHorario.setHoraFin(LocalTime.of(21,0));

        bloqueHorario.setAgrupacion(agrupacionService.getAgrupacionPorId(dto.getIdAgrupacion()));

        bloqueHorario.setSede(sedeService.getSedePorId(dto.getIdSede()));
        bloqueHorario.setGrupoAnidado(bloqueHorarioRepository.findMaxGrupoAnidado() + 1);
        bloqueHorario.setIsTurnoNoche(Boolean.TRUE);
        bloqueHorarioRepository.save(bloqueHorario);
        return bloqueHorario;
    }

    public void eliminar(Long id){
        bloqueHorarioRepository.deleteById(id);
    }

    public BloqueHorario editar(Recibido_BH_DTO dto, Long id){

        BloqueHorario bloque = bloqueHorarioRepository.findById(id).get();


        if (bloque.getIsTurnoNoche() == null || !bloque.getIsTurnoNoche()){
            if (hayCruceHorarioEditar(dto.getIdColaborador(), dto.getFecha(), dto.getHoraInicio(), dto.getHoraFin(), id)) {
                throw new IllegalArgumentException("El colaborador ya tiene un bloque horario que se cruza en esa fecha.");
            }
            bloque.setHoraInicio(dto.getHoraInicio());
            bloque.setHoraFin(dto.getHoraFin());
        }else{
            if (hayCruceHorarioEditar(dto.getIdColaborador(), dto.getFecha(), bloque.getHoraInicio(), bloque.getHoraFin(), id)) {
                throw new IllegalArgumentException("El colaborador ya tiene un bloque horario que se cruza en esa fecha.");
            }
        }
        //almuerzo
        if(dto.getHoraInicioAlmuerzo() != null && dto.getHoraFinAlmuerzo() !=null){
            Almuerzo almuerzo;
            if (bloque.getAlmuerzo() == null){
                almuerzo = new Almuerzo();
            } else{
                almuerzo = bloque.getAlmuerzo();
            }
            almuerzo.setHoraFin(dto.getHoraFinAlmuerzo());
            almuerzo.setHoraInicio(dto.getHoraInicioAlmuerzo());
            almuerzo = almuerzoService.save(almuerzo);
            bloque.setAlmuerzo(almuerzo);
        }


        bloque.setFecha(dto.getFecha());
        bloque.setColaborador(colaboradorService.getColaboradorPorId(dto.getIdColaborador()));
        bloque.setSede(sedeService.getSedePorId(dto.getIdSede()));

        bloque.setGrupoAnidado(bloqueHorarioRepository.findMaxGrupoAnidado() + 1);
        bloque.setIsTurnoNoche(bloque.getIsTurnoNoche());



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
                repetido.setIsTurnoNoche(bloqueRepetir.getIsTurnoNoche());

                if (bloqueRepetir.getAlmuerzo() != null){
                    if(bloqueRepetir.getAlmuerzo().getHoraInicio() != null && bloqueRepetir.getAlmuerzo().getHoraFin() != null){
                        Almuerzo almuerzo = new Almuerzo();
                        almuerzo.setHoraInicio(bloqueRepetir.getAlmuerzo().getHoraInicio());
                        almuerzo.setHoraFin(bloqueRepetir.getAlmuerzo().getHoraFin());
                        almuerzo = almuerzoService.save(almuerzo);
                        repetido.setAlmuerzo(almuerzo);
                    }
                }
                bloquesResultantes.add(bloqueHorarioRepository.save(repetido));
            }
        }

        // ELIMINAR: fechas actuales que no están en las recibidas
        for (BloqueHorario bloque : listaRepeticionActual) {
            if (!fechasRecibidas.contains(bloque.getFecha())) {
                bloqueHorarioRepository.delete(bloque);
            } else {
                bloquesResultantes.add(bloque);
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


    /*HORAS COLABORADOR*/
    public double  sumarHorasBloques(List<BloqueHorario> bloques) {
        double total = 0;
        for (BloqueHorario bloque : bloques) {
            if (bloque.getHoraInicio() != null && bloque.getHoraFin() != null && (bloque.getIsTurnoNoche() == null || !bloque.getIsTurnoNoche())) {
                int minutos = (int) java.time.Duration.between(bloque.getHoraInicio(), bloque.getHoraFin()).toMinutes();
                total += minutos;
            }
        }
        return total / 60;
    }

    public double  sumarHorasAlmuerzo(List<BloqueHorario> bloques) {
        double total = 0;
        for (BloqueHorario bloque : bloques) {
            if (bloque.getAlmuerzo() != null){
                if (bloque.getAlmuerzo().getHoraInicio() != null && bloque.getAlmuerzo().getHoraFin() != null) {
                    int minutos = (int) java.time.Duration.between(bloque.getAlmuerzo().getHoraInicio(), bloque.getAlmuerzo().getHoraFin()).toMinutes();
                    total += minutos;
                }
            }
        }
        return total / 60;
    }

    public List<BloqueHorario> bloquesDeColaboradorPorMes(Long colaboradorId, Long agrupacionId, LocalDate inicioMes, LocalDate finMes){
        return bloqueHorarioRepository.findByColaboradorIdAndAgrupacionIdAndFechaBetween(colaboradorId, agrupacionId, inicioMes, finMes);
    }


    /*FILTRO A COLABORADOR*/


}
