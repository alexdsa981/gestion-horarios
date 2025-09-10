package com.ipor.horariostua.core.usuario.login;

import com.ipor.horariostua.config.security.CookieUtil;
import com.ipor.horariostua.core.usuario.Usuario;
import com.ipor.horariostua.core.usuario.UsuarioService;
import com.ipor.horariostua.logs.AccionLogGlobal;
import com.ipor.horariostua.logs.LogService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/app")
public class LoginController {

    @Autowired
    UsuarioService usuarioService;
    @Autowired
    LoginService loginService;
    @Autowired
    LogService logService;


    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestParam String username, @RequestParam String password, HttpServletResponse response) throws IOException {
        return loginService.logearUsuarioAlSistema(username, password, response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) throws IOException {
        Usuario usuario = usuarioService.getUsuarioLogeado();

        String descripcion = String.format("El usuario %s cerró sesión el %s.",
                usuario.getNombre(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm"))
        );

        logService.saveDeGlobal(usuario, AccionLogGlobal.CIERRE_SESION, descripcion);

        CookieUtil.removeJwtCookie(response);
        response.sendRedirect("/login");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/cambiar-password")
    public ResponseEntity<Void> cambiarPassword(@RequestParam String newPassword) {
        Long iDUsuarioLogeado = usuarioService.getIDdeUsuarioLogeado();
        Usuario usuario = usuarioService.getUsuarioPorId(iDUsuarioLogeado);
            usuario.asignarYEncriptarPassword(newPassword);
            usuario.setChangedPass(true);
            usuarioService.save(usuario);
            return ResponseEntity.ok().build();

    }


}
