//package com.ipor.horariostua.core.web;
//
//import com.ipor.quimioterapia.usuario.Usuario;
//import com.ipor.quimioterapia.usuario.UsuarioService;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ModelAttribute;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
//@ControllerAdvice
//public class GlobalAdviceController {
//
//    @Autowired
//    UsuarioService usuarioService;
//
//    @ModelAttribute
//    public void datosTiempo(Model model, HttpServletRequest request) {
//        LocalDateTime now = LocalDateTime.now(); // Fecha y hora actual
//
//        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
//        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//        DateTimeFormatter dateInputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Para input type="date"
//        model.addAttribute("horaActual", now.format(timeFormatter));
//        model.addAttribute("fechaActual", now.format(dateFormatter));
//        model.addAttribute("fechaInputActual", now.format(dateInputFormatter));
//
//    }
//
//
//    @ModelAttribute
//    public void datosHeader(Model model, HttpServletRequest request) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.isAuthenticated() &&
//                !authentication.getPrincipal().equals("anonymousUser")) {
//            Usuario usuario = usuarioService.getUsuarioPorId(usuarioService.getIDdeUsuarioLogeado());
//            model.addAttribute("idRolUsuario", usuario.getRolUsuario().getId());
//            model.addAttribute("idUsuarioLogeado", usuario.getId());
//            model.addAttribute("nombreUsuario", usuario.getNombre());
//            model.addAttribute("currentPath", request.getRequestURI());
//
//        }
//    }
//}
//
