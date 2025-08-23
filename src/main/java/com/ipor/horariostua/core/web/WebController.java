package com.ipor.horariostua.core.web;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {


    /*
    @GetMapping("/")
    public String redirectToInicio() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String redirigePaginaLogin(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "username", required = false) String username,
            Model model) {
        // Obtén la autenticación actual
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !authentication.getPrincipal().equals("anonymousUser")) {
            return "redirect:/citas";
        }
        model.addAttribute("error", error);
        model.addAttribute("username", username);
        return "index";
    }
    */

    @GetMapping("/registro-horarios")
    public String redirigePaginaInicio(Model model) {
        model.addAttribute("Titulo", "Gestión Horarios | Principal");
        return "gestion-horarios/inicio";
    }


}
