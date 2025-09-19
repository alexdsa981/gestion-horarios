package com.ipor.horariostua.core.usuario.login;


import com.ipor.horariostua.config.security.ConstantesSeguridad;
import com.ipor.horariostua.config.security.JwtTokenProvider;
import com.ipor.horariostua.core.usuario.Usuario;
import com.ipor.horariostua.core.usuario.UsuarioService;
import com.ipor.horariostua.core.usuario.caracteristicas.rol.RolUsuarioRepository;
import com.ipor.horariostua.core.usuario.spring.SpringUserService;
import com.ipor.horariostua.core.usuario.spring.UsuarioSpringDTO;
import com.ipor.horariostua.logs.AccionLogGlobal;
import com.ipor.horariostua.logs.LogService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class LoginService {
    @Autowired
    SpringUserService springUserService;
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    RolUsuarioRepository rolUsuarioRepository;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Autowired
    LogService logService;


    public ResponseEntity<Void> logearUsuarioAlSistema(String username, String password, HttpServletResponse response) throws IOException {
        try {
            username = username.toUpperCase();
            System.out.println("Iniciando login para usuario: " + username);

            // 1. Verificar si el usuario existe localmente
            Optional<Usuario> optionalUsuario = usuarioService.getUsuarioPorUsername(username.toUpperCase());

            if (optionalUsuario.isEmpty()) {
                System.out.println("Usuario no encontrado localmente: " + username);
                response.sendRedirect("/login?error=unregistered&username=" + URLEncoder.encode(username, StandardCharsets.UTF_8));
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Usuario usuarioLocal = optionalUsuario.get();
            System.out.println("Usuario encontrado localmente: " + usuarioLocal.getUsername());

            // 2. Verificar si está activo
            if (!usuarioLocal.getIsActive()) {
                System.out.println("Usuario inactivo: " + username);
                response.sendRedirect("/login?error=inactive&username=" + URLEncoder.encode(username, StandardCharsets.UTF_8));
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            boolean credencialesValidas;

            if (usuarioLocal.getIsSpringUser()) {
                // 3.1 Validar credenciales con Spring si es usuario de Spring
                System.out.println("Validando credenciales contra sistema Spring externo...");
                credencialesValidas = Boolean.TRUE.equals(springUserService.obtenerValidacionLoginSpring(username, password));

                if (!credencialesValidas) {
                    System.out.println("Credenciales inválidas para usuario Spring: " + username);
                    response.sendRedirect("/login?error=badCredentials&username=" + URLEncoder.encode(username, StandardCharsets.UTF_8));
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }

                // 3.2 Actualizar datos locales con info de Spring
                UsuarioSpringDTO usuarioSpringDTO = springUserService.obtenerUsuarioSpring(username);
                usuarioLocal.setChangedPass(true);
                usuarioLocal.setNombre(usuarioSpringDTO.getNombre().toUpperCase());
                usuarioLocal.asignarYEncriptarPassword(password); // actualizar contraseña local
                usuarioService.save(usuarioLocal);
                System.out.println("Datos del usuario actualizados desde Spring");

            } else {
                // 4. Validar solo localmente (intenta autenticar con la contraseña local)
                try {
                    Authentication authentication = authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(username, password)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    credencialesValidas = true;
                } catch (BadCredentialsException e) {
                    System.out.println("Credenciales inválidas localmente para usuario no-Spring: " + username);
                    response.sendRedirect("/login?error=badCredentials&username=" + URLEncoder.encode(username, StandardCharsets.UTF_8));
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }
            }

            // 5. Autenticación local final (para ambos tipos de usuario, ya con datos validados)
            System.out.println("Autenticando contra sistema local (post-validación)...");
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 6. Generar JWT
            System.out.println("Generando JWT...");
            String token = jwtTokenProvider.generarToken(authentication);
            Cookie jwtCookie = new Cookie("JWT", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setMaxAge((int) (ConstantesSeguridad.JWT_EXPIRATION_TOKEN / 1000));
            jwtCookie.setPath("/");
            response.addCookie(jwtCookie);

            // 7. Redirección final
            if (!usuarioLocal.getChangedPass()) {
                System.out.println("Redirigiendo a /home");
                response.sendRedirect("/home");
            } else {
                System.out.println("Redirigiendo a /home");
                response.sendRedirect("home");
            }


            String descripcion = String.format("El usuario %s inició sesión el %s.",
                    usuarioLocal.getNombre(),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm"))
            );

            logService.saveDeGlobal(usuarioLocal, AccionLogGlobal.INICIO_SESION, descripcion);


            return ResponseEntity.ok().build();

        } catch (BadCredentialsException e) {
            System.out.println("Excepción: BadCredentialsException");
            response.sendRedirect("/login?error=badCredentials&username=" + URLEncoder.encode(username, StandardCharsets.UTF_8));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        } catch (Exception e) {
            System.out.println("Excepción general:");
            e.printStackTrace();
            response.sendRedirect("/login?error=unknown");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }







}
