package com.ipor.horariostua.core.web;


import com.ipor.horariostua.bloquehorario.colaborador.ColaboradorService;
import com.ipor.horariostua.bloquehorario.sede.SedeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @Autowired
    SedeService sedeService;
    @Autowired
    ColaboradorService colaboradorService;


    @GetMapping("/registro-horarios")
    public String redirigePaginaInicio(Model model) {
        colaboradorService.getModelSelectColaboradoresActivos(model);
        sedeService.getModelSedesActivas(model);
        model.addAttribute("Titulo", "Gestión Horarios | Principal");
        return "gestion-horarios/inicio";
    }


}
