package com.ipor.horariostua.config.initializer;


import com.ipor.horariostua.core.bloquehorario.agrupacion.Agrupacion;
import com.ipor.horariostua.core.bloquehorario.agrupacion.AgrupacionRepository;
import com.ipor.horariostua.core.bloquehorario.agrupacion.departamento.Departamento;
import com.ipor.horariostua.core.bloquehorario.agrupacion.departamento.DepartamentoRepository;
import com.ipor.horariostua.core.bloquehorario.agrupacion.rangohoras.RangoHorario;
import com.ipor.horariostua.core.bloquehorario.agrupacion.rangohoras.RangoHorarioRepository;
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
    private RolUsuarioRepository rolUsuarioRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ColaboradorRepository colaboradorRepository;
    @Autowired
    private SedeRepository sedeRepository;
    @Autowired
    private DepartamentoRepository departamentoRepository;
    @Autowired
    private AgrupacionRepository agrupacionRepository;
    @Autowired
    private HorarioLaboralRepository horarioLaboralRepository;
    @Autowired
    private DetalleGruposUsuarioRepository detalleGruposUsuarioRepository;
    @Autowired
    private DetalleSedeAgrupacionRepository detalleSedeAgrupacionRepository;
    @Autowired
    private RangoHorarioRepository rangoHorarioRepository;


    @Override
    public void run(String... args) {

        if (sedeRepository.count() == 0) {
            List<String> nombres = Arrays.asList(
                    "San Isidro - Clinica", "San Isidro - Administrativo", "Jesús María", "Vesalio"
            );

            List<Sede> sedes = nombres.stream()
                    .map(Sede::new)
                    .collect(Collectors.toList());
            sedeRepository.saveAll(sedes);

        }


        if (agrupacionRepository.count() == 0) {
            List<String> nombres = Arrays.asList(
                    "Área Clínica", "Área Administrativa", "Externo"
            );

            List<Departamento> departamentos = nombres.stream()
                    .map(Departamento::new)
                    .collect(Collectors.toList());

            departamentoRepository.saveAll(departamentos);
        }


        if (agrupacionRepository.count() == 0) {
            Departamento medico  = departamentoRepository.findById(1L).get();
            List<String> nombresMedico = Arrays.asList(
                    "Rol de Enfermería", "Rol de Farmacia", "Imágenes", "Tecnólogos",
                    "Radioterapia", "Psicologia", "Nutrición", "Oncologos",
                    "Dr. Rodriguez", "Dr. Ponce"
            );

            List<Agrupacion> agrupacionesMedico = nombresMedico.stream()
                    .map(nombre -> new Agrupacion(nombre, medico))
                    .collect(Collectors.toList());
            agrupacionRepository.saveAll(agrupacionesMedico);


            Departamento administrativo  = departamentoRepository.findById(2L).get();
            List<String> nombresAdministrativo = Arrays.asList(
                    "RRHH", "Facturación", "Contabilidad", "Comercial", "Logistica"
            );

            List<Agrupacion> agrupacionesAdministrativo = nombresAdministrativo.stream()
                    .map(nombre -> new Agrupacion(nombre, administrativo))
                    .collect(Collectors.toList());
            agrupacionRepository.saveAll(agrupacionesAdministrativo);



            Departamento externo  = departamentoRepository.findById(3L).get();
            List<String> nombresExterno = Arrays.asList(
                    "TI", "Limpieza"
            );

            List<Agrupacion> agrupacionesExterno = nombresExterno.stream()
                    .map(nombre -> new Agrupacion(nombre, externo))
                    .collect(Collectors.toList());
            agrupacionRepository.saveAll(agrupacionesExterno);

        }

        if (detalleSedeAgrupacionRepository.count() == 0){
            List<Agrupacion> agrupaciones = agrupacionRepository.findAll();
            for (Agrupacion agrupacion : agrupaciones){
                DetalleSedeAgrupacion detalle = new DetalleSedeAgrupacion(sedeRepository.findById(1L).get(), agrupacion);
                detalleSedeAgrupacionRepository.save(detalle);
            }
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
        }

        if (detalleGruposUsuarioRepository.count() == 0){
            List<Agrupacion> agrupaciones = agrupacionRepository.findAll();
            for (Agrupacion agrupacion : agrupaciones){
                DetalleGruposUsuario detalle = new DetalleGruposUsuario(usuarioRepository.findById(1L).get(), agrupacion);
                detalleGruposUsuarioRepository.save(detalle);
            }
        }

        if (horarioLaboralRepository.count() == 0){
            HorarioLaboral horarioLaboral = new HorarioLaboral();
            horarioLaboral.setFechaCreacion(LocalDateTime.now());
            horarioLaboral.setHoraInicio(LocalTime.of(7, 0));
            horarioLaboral.setHoraFin(LocalTime.of(20, 0));
            horarioLaboral.setUsuarioCreador(usuarioRepository.findById(1L).get());

            horarioLaboralRepository.save(horarioLaboral);
        }

        if(rangoHorarioRepository.count() == 0){
            List<Agrupacion> agrupaciones = agrupacionRepository.findAll();
            for (Agrupacion agrupacion : agrupaciones){
                RangoHorario rangoHorario = new RangoHorario(agrupacion, 7, 20);
                rangoHorarioRepository.save(rangoHorario);
            }
        }




    }
}