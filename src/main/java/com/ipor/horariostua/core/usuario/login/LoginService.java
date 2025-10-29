package com.ipor.horariostua.core.usuario.login;


import com.ipor.horariostua.config.security.ConstantesSeguridad;
import com.ipor.horariostua.config.security.JwtTokenProvider;
import com.ipor.horariostua.core.bloquehorario.agrupacion.colaboradores.DetalleColaboradorAgrupacion;
import com.ipor.horariostua.core.bloquehorario.agrupacion.colaboradores.DetalleColaboradorAgrupacionService;
import com.ipor.horariostua.core.bloquehorario.agrupacion.usuarios.DetalleGruposUsuario;
import com.ipor.horariostua.core.bloquehorario.agrupacion.usuarios.DetalleGruposUsuarioService;
import com.ipor.horariostua.core.bloquehorario.colaborador.Colaborador;
import com.ipor.horariostua.core.bloquehorario.colaborador.ColaboradorService;
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
import java.util.List;
import java.util.Optional;

@Service
public class LoginService {
    @Autowired
    SpringUserService springUserService;
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    ColaboradorService colaboradorService;
    @Autowired
    DetalleColaboradorAgrupacionService detalleColaboradorAgrupacionService;
    @Autowired
    DetalleGruposUsuarioService detalleGruposUsuarioService;
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
            Boolean existeEnSpring = springUserService.obtenerValidacionLoginSpring(username, password);
            Usuario usuarioLocal;
            if (existeEnSpring == null) {
                if (usuarioService.getUsuarioPorUsername(username).isPresent()) {
                    usuarioLocal = usuarioService.getUsuarioPorUsername(username).get();
                } else {
                    username = URLEncoder.encode(username, StandardCharsets.UTF_8);
                    response.sendRedirect("/login?error=unknown&username=" + username);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
            } else {

                Boolean existeLocalmente = usuarioService.existeUsuarioPorUsername(username);
                UsuarioSpringDTO usuarioSpringDTO = springUserService.obtenerUsuarioSpring(username);

                if (existeEnSpring && existeLocalmente) {
                    usuarioLocal = usuarioService.getUsuarioPorUsername(username).get();
                    usuarioLocal.setChangedPass(true);
                    usuarioLocal.setIsSpringUser(Boolean.TRUE);
                    usuarioLocal.setNombre(usuarioSpringDTO.getNombre().toUpperCase());
                    usuarioLocal.asignarYEncriptarPassword(password);
                    usuarioService.save(usuarioLocal);
                } else if (existeEnSpring && !existeLocalmente) {
                    if (springUserService.obtenerIDColaboradorConUsernameSpring(username) == null){
                        //MENSAJE DE USUARIO NO SE PUDO ENLAZAR
                        username = URLEncoder.encode(username, StandardCharsets.UTF_8);
                        response.sendRedirect("/login?error=unknownID");
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

                    }else{
                        Colaborador colaboradorEncontrado = colaboradorService.getColaboradorPorId(springUserService.obtenerIDColaboradorConUsernameSpring(username));
                        List<DetalleColaboradorAgrupacion> listaAgrupaciones = detalleColaboradorAgrupacionService.listarDetallePorIdColaborador(colaboradorEncontrado.getId());

                        if (!listaAgrupaciones.isEmpty()){
                            usuarioLocal = new Usuario();
                            usuarioLocal.setIsActive(true);
                            usuarioLocal.setRolUsuario(rolUsuarioRepository.findById(3l).get());
                            usuarioLocal.setIsSpringUser(true);
                            usuarioLocal.setChangedPass(true);
                            usuarioLocal.setNombre(usuarioSpringDTO.getNombre().toUpperCase());
                            usuarioLocal.setUsername(usuarioSpringDTO.getUsuario().toUpperCase());
                            usuarioLocal.asignarYEncriptarPassword(password);
                            usuarioLocal.setAgrupacionSeleccionada(listaAgrupaciones.get(0).getAgrupacion());
                            usuarioLocal = usuarioService.save(usuarioLocal);
                            for (DetalleColaboradorAgrupacion detalleCA : listaAgrupaciones){
                                detalleGruposUsuarioService.agrupar(usuarioLocal, detalleCA.getAgrupacion().getId());
                            }
                        }else{
                            //MENSAJE DE NO ASIGNADO A NINGUNA AGRUPACION
                            username = URLEncoder.encode(username, StandardCharsets.UTF_8);
                            response.sendRedirect("/login?error=unknown-group");
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                        }
                    }
                } else {
                    if (usuarioService.getUsuarioPorUsername(username).isPresent()) {
                        usuarioLocal = usuarioService.getUsuarioPorUsername(username).get();
                    } else {
                        username = URLEncoder.encode(username, StandardCharsets.UTF_8);
                        response.sendRedirect("/login?error=badCredentials&username=" + username);
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                    }
                }
            }
            if (!usuarioLocal.getIsActive()) {
                username = URLEncoder.encode(username, StandardCharsets.UTF_8);
                response.sendRedirect("/login?error=inactive&username=" + username);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenProvider.generarToken(authentication);
            Cookie jwtCookie = new Cookie("JWT", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setMaxAge((int) (ConstantesSeguridad.JWT_EXPIRATION_TOKEN) / 1000);
            jwtCookie.setPath("/");
            response.addCookie(jwtCookie);

            if (!usuarioLocal.getChangedPass()) {
                response.sendRedirect("/home?changePassword");
                return ResponseEntity.ok().build();
            }
            //LOGIN EXITOSO
            response.sendRedirect("/home");
            return ResponseEntity.ok().build();
        } catch (BadCredentialsException e) {
            //System.out.println(e.getMessage());
            username = URLEncoder.encode(username, StandardCharsets.UTF_8);
            response.sendRedirect("/login?error=badCredentials&username=" + username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            System.out.println("Error 2");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }






}
