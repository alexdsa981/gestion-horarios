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
        detalleAgrupacion.setEventoColor(getRandomColorHex());
        detalleColaboradorRepository.save(detalleAgrupacion);
    }

    public List<DetalleColaboradorAgrupacion> listarDetallePorIdAgrupacion(Long idAgrupacion){
        return detalleColaboradorRepository.findByAgrupacionId(idAgrupacion);
    }

    public DetalleColaboradorAgrupacion getDetallePorColaboradorYAgrupacion(Long idColaborador, Long idAgrupacion) {
        Optional<DetalleColaboradorAgrupacion> detalle = detalleColaboradorRepository.findByColaboradorIdAndAgrupacionId(idColaborador, idAgrupacion);
        return detalle.orElse(null);
    }


    public String getRandomColorHex() {
        Random rand = new Random();
        int r, g, b;
        do {
            r = rand.nextInt(256);
            g = rand.nextInt(256);
            b = rand.nextInt(256);
            // Evitar colores oscuros cerca del negro (todos menores a 40)
        } while (r < 40 && g < 40 && b < 40);

        // Formato hexadecimal
        return String.format("#%02X%02X%02X", r, g, b);
    }
}
