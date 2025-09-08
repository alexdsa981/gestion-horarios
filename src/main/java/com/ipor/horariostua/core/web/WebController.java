package com.ipor.horariostua.core.web;


import com.ipor.horariostua.core.bloquehorario.colaborador.ColaboradorService;
import com.ipor.horariostua.core.bloquehorario.sede.SedeService;
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
    public String redirigePaginaRegistro(Model model) {
        colaboradorService.getModelSelectColaboradoresActivos(model);
        sedeService.getModelSedesActivas(model);
        model.addAttribute("Titulo", "IPOR - Horarios | Registro");
        return "gestion-horarios/inicio";
    }

    @GetMapping("/personal")
    public String redirigePaginaPersonal(Model model) {
        model.addAttribute("Titulo", "IPOR - Horarios | Mi Personal");
        return "personalsedes/personal/personal";
    }

    @GetMapping("/sedes")
    public String redirigePaginaSedes(Model model) {
        model.addAttribute("Titulo", "IPOR - Horarios | Sedes");
        return "personalsedes/sedes/sedes";
    }

    @GetMapping("/usuarios")
    public String redirigePaginaUsuarios(Model model) {
        model.addAttribute("Titulo", "IPOR - Horarios | Usuarios");
        return "usuarios/usuarios";
    }


}
