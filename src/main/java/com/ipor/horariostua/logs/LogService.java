package com.ipor.horariostua.logs;


import com.ipor.horariostua.core.bloquehorario.BloqueHorario;
import com.ipor.horariostua.core.usuario.Usuario;
import com.ipor.horariostua.logs.global.LogGlobal;
import com.ipor.horariostua.logs.global.LogGlobalRepository;
import com.ipor.horariostua.logs.horario.LogHorarios;
import com.ipor.horariostua.logs.horario.LogHorariosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LogService {

    @Autowired
    LogHorariosRepository logHorariosRepository;
    @Autowired
    LogGlobalRepository logGlobalRepository;

    //GLOBAL
    public void saveDeGlobal(LogGlobal logGlobal) {

    }

    public List<LogGlobal> getListaGlobal() {
        return logGlobalRepository.findAll();
    }

    //POR FICHA

    public void saveDeFicha(Usuario usuarioLogeado, BloqueHorario bloqueHorario, AccionLogFicha accion, String valorAnterior, String valorNuevo , String descripcion) {
        LogHorarios logHorario = new LogHorarios();
        logHorario.setBloqueHorario(bloqueHorario);
        logHorario.setUsuario(usuarioLogeado);
        logHorario.setAccion(String.valueOf(accion));
        logHorario.setFechaHora(LocalDateTime.now());
        logHorario.setValorAnterior(valorAnterior);
        logHorario.setValorNuevo(valorNuevo);
        logHorario.setDescripcion(descripcion);
        logHorariosRepository.save(logHorario);
    }

    public void saveDeGlobal(Usuario usuarioLogeado, AccionLogGlobal accion, String descripcion){
        LogGlobal logGlobal = new LogGlobal();
        logGlobal.setDetalle(descripcion);
        logGlobal.setEvento(String.valueOf(accion));
        logGlobal.setUsuario(usuarioLogeado);
        logGlobal.setFechaHora(LocalDateTime.now());
        logGlobalRepository.save(logGlobal);
    }
}
