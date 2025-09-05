//package com.ipor.horariostua.usuario;
//
//
//import com.ipor.quimioterapia.gestioncitas.logs.AccionLogGlobal;
//import com.ipor.quimioterapia.gestioncitas.logs.LogService;
//import com.ipor.quimioterapia.spring.usuario.SpringUserService;
//import com.ipor.quimioterapia.spring.usuario.UsuarioSpringDTO;
//import com.ipor.quimioterapia.usuario.rol.RolUsuario;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//import java.util.Objects;
//import java.util.Optional;
//
//@Controller
//@RequestMapping("/app/usuarios")
//public class UsuariosController {
//
//
//    @Autowired
//    private UsuarioService usuarioService;
//    @Autowired
//    private SpringUserService springUserService;
//    @Autowired
//    LogService logService;
//
//    // Mostrar la lista de usuarios
//    @GetMapping("/activos")
//    @ResponseBody
//    public List<UsuarioDTO> listarUsuariosActivos() {
//        List<Usuario> listaUsuarios = usuarioService.getListaUsuariosActivos();
//        return listaUsuarios.stream().map(usuario -> {
//            UsuarioDTO dto = new UsuarioDTO();
//            dto.setId(usuario.getId());
//            dto.setUsername(usuario.getUsername());
//            dto.setSpringUser(usuario.getIsSpringUser());
//            dto.setNombre(usuario.getNombre());
//            dto.setRol(usuario.getRolUsuario().getNombre());
//            dto.setIsActive(usuario.getIsActive());
//            return dto;
//        }).toList();
//    }
//
//
//    public Model listarRoles(Model model){
//        List<RolUsuario> listaRoles = usuarioService.getListaRoles();
//        model.addAttribute("ListaRoles", listaRoles);
//        return model;
//    }
//
//    @PostMapping("/spring/nuevo")
//    public ResponseEntity<String> crearOActualizarUsuario(
//            @RequestParam("username") String username,
//            @RequestParam("nombre") String nombre,
//            @RequestParam("rolUsuario") Long rolId) {
//
//        try {
//            // Obtener datos desde el sistema externo (incluye clave)
//            UsuarioSpringDTO usuarioSpringDTO = springUserService.obtenerUsuarioSpring(username);
//            String claveSpring = usuarioSpringDTO.getClave();
//
//            Optional<Usuario> optionalUsuario = usuarioService.getUsuarioPorUsername(username.toUpperCase());
//            Usuario usuario;
//
//            if (optionalUsuario.isPresent()) {
//                usuario = optionalUsuario.get();
//                usuario.setRolUsuario(usuarioService.getRolPorId(rolId));
//
//                usuario.asignarYEncriptarPassword(claveSpring);
//
//
//                usuario.setNombre(nombre.toUpperCase());
//                usuario.setIsSpringUser(Boolean.TRUE);
//                usuario.setIsActive(Boolean.TRUE);
//                usuario.setChangedPass(Boolean.FALSE);
//                usuarioService.guardarUsuario(usuario);
//
//
//                //LOG GLOBAL----------------------
//                String descripcion = String.format("El usuario %s añadió la cuenta (ya existente localmente) de %s. Fecha: %s",
//                        usuarioService.getUsuarioLogeado().getNombre(),
//                        usuario.getNombre(),
//                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm"))
//                );
//
//                logService.saveDeGlobal(usuarioService.getUsuarioLogeado(), AccionLogGlobal.AGREGA_USUARIO, descripcion);
//                //-------------------------------
//
//
//                return ResponseEntity.ok("Usuario actualizado correctamente");
//            } else {
//                usuario = new Usuario();
//                usuario.setRolUsuario(usuarioService.getRolPorId(rolId));
//
//
//                usuario.asignarYEncriptarPassword(claveSpring);
//
//
//                usuario.setNombre(nombre.toUpperCase());
//                usuario.setUsername(username.toUpperCase());
//                usuario.setIsSpringUser(Boolean.TRUE);
//                usuario.setIsActive(Boolean.TRUE);
//                usuario.setChangedPass(Boolean.FALSE);
//
//                usuarioService.guardarUsuario(usuario);
//
//                //LOG GLOBAL----------------------
//                String descripcion = String.format("El usuario %s añadió la cuenta (no existente localmente) de %s. Fecha: %s",
//                        usuarioService.getUsuarioLogeado().getNombre(),
//                        usuario.getNombre(),
//                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm"))
//                );
//
//                logService.saveDeGlobal(usuarioService.getUsuarioLogeado(), AccionLogGlobal.AGREGA_USUARIO, descripcion);
//                //-------------------------------
//
//
//                return ResponseEntity.status(HttpStatus.CREATED).body("Usuario creado correctamente");
//            }
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error al crear o actualizar el usuario.");
//        }
//    }
//
//
//
//
//
//    @PostMapping("/actualizar/{id}")
//    public ResponseEntity<String> actualizarUsuario(
//            @PathVariable Long id,
//            @RequestParam("username") String username,
//            @RequestParam("password") String password,
//            @RequestParam("nombre") String nombre,
//            @RequestParam("rol") Long rolId) {
//
//        try {
//            Usuario usuario = new Usuario();
//            usuario.setUsername(username.toUpperCase());
//
//            if (password != null && !password.isEmpty()) {
//                usuario.setPassword(password);
//            }
//
//            String rolAnterior = usuarioService.getUsuarioPorId(id).getRolUsuario().getNombre();
//
//            usuario.setNombre(nombre.toUpperCase());
//            usuario.setRolUsuario(usuarioService.getRolPorId(rolId));
//            usuario.setIsActive(Boolean.TRUE);
//
//            usuarioService.actualizarUsuario(id, usuario);
//
//
//
//
//
//            //LOG GLOBAL----------------------
//            String descripcion = String.format("El usuario %s actualizó la cuenta de %s. Fecha: %s Rol: %s -> %s",
//                    usuarioService.getUsuarioLogeado().getNombre(),
//                    usuarioService.getUsuarioPorId(id).getNombre(),
//                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm")),
//                    rolAnterior,
//                    usuarioService.getUsuarioPorId(id).getRolUsuario().getNombre()
//            );
//
//            logService.saveDeGlobal(usuarioService.getUsuarioLogeado(), AccionLogGlobal.ACTUALIZA_USUARIO, descripcion);
//            //-------------------------------
//
//
//
//
//            return ResponseEntity.ok("Usuario actualizado correctamente");
//        } catch (DataIntegrityViolationException e) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body("Nombre de usuario duplicado");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error general al actualizar usuario");
//        }
//    }
//
//
//
//    @PostMapping("/desactivar/{id}")
//    public ResponseEntity<String> desactivarUsuario(@PathVariable Long id) {
//        try {
//            Long idUsuarioLogeado = usuarioService.getIDdeUsuarioLogeado();
//
//            if (!Objects.equals(idUsuarioLogeado, id) && id != 1) {
//                usuarioService.desactivarUsuario(id);
//
//
//                //LOG GLOBAL----------------------
//                String descripcion = String.format("El usuario %s desactivó la cuenta de %s. Fecha: %s",
//                        usuarioService.getUsuarioLogeado().getNombre(),
//                        usuarioService.getUsuarioPorId(id).getNombre(),
//                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm"))
//                );
//
//                logService.saveDeGlobal(usuarioService.getUsuarioLogeado(), AccionLogGlobal.DESACTIVA_USUARIO, descripcion);
//                //-------------------------------
//
//
//
//                return ResponseEntity.ok("Usuario desactivado correctamente");
//            } else {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No puedes desactivarte a ti mismo o al usuario admin");
//            }
//
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al desactivar el usuario");
//        }
//    }
//
//
//    @GetMapping("/rol")
//    public ResponseEntity<String> obtenerRolUsuario() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null) {
//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//            Usuario usuario = usuarioService.getUsuarioPorUsername(userDetails.getUsername()).get();
//
//            if (usuario != null && usuario.getRolUsuario().getId() == 2L) {
//                return ResponseEntity.ok("soporte");
//            }
//        }
//        return ResponseEntity.ok("otro");
//    }
//    @GetMapping("/id")
//    public ResponseEntity<Long> obtenerIdUsuarioLogeado() {
//        Long id = usuarioService.getIDdeUsuarioLogeado();
//        return ResponseEntity.ok(id);
//    }
//
//
//
//
//}
