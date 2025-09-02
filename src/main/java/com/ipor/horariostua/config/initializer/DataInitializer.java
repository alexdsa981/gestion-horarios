package com.ipor.horariostua.config.initializer;


import com.ipor.horariostua.bloquehorario.agrupacion.Agrupacion;
import com.ipor.horariostua.bloquehorario.agrupacion.AgrupacionRepository;
import com.ipor.horariostua.bloquehorario.colaborador.Colaborador;
import com.ipor.horariostua.bloquehorario.colaborador.ColaboradorRepository;
import com.ipor.horariostua.bloquehorario.horariolaboral.HorarioLaboral;
import com.ipor.horariostua.bloquehorario.horariolaboral.HorarioLaboralRepository;
import com.ipor.horariostua.usuario.caracteristicas.rol.RolUsuario;
import com.ipor.horariostua.bloquehorario.sede.Sede;
import com.ipor.horariostua.usuario.Usuario;
import com.ipor.horariostua.usuario.caracteristicas.rol.RolUsuarioRepository;
import com.ipor.horariostua.bloquehorario.sede.SedeRepository;
import com.ipor.horariostua.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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

        if (horarioLaboralRepository.count() == 0){
            HorarioLaboral horarioLaboral = new HorarioLaboral();
            horarioLaboral.setFechaCreacion(LocalDateTime.now());
            horarioLaboral.setHoraInicio(LocalTime.of(7, 0));
            horarioLaboral.setHoraFin(LocalTime.of(20, 0));
            horarioLaboral.setUsuarioCreador(usuarioRepository.findById(1L).get());

            horarioLaboralRepository.save(horarioLaboral);
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

        if (agrupacionRepository.count() == 0) {
            List<Agrupacion> agrupaciones = new ArrayList<>();

            Agrupacion agrupacion1 = new Agrupacion();
            agrupacion1.setNombre("Rol de Enfermería");
            agrupaciones.add(agrupacion1);

            Agrupacion agrupacion2 = new Agrupacion();
            agrupacion2.setNombre("Rol de Farmacia");
            agrupaciones.add(agrupacion2);

            Agrupacion agrupacion3 = new Agrupacion();
            agrupacion3.setNombre("Imágenes");
            agrupaciones.add(agrupacion3);

            Agrupacion agrupacion4 = new Agrupacion();
            agrupacion4.setNombre("Tecnólogos");
            agrupaciones.add(agrupacion4);

            Agrupacion agrupacion5 = new Agrupacion();
            agrupacion5.setNombre("Radioterapia");
            agrupaciones.add(agrupacion5);

            Agrupacion agrupacion6 = new Agrupacion();
            agrupacion6.setNombre("Psicologia");
            agrupaciones.add(agrupacion6);

            Agrupacion agrupacion7 = new Agrupacion();
            agrupacion7.setNombre("Nutrición");
            agrupaciones.add(agrupacion7);

            Agrupacion agrupacion8 = new Agrupacion();
            agrupacion8.setNombre("Oncologos");
            agrupaciones.add(agrupacion8);

            Agrupacion agrupacion9 = new Agrupacion();
            agrupacion9.setNombre("Dr. Rodriguez");
            agrupaciones.add(agrupacion9);

            Agrupacion agrupacion10 = new Agrupacion();
            agrupacion10.setNombre("Dr. Ponce");
            agrupaciones.add(agrupacion10);

            agrupacionRepository.saveAll(agrupaciones);
        }



        if (colaboradorRepository.count() == 0) {
            Colaborador prueba1 = new Colaborador();
            Colaborador prueba2 = new Colaborador();
            Colaborador prueba3 = new Colaborador();

            prueba1.setNombreCompleto("Rodrigez Zavaleta, Maria Claudia");
            prueba1.setApellidoP("Rodriguez");
            prueba1.setApellidoM("Zavaleta");
            prueba1.setNombre("Maria Claudia");
            prueba1.setEventoColor("#C0C0C0");
            prueba1.setIsActive(Boolean.TRUE);

            prueba2.setNombreCompleto("Ramirez Rosas, Jorge Manuel");
            prueba2.setApellidoP("Ramirez");
            prueba2.setApellidoM("Rosas");
            prueba2.setNombre("Jorge Manuel");
            prueba2.setEventoColor("#FFD700");
            prueba2.setIsActive(Boolean.TRUE);

            prueba3.setNombreCompleto("Rojas Alzamora, Pedro");
            prueba3.setApellidoP("Rojas");
            prueba3.setApellidoM("Alzamora");
            prueba3.setNombre("Pedro");
            prueba3.setEventoColor("#00CED1");
            prueba3.setIsActive(Boolean.TRUE);

            colaboradorRepository.save(prueba1);
            colaboradorRepository.save(prueba2);
            colaboradorRepository.save(prueba3);
        }



    }
}