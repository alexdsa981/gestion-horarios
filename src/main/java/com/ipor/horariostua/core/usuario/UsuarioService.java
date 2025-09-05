//package com.ipor.horariostua.usuario;
//
//
//import org.springframework.beans.factory.annotation.Autowired;
//
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class UsuarioService {
//    @Autowired
//    JwtAuthenticationFilter jwtAuthenticationFilter;
//    @Autowired
//    JwtTokenProvider jwtTokenProvider;
//    @Autowired
//    UsuarioRepository usuarioRepository;
//    @Autowired
//    RolUsuarioRepository rolUsuarioRepository;
//    @Autowired
//    SpringUserService springUserService;
//    @Autowired
//    private AuthenticationManager authenticationManager;
//
//    public Usuario getUsuarioLogeado(){
//        return getUsuarioPorId(getIDdeUsuarioLogeado());
//    }
//
//    public Long getIDdeUsuarioLogeado() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || !authentication.isAuthenticated()) {
//            throw new AuthenticationCredentialsNotFoundException("Usuario no autenticado");
//        }
//
//        String username = authentication.getName(); // o ((UserDetails) authentication.getPrincipal()).getUsername();
//        return usuarioRepository.findByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado")).getId();
//    }
//
//    public Usuario getUsuarioPorId(Long id) {
//        return usuarioRepository.findById(id).get();
//    }
//
//    // Crear un nuevo usuario
//    public Usuario guardarUsuario(Usuario usuario) {
//        return usuarioRepository.save(usuario);
//    }
//
//    // Obtener todos los usuarios
//    public List<Usuario> getListaUsuarios() {
//        return usuarioRepository.findAllByOrderByIsSpringUserAsc();
//    }
//
//    //activos
//    public List<Usuario> getListaUsuariosActivos() {
//        return usuarioRepository.findByIsActiveTrue();
//    }
//
//    //desactivados
//    public List<Usuario> getListaUsuariosDesactivados() {
//        return usuarioRepository.findByIsActiveFalse();
//    }
//
//    // Actualizar un usuario existente
//    public Usuario actualizarUsuario(Long id, Usuario usuarioActualizado) {
//        Optional<Usuario> usuarioExistente = usuarioRepository.findById(id);
//        if (usuarioExistente.isPresent()) {
//            Usuario usuario = usuarioExistente.get();
//            usuario.setUsername(usuarioActualizado.getUsername());
//            usuario.setNombre(usuarioActualizado.getNombre());
//            usuario.setRolUsuario(usuarioActualizado.getRolUsuario());
//            // Encriptar la nueva contraseña si es que se actualiza
//            if (usuarioActualizado.getPassword() != null && !usuarioActualizado.getPassword().isEmpty()) {
//                usuario.asignarYEncriptarPassword(usuarioActualizado.getPassword());
//                usuario.setChangedPass(Boolean.FALSE);
//            }
//            return usuarioRepository.save(usuario);
//        }
//        return null; // O puedes lanzar una excepción personalizada si no existe
//    }
//
//
//    public void desactivarUsuario(Long id) {
//        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
//        if (usuarioOpt.isPresent()) {
//            Usuario usuario = usuarioOpt.get();
//            usuario.setIsActive(false);
//            usuarioRepository.save(usuario);
//        }
//    }
//
//    public void activarUsuario(Long id) {
//        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
//        if (usuarioOpt.isPresent()) {
//            Usuario usuario = usuarioOpt.get();
//            usuario.setIsActive(true);
//            usuarioRepository.save(usuario);
//        }
//    }
//
//    //retorna lista de usuarios por rol
//    public List<Usuario> ListaUsuariosPorRol(Long idRolUsuario) {
//        return usuarioRepository.findByRolUsuarioId(idRolUsuario);
//    }
//
//    //    //retorna todos los roles:
//    public List<RolUsuario> getListaRoles() {
//        return rolUsuarioRepository.findAll();
//    }
//
//    public RolUsuario getRolPorId(Long id) {
//        return rolUsuarioRepository.findById(id).get();
//    }
//
//    public Optional<Usuario> getUsuarioPorUsername(String username) {
//        return usuarioRepository.findByUsername(username);
//    }
//
//    public Boolean existeUsuarioPorUsername(String username) {
//        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
//        if (usuarioOpt.isPresent()) {
//            //System.out.println("Existe en bd Tickets");
//            return true;
//        } else {
//            //System.out.println("No existe en bd tickets");
//            return false;
//        }
//
//    }
//
//
//}
