package com.ipor.horariostua.core.bloquehorario.agrupacion.colaboradores;

import com.ipor.horariostua.core.bloquehorario.agrupacion.Agrupacion;
import com.ipor.horariostua.core.bloquehorario.agrupacion.AgrupacionService;
import com.ipor.horariostua.core.bloquehorario.colaborador.Colaborador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class DetalleColaboradorAgrupacionService {
    @Autowired
    DetalleColaboradorAgrupacionRepository detalleColaboradorRepository;
    @Autowired
    AgrupacionService agrupacionService;

    public void save(DetalleColaboradorAgrupacion detalle){
        detalleColaboradorRepository.save(detalle);
    }
    public void agrupar(Colaborador colaborador, Long idAgrupacion){
        DetalleColaboradorAgrupacion detalleAgrupacion;
        if (detalleColaboradorRepository.findByColaboradorIdAndAgrupacionId(colaborador.getId(), idAgrupacion).isPresent()){
            detalleAgrupacion = detalleColaboradorRepository.findByColaboradorIdAndAgrupacionId(colaborador.getId(), idAgrupacion).get();
        }else{
            detalleAgrupacion = new DetalleColaboradorAgrupacion();
        }
        Agrupacion agrupacion = agrupacionService.getAgrupacionPorId(idAgrupacion);
        detalleAgrupacion.setColaborador(colaborador);
        detalleAgrupacion.setAgrupacion(agrupacion);
        detalleAgrupacion.setIsActive(true);
        detalleAgrupacion.setEventoColor(getRandomPastelColorHex());
        detalleColaboradorRepository.save(detalleAgrupacion);
    }

    public List<DetalleColaboradorAgrupacion> listarDetallePorIdAgrupacion(Long idAgrupacion){
        return detalleColaboradorRepository.findByAgrupacionIdAndIsActiveTrue(idAgrupacion);
    }

    public DetalleColaboradorAgrupacion getDetallePorColaboradorYAgrupacion(Long idColaborador, Long idAgrupacion) {
        Optional<DetalleColaboradorAgrupacion> detalle = detalleColaboradorRepository.findByColaboradorIdAndAgrupacionId(idColaborador, idAgrupacion);
        return detalle.orElse(null);
    }

    public List<DetalleColaboradorAgrupacion> listarDetallePorIdColaborador(Long idColaborador){
        return detalleColaboradorRepository.findByColaboradorIdAndIsActiveTrue(idColaborador);
    }



    public String getRandomPastelColorHex() {
        Random rand = new Random();
        int r = rand.nextInt(256);
        int g = rand.nextInt(256);
        int b = rand.nextInt(256);

        // Mezclamos con blanco (media entre color y 255)
        r = (r + 255) / 2;
        g = (g + 255) / 2;
        b = (b + 255) / 2;

        return String.format("#%02X%02X%02X", r, g, b);
    }
}
