package com.ipor.horariostua.core.bloquehorario.colaborador;

import com.ipor.horariostua.core.bloquehorario.agrupacion.colaboradores.DetalleColaboradorAgrupacion;
import com.ipor.horariostua.core.bloquehorario.agrupacion.colaboradores.DetalleColaboradorAgrupacionRepository;
import com.ipor.horariostua.core.bloquehorario.agrupacion.colaboradores.DetalleColaboradorAgrupacionService;
import com.ipor.horariostua.core.bloquehorario.colaborador.dto.AgregarColaborador;
import com.ipor.horariostua.core.bloquehorario.colaborador.dto.ColaboradorSeleccionableDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class ColaboradorService {
    @Autowired
    ColaboradorRepository colaboradorRepository;
    @Autowired
    DetalleColaboradorAgrupacionService detalleColaboradorAgrupacionService;

    public Colaborador agregar(AgregarColaborador agregarColaborador){
        Colaborador colaborador;
        if (existeEnBD(agregarColaborador.getEmpleado())){
            colaborador = getColaboradorPorId(agregarColaborador.getEmpleado());
        }else{
            colaborador = new Colaborador();
            colaborador.setServicio("No Asignado");
        }
        colaborador.setId(agregarColaborador.getEmpleado());
        colaborador.setNombreCompleto(agregarColaborador.getNombreCompleto());
        colaborador.setNumeroDocumento(agregarColaborador.getDocumento());
        colaborador.setColegioProfesional(agregarColaborador.getTipoColegio());
        colaborador.setNumeroColegiatura(agregarColaborador.getCmp());
        colaborador.setEspecialidad(agregarColaborador.getEspecialidadNombre());
        colaborador.setIsActive(Boolean.TRUE);
        colaboradorRepository.save(colaborador);
        return colaborador;
    }

    public List<Colaborador> getListarTodo(){
        return colaboradorRepository.findAll();
    }

    public List<Colaborador> getListarPorAgrupacionId(Long idAgrupacion){
        List<DetalleColaboradorAgrupacion> listaDetalleColab = detalleColaboradorAgrupacionService.listarDetallePorIdAgrupacion(idAgrupacion);
        List<Colaborador> listaColaborador = new ArrayList<>();
        for (DetalleColaboradorAgrupacion detalleColab : listaDetalleColab){
            listaColaborador.add(detalleColab.getColaborador());
        }
        return listaColaborador;
    }



    public void getModelSelectColaboradoresActivos(Model model) {
        List<ColaboradorSeleccionableDTO> listaDTO = new ArrayList<>();
        for (Colaborador colaborador : colaboradorRepository.findByIsActiveTrue()) {
            ColaboradorSeleccionableDTO dto = new ColaboradorSeleccionableDTO();
            dto.setId(colaborador.getId());
            dto.setNombreCompleto(colaborador.getNombreCompleto());
            listaDTO.add(dto);
        }
        model.addAttribute("listaSelectColaboradoresActivos", listaDTO);
    }

    public boolean cambiarEstado(Long id, Boolean estado) {
        Optional<Colaborador> optionalColaborador = colaboradorRepository.findById(id);
        if (optionalColaborador.isPresent()) {
            Colaborador colaborador = optionalColaborador.get();
            colaborador.setIsActive(estado);
            colaboradorRepository.save(colaborador);
            return true;
        }
        return false;
    }

    public Colaborador getColaboradorPorId(Long id){
        return colaboradorRepository.findById(id).get();
    }

    public Boolean existeEnBD(Long id){
        return colaboradorRepository.findById(id).isPresent();
    }

}
