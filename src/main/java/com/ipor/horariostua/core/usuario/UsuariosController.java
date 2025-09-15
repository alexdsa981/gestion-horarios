package com.ipor.horariostua.core.usuario;


import com.ipor.horariostua.core.bloquehorario.agrupacion.Agrupacion;
import com.ipor.horariostua.core.bloquehorario.agrupacion.AgrupacionService;
import com.ipor.horariostua.core.bloquehorario.agrupacion.usuarios.DetalleGruposUsuarioService;
import com.ipor.horariostua.core.usuario.caracteristicas.rol.RolUsuario;
import com.ipor.horariostua.core.usuario.spring.SpringUserService;
import com.ipor.horariostua.core.usuario.spring.UsuarioSpringDTO;
import com.ipor.horariostua.logs.AccionLogGlobal;
import com.ipor.horariostua.logs.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/app/usuarios")
public class UsuariosController {


    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private LogService logService;
    @Autowired
    private SpringUserService springUserService;
    @Autowired
    private DetalleGruposUsuarioService detalleGruposUsuarioService;
    @Autowired
    private AgrupacionService agrupacionService;

    // Mostrar la lista de usuarios
    @GetMapping("/activos")
    @ResponseBody
    public List<UsuarioDTO> listarUsuariosActivos() {
        List<Usuario> listaUsuarios = usuarioService.getListaUsuariosActivos();
        return listaUsuarios.stream().map(usuario -> {
            UsuarioDTO dto = new UsuarioDTO();
            dto.setId(usuario.getId());
            dto.setUsername(usuario.getUsername());
            dto.setSpringUser(usuario.getIsSpringUser());
            dto.setNombre(usuario.getNombre());
            dto.setRol(usuario.getRolUsuario().getNombre());
            dto.setIsActive(usuario.getIsActive());
            return dto;
        }).toList();
    }


    public Model listarRoles(Model model){
        List<RolUsuario> listaRoles = usuarioService.getListaRoles();
        model.addAttribute("ListaRoles", listaRoles);
        return model;
    }

    @PostMapping("/spring/nuevo")
    public ResponseEntity<String> crearOActualizarUsuario(@RequestBody Map<String, Object> payload) {
        try {
            String username = (String) payload.get("username");
            String nombre = (String) payload.get("nombre");
            // El frontend envía "rolUsuario" como string o número
            Long rolId = Long.valueOf(payload.get("rolUsuario").toString());
            String agrupacionesCSV = (String) payload.get("agrupacionesSeleccionadas");

            // Obtener datos desde el sistema externo (incluye clave)
            UsuarioSpringDTO usuarioSpringDTO = springUserService.obtenerUsuarioSpring(username);
            String claveSpring = usuarioSpringDTO.getClave();

            Optional<Usuario> optionalUsuario = usuarioService.getUsuarioPorUsername(username.toUpperCase());
            Usuario usuario;

            List<Long> agrupacionesIds = new ArrayList<>();
            if (agrupacionesCSV != null && !agrupacionesCSV.isEmpty()) {
                agrupacionesIds = Arrays.stream(agrupacionesCSV.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(Long::parseLong)
                        .collect(Collectors.toList());
            }

            if (optionalUsuario.isPresent()) {
                usuario = optionalUsuario.get();
                usuario.setRolUsuario(usuarioService.getRolPorId(rolId));
                usuario.asignarYEncriptarPassword(claveSpring);
                usuario.setNombre(nombre.toUpperCase());
                usuario.setIsSpringUser(Boolean.TRUE);
                usuario.setIsActive(Boolean.TRUE);
                usuario.setChangedPass(Boolean.FALSE);
                usuario.setAgrupacionSeleccionada(agrupacionService.getAgrupacionPorId(agrupacionesIds.getFirst()));
                usuarioService.save(usuario);

                // Actualiza agrupaciones del usuario
                for (Long idAgrupacion : agrupacionesIds) {
                    detalleGruposUsuarioService.agrupar(usuario, idAgrupacion);
                }

                //LOG GLOBAL----------------------
                String descripcion = String.format("El usuario %s añadió la cuenta (ya existente localmente) de %s. Fecha: %s",
                        usuarioService.getUsuarioLogeado().getNombre(),
                        usuario.getNombre(),
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm"))
                );

                logService.saveDeGlobal(usuarioService.getUsuarioLogeado(), AccionLogGlobal.AGREGA_USUARIO, descripcion);
                //-------------------------------

                return ResponseEntity.ok("Usuario actualizado correctamente");
            } else {
                usuario = new Usuario();
                usuario.setRolUsuario(usuarioService.getRolPorId(rolId));
                usuario.asignarYEncriptarPassword(claveSpring);
                usuario.setNombre(nombre.toUpperCase());
                usuario.setUsername(username.toUpperCase());
                usuario.setIsSpringUser(Boolean.TRUE);
                usuario.setIsActive(Boolean.TRUE);
                usuario.setChangedPass(Boolean.FALSE);
                usuario.setAgrupacionSeleccionada(agrupacionService.getAgrupacionPorId(agrupacionesIds.getFirst()));
                usuarioService.save(usuario);

                // Asocia agrupaciones al usuario nuevo
                for (Long idAgrupacion : agrupacionesIds) {
                    detalleGruposUsuarioService.agrupar(usuario, idAgrupacion);
                }

                //LOG GLOBAL----------------------
                String descripcion = String.format("El usuario %s añadió la cuenta (no existente localmente) de %s. Fecha: %s",
                        usuarioService.getUsuarioLogeado().getNombre(),
                        usuario.getNombre(),
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm"))
                );

                logService.saveDeGlobal(usuarioService.getUsuarioLogeado(), AccionLogGlobal.AGREGA_USUARIO, descripcion);
                //-------------------------------

                return ResponseEntity.status(HttpStatus.CREATED).body("Usuario creado correctamente");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear o actualizar el usuario.");
        }
    }


    @PostMapping("/actualizar/{id}")
    public ResponseEntity<String> actualizarUsuario(
            @PathVariable Long id,
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("nombre") String nombre,
            @RequestParam("rol") Long rolId,
            @RequestParam("agrupacionesSeleccionadas") String agrupacionesCSV // <-- recibe el CSV de IDs
    ) {

        try {
            Usuario usuario = new Usuario();
            usuario.setUsername(username.toUpperCase());

            if (password != null && !password.isEmpty()) {
                usuario.setPassword(password);
            }

            String rolAnterior = usuarioService.getUsuarioPorId(id).getRolUsuario().getNombre();
            usuario.setNombre(nombre.toUpperCase());
            usuario.setRolUsuario(usuarioService.getRolPorId(rolId));
            usuario.setIsActive(Boolean.TRUE);

            usuarioService.actualizarUsuario(id, usuario);

            // --- LOGICA DE AGRUPACIONES ---
            // 1. Obtener agrupaciones actuales del usuario
            List<Agrupacion> agrupacionesActuales = detalleGruposUsuarioService.getAgrupacionesPorUsuarioId(id);
            Set<Long> idsActuales = agrupacionesActuales.stream().map(Agrupacion::getId).collect(Collectors.toSet());

            // 2. Obtener agrupaciones nuevas desde el formulario
            Set<Long> idsNuevos = Arrays.stream(agrupacionesCSV.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::parseLong)
                    .collect(Collectors.toSet());

            // 3. Determinar cuáles eliminar, agregar y mantener
            Set<Long> idsEliminar = new HashSet<>(idsActuales);
            idsEliminar.removeAll(idsNuevos); // Los que ya no están

            Set<Long> idsAgregar = new HashSet<>(idsNuevos);
            idsAgregar.removeAll(idsActuales); // Los nuevos

            // Eliminar agrupaciones que ya no están
            for (Long idAgrupacion : idsEliminar) {
                detalleGruposUsuarioService.desagrupar(usuarioService.getUsuarioPorId(id), idAgrupacion);
            }

            // Agregar agrupaciones nuevas
            for (Long idAgrupacion : idsAgregar) {
                detalleGruposUsuarioService.agrupar(usuarioService.getUsuarioPorId(id), idAgrupacion);
            }
            // Las que no cambiaron quedan igual

            //LOG GLOBAL----------------------
            String descripcion = String.format("El usuario %s actualizó la cuenta de %s. Fecha: %s Rol: %s -> %s",
                    usuarioService.getUsuarioLogeado().getNombre(),
                    usuarioService.getUsuarioPorId(id).getNombre(),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm")),
                    rolAnterior,
                    usuarioService.getUsuarioPorId(id).getRolUsuario().getNombre()
            );

            logService.saveDeGlobal(usuarioService.getUsuarioLogeado(), AccionLogGlobal.ACTUALIZA_USUARIO, descripcion);
            //-------------------------------

            return ResponseEntity.ok("Usuario actualizado correctamente");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Nombre de usuario duplicado");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error general al actualizar usuario");
        }
    }


    @PostMapping("/desactivar/{id}")
    public ResponseEntity<String> desactivarUsuario(@PathVariable Long id) {
        try {
            Long idUsuarioLogeado = usuarioService.getIDdeUsuarioLogeado();

            if (!Objects.equals(idUsuarioLogeado, id) && id != 1) {
                usuarioService.desactivarUsuario(id);


                //LOG GLOBAL----------------------
                String descripcion = String.format("El usuario %s desactivó la cuenta de %s. Fecha: %s",
                        usuarioService.getUsuarioLogeado().getNombre(),
                        usuarioService.getUsuarioPorId(id).getNombre(),
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm"))
                );

                logService.saveDeGlobal(usuarioService.getUsuarioLogeado(), AccionLogGlobal.DESACTIVA_USUARIO, descripcion);
                //-------------------------------



                return ResponseEntity.ok("Usuario desactivado correctamente");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No puedes desactivarte a ti mismo o al usuario admin");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al desactivar el usuario");
        }
    }


    @GetMapping("/rol")
    public ResponseEntity<String> obtenerRolUsuario() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Usuario usuario = usuarioService.getUsuarioPorUsername(userDetails.getUsername()).get();

            if (usuario != null && usuario.getRolUsuario().getId() == 2L) {
                return ResponseEntity.ok("soporte");
            }
        }
        return ResponseEntity.ok("otro");
    }
    @GetMapping("/id")
    public ResponseEntity<Long> obtenerIdUsuarioLogeado() {
        Long id = usuarioService.getIDdeUsuarioLogeado();
        return ResponseEntity.ok(id);
    }




}
