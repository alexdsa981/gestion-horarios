//package com.ipor.horariostua.logs;
//
//import com.ipor.horariostua.core.model.Usuario;
//import com.ipor.quimioterapia.gestioncitas.fichapaciente.FichaPaciente;
//import com.ipor.quimioterapia.usuario.Usuario;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Service
//public class LogService {
//
//    @Autowired
//    LogFichaRepository logFichaRepository;
//    @Autowired
//    LogGlobalRepository logGlobalRepository;
//
//    //GLOBAL
//    public void saveDeGlobal(LogGlobal logGlobal) {
//
//    }
//
//    public List<LogGlobal> getListaGlobal() {
//        return logGlobalRepository.findAll();
//    }
//
//    //POR FICHA
//
//    public void saveDeFicha(Usuario usuarioLogeado, FichaPaciente fichaPaciente, AccionLogFicha accion, String valorAnterior, String valorNuevo ,String descripcion) {
//        LogFicha logFicha = new LogFicha();
//        logFicha.setFichaPaciente(fichaPaciente);
//        logFicha.setUsuario(usuarioLogeado);
//        logFicha.setAccion(String.valueOf(accion));
//        logFicha.setFechaHora(LocalDateTime.now());
//        logFicha.setValorAnterior(valorAnterior);
//        logFicha.setValorNuevo(valorNuevo);
//        logFicha.setDescripcion(descripcion);
//        logFichaRepository.save(logFicha);
//    }
//
//    public void saveDeGlobal(Usuario usuarioLogeado, AccionLogGlobal accion, String descripcion){
//        LogGlobal logGlobal = new LogGlobal();
//        logGlobal.setDetalle(descripcion);
//        logGlobal.setEvento(String.valueOf(accion));
//        logGlobal.setUsuario(usuarioLogeado);
//        logGlobal.setFechaHora(LocalDateTime.now());
//        logGlobalRepository.save(logGlobal);
//    }
//
//    public List<LogFicha> getListaFicha(Long idFicha) {
//        return logFichaRepository.findByFichaPaciente_Id(idFicha);
//    }
//
//}
