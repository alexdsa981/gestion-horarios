package com.ipor.horariostua.core.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class EmpleadoSpringService {
    @Autowired
    RestTemplate restTemplate;


    public List<EmpleadoSpringDTO> obtenerEmpleadoPorTexto(String texto) {
        String url = "http://localhost:9000/api/gh/colaborador/lista/" + texto;
        EmpleadoSpringDTO[] empleados = restTemplate.getForObject(url, EmpleadoSpringDTO[].class);
        return Arrays.asList(empleados);
    }
}
