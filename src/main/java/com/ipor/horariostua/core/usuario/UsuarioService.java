package com.ipor.horariostua.core.usuario;


import com.ipor.horariostua.config.security.JwtAuthenticationFilter;
import com.ipor.horariostua.config.security.JwtTokenProvider;
import com.ipor.horariostua.core.usuario.caracteristicas.rol.RolUsuario;
import com.ipor.horariostua.core.usuario.caracteristicas.rol.RolUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RolUsuarioRepository rolUsuarioRepository;
    @Autowired
    private AuthenticationManager authenticationManager;

    public Usuario getUsuarioLogeado(){
        return getUsuarioPorId(getIDdeUsuarioLogeado());
    }

    public Long getIDdeUsuarioLogeado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException("Usuario no autenticado");
        }

        String username = authentication.getName(); // o ((UserDetails) authentication.getPrincipal()).getUsername();
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado")).getId();
    }

    public Usuario getUsuarioPorId(Long id) {
        return usuarioRepository.findById(id).get();
    }

    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    //activos
    public List<Usuario> getListaUsuariosActivos() {
        return usuarioRepository.findByIsActiveTrue();
    }

    //desactivados
    public List<Usuario> getListaUsuariosDesactivados() {
        return usuarioRepository.findByIsActiveFalse();
    }

    // Actualizar un usuario existente
    public Usuario actualizarUsuario(Long id, Usuario usuarioActualizado) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findById(id);
        if (usuarioExistente.isPresent()) {
            Usuario usuario = usuarioExistente.get();
            usuario.setUsername(usuarioActualizado.getUsername());
            usuario.setNombre(usuarioActualizado.getNombre());
            usuario.setRolUsuario(usuarioActualizado.getRolUsuario());
            // Encriptar la nueva contraseña si es que se actualiza
            if (usuarioActualizado.getPassword() != null && !usuarioActualizado.getPassword().isEmpty()) {
                usuario.asignarYEncriptarPassword(usuarioActualizado.getPassword());
                usuario.setChangedPass(Boolean.FALSE);
            }
            return usuarioRepository.save(usuario);
        }
        return null; // O puedes lanzar una excepción personalizada si no existe
    }


    public void desactivarUsuario(Long id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setIsActive(false);
            usuarioRepository.save(usuario);
        }
    }

    public void activarUsuario(Long id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setIsActive(true);
            usuarioRepository.save(usuario);
        }
    }

    //retorna lista de usuarios por rol
    public List<Usuario> ListaUsuariosPorRol(Long idRolUsuario) {
        return usuarioRepository.findByRolUsuarioId(idRolUsuario);
    }

    public List<RolUsuario> getListaRoles() {
        return rolUsuarioRepository.findAll();
    }

    public RolUsuario getRolPorId(Long id) {
        return rolUsuarioRepository.findById(id).get();
    }

    public Optional<Usuario> getUsuarioPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }


}
