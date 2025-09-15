package com.ipor.horariostua.config.initializer;


import com.ipor.horariostua.core.bloquehorario.agrupacion.Agrupacion;
import com.ipor.horariostua.core.bloquehorario.agrupacion.AgrupacionRepository;
import com.ipor.horariostua.core.bloquehorario.agrupacion.sedes.DetalleSedeAgrupacion;
import com.ipor.horariostua.core.bloquehorario.agrupacion.sedes.DetalleSedeAgrupacionRepository;
import com.ipor.horariostua.core.bloquehorario.colaborador.ColaboradorRepository;
import com.ipor.horariostua.core.bloquehorario.horariolaboral.HorarioLaboral;
import com.ipor.horariostua.core.bloquehorario.horariolaboral.HorarioLaboralRepository;
import com.ipor.horariostua.core.bloquehorario.agrupacion.usuarios.DetalleGruposUsuario;
import com.ipor.horariostua.core.bloquehorario.agrupacion.usuarios.DetalleGruposUsuarioRepository;
import com.ipor.horariostua.core.usuario.caracteristicas.rol.RolUsuario;
import com.ipor.horariostua.core.bloquehorario.sede.Sede;
import com.ipor.horariostua.core.usuario.Usuario;
import com.ipor.horariostua.core.usuario.caracteristicas.rol.RolUsuarioRepository;
import com.ipor.horariostua.core.bloquehorario.sede.SedeRepository;
import com.ipor.horariostua.core.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    @Autowired
    AgrupacionRepository agrupacionRepository;
    @Autowired
    HorarioLaboralRepository horarioLaboralRepository;
    @Autowired
    DetalleGruposUsuarioRepository detalleGruposUsuarioRepository;
    @Autowired
    DetalleSedeAgrupacionRepository detalleSedeAgrupacionRepository;


    @Override
    public void run(String... args) {


        if (agrupacionRepository.count() == 0) {
            List<String> nombres = Arrays.asList(
                    "Rol de Enfermería", "Rol de Farmacia", "Imágenes", "Tecnólogos",
                    "Radioterapia", "Psicologia", "Nutrición", "Oncologos",
                    "Dr. Rodriguez", "Dr. Ponce"
            );

            List<Agrupacion> agrupaciones = nombres.stream()
                    .map(Agrupacion::new)
                    .collect(Collectors.toList());

            agrupacionRepository.saveAll(agrupaciones);
        }


        if (rolUsuarioRepository.count() == 0) {
            rolUsuarioRepository.save(new RolUsuario("General"));
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
            admin.setAgrupacionSeleccionada(agrupacionRepository.findById(1L).get());
            usuarioRepository.save(admin);

            DetalleGruposUsuario detalleGruposUsuario1 = new DetalleGruposUsuario();
            detalleGruposUsuario1.setUsuario(usuarioRepository.findById(1L).get());
            detalleGruposUsuario1.setAgrupacion(agrupacionRepository.findById(1L).get());
            detalleGruposUsuario1.setIsActive(true);

            DetalleGruposUsuario detalleGruposUsuario2 = new DetalleGruposUsuario();
            detalleGruposUsuario2.setUsuario(usuarioRepository.findById(1L).get());
            detalleGruposUsuario2.setAgrupacion(agrupacionRepository.findById(2L).get());
            detalleGruposUsuario1.setIsActive(true);


            detalleGruposUsuarioRepository.save(detalleGruposUsuario1);
            detalleGruposUsuarioRepository.save(detalleGruposUsuario2);

        }

        if (horarioLaboralRepository.count() == 0){
            HorarioLaboral horarioLaboral = new HorarioLaboral();
            horarioLaboral.setFechaCreacion(LocalDateTime.now());
            horarioLaboral.setHoraInicio(LocalTime.of(7, 0));
            horarioLaboral.setHoraFin(LocalTime.of(20, 0));
            horarioLaboral.setUsuarioCreador(usuarioRepository.findById(1L).get());

            horarioLaboralRepository.save(horarioLaboral);
        }

        if (sedeRepository.count() == 0) {
            List<String> nombres = Arrays.asList(
                    "San Isidro - Clinica", "San Isidro - Administrativo", "Jesús María", "Vesalio"
            );

            List<Sede> sedes = nombres.stream()
                    .map(Sede::new)
                    .collect(Collectors.toList());
            sedeRepository.saveAll(sedes);

            detalleSedeAgrupacionRepository.save(new DetalleSedeAgrupacion(sedeRepository.findById(1L).get(), agrupacionRepository.findById(1L).get()));
            detalleSedeAgrupacionRepository.save(new DetalleSedeAgrupacion(sedeRepository.findById(2L).get(), agrupacionRepository.findById(1L).get()));
            detalleSedeAgrupacionRepository.save(new DetalleSedeAgrupacion(sedeRepository.findById(3L).get(), agrupacionRepository.findById(1L).get()));

            detalleSedeAgrupacionRepository.save(new DetalleSedeAgrupacion(sedeRepository.findById(1L).get(), agrupacionRepository.findById(2L).get()));
            detalleSedeAgrupacionRepository.save(new DetalleSedeAgrupacion(sedeRepository.findById(2L).get(), agrupacionRepository.findById(3L).get()));
            detalleSedeAgrupacionRepository.save(new DetalleSedeAgrupacion(sedeRepository.findById(1L).get(), agrupacionRepository.findById(3L).get()));
        }



    }
}