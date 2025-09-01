package com.ipor.horariostua.config.initializer;


import com.ipor.horariostua.colaborador.Colaborador;
import com.ipor.horariostua.colaborador.ColaboradorRepository;
import com.ipor.horariostua.usuario.caracteristicas.rol.RolUsuario;
import com.ipor.horariostua.sede.Sede;
import com.ipor.horariostua.core.model.Usuario;
import com.ipor.horariostua.usuario.caracteristicas.rol.RolUsuarioRepository;
import com.ipor.horariostua.sede.SedeRepository;
import com.ipor.horariostua.core.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

     //
     //CLASIFICADORES
     //

    @Autowired
    RolUsuarioRepository rolUsuarioRepository;
    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    ColaboradorRepository colaboradorRepository;
    @Autowired
    SedeRepository sedeRepository;

    @Override
    public void run(String... args) {

        if (rolUsuarioRepository.count() == 0) {
            rolUsuarioRepository.save(new RolUsuario("General"));
            rolUsuarioRepository.save(new RolUsuario("Supervisor"));
            rolUsuarioRepository.save(new RolUsuario("Admin"));
        }

        if (usuarioRepository.count() == 0) {
            RolUsuario rolAdmin = rolUsuarioRepository.findByNombre("Admin");
            Usuario admin = new Usuario();
            admin.setNombre("ADMINISTRADOR");
            admin.setUsername("ADMIN");
            admin.setPassword("$2a$12$7SW6dd16qcrYSdV0L4Uzp.qzCEe6ricYOH9fdr1r/bGlF2ItBun4a");
            admin.setRolUsuario(rolAdmin);
            admin.setIsActive(true);
            admin.setChangedPass(false);
            admin.setIsSpringUser(false);
            usuarioRepository.save(admin);
        }

        if (colaboradorRepository.count() == 0) {
            Colaborador prueba1 = new Colaborador();
            Colaborador prueba2 = new Colaborador();
            Colaborador prueba3 = new Colaborador();

            prueba1.setNombreCompleto("PruebaP1 PruebaM1 PruebaN1");
            prueba1.setEventoColor("#C0C0C0");
            prueba1.setIsActive(Boolean.TRUE);
            prueba2.setNombreCompleto("PruebaP2 PruebaM2 PruebaN2");
            prueba2.setEventoColor("#FFD700");
            prueba2.setIsActive(Boolean.TRUE);
            prueba3.setNombreCompleto("PruebaP3 PruebaM3 PruebaN3");
            prueba3.setEventoColor("#00CED1");
            prueba3.setIsActive(Boolean.TRUE);

            colaboradorRepository.save(prueba1);
            colaboradorRepository.save(prueba2);
            colaboradorRepository.save(prueba3);
        }

        if (sedeRepository.count() == 0) {
            Sede sede1 = new Sede();
            sede1.setNombre("San Isidro");
            sede1.setIsActive(Boolean.TRUE);
            Sede sede2 = new Sede();
            sede2.setNombre("Jesús María");
            sede2.setIsActive(Boolean.TRUE);

            sedeRepository.save(sede1);
            sedeRepository.save(sede2);
        }


    }
}