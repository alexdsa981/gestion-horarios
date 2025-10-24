package com.ipor.horariostua.core.bloquehorario.colaborador;

import com.ipor.horariostua.core.bloquehorario.BloqueHorarioService;
import com.ipor.horariostua.core.bloquehorario.agrupacion.colaboradores.DetalleColaboradorAgrupacion;
import com.ipor.horariostua.core.bloquehorario.agrupacion.colaboradores.DetalleColaboradorAgrupacionService;
import com.ipor.horariostua.core.bloquehorario.colaborador.dto.AgregarColaboradorDTO;
import com.ipor.horariostua.core.bloquehorario.colaborador.dto.ColaboradorSeleccionableDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ColaboradorService {
    @Autowired
    private ColaboradorRepository colaboradorRepository;
    @Autowired
    private DetalleColaboradorAgrupacionService detalleColaboradorAgrupacionService;


    public Colaborador agregar(AgregarColaboradorDTO agregarColaboradorDTO){
        Colaborador colaborador;
        if (existeEnBD(agregarColaboradorDTO.getEmpleado())){
            colaborador = getColaboradorPorId(agregarColaboradorDTO.getEmpleado());
        }else{
            colaborador = new Colaborador();
            colaborador.setServicio("No Asignado");
        }
        colaborador.setId(agregarColaboradorDTO.getEmpleado());
        colaborador.setNombreCompleto(agregarColaboradorDTO.getNombreCompleto());
        colaborador.setNumeroDocumento(agregarColaboradorDTO.getDocumento());
        colaborador.setColegioProfesional(agregarColaboradorDTO.getTipoColegio());
        colaborador.setNumeroColegiatura(agregarColaboradorDTO.getCmp());
        colaborador.setEspecialidad(agregarColaboradorDTO.getEspecialidadNombre());
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



    public void getModelSelectColaboradoresActivosPorAgrupacion(Model model, Long idAgrupacion) {
        List<DetalleColaboradorAgrupacion> listaDetalleColab = detalleColaboradorAgrupacionService.listarDetallePorIdAgrupacion(idAgrupacion);
        List<ColaboradorSeleccionableDTO> listaDTO = new ArrayList<>();
        for (DetalleColaboradorAgrupacion detalle : listaDetalleColab){
            ColaboradorSeleccionableDTO dto = new ColaboradorSeleccionableDTO();
            dto.setId(detalle.getColaborador().getId());
            dto.setNombreCompleto(detalle.getColaborador().getNombreCompleto());
            listaDTO.add(dto);
        }
        model.addAttribute("listaSelectColaboradoresActivosPorAgrupacion", listaDTO);
    }

    public boolean cambiarEstado(Long idAgrupacion ,Long idColaborador, Boolean estado) {
        Optional<Colaborador> optionalColaborador = colaboradorRepository.findById(idColaborador);
        if (optionalColaborador.isPresent()) {
            Colaborador colaborador = optionalColaborador.get();
            DetalleColaboradorAgrupacion detalle = detalleColaboradorAgrupacionService.getDetallePorColaboradorYAgrupacion(colaborador.getId(), idAgrupacion);
            detalle.setIsActive(estado);
            detalleColaboradorAgrupacionService.save(detalle);
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
