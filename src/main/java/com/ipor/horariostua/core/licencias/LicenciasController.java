package com.ipor.horariostua.core.licencias;

import com.ipor.horariostua.core.licencias.dto.*;
import com.ipor.horariostua.core.usuario.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/app/licencias")
public class LicenciasController {

    @Autowired
    private LicenciasService licenciasService;
    @Autowired
    private UsuarioService usuarioService;

    @ResponseBody
    @GetMapping("/vista")
    public List<FechaLicenciaVistaDTO> listarActivosDTOAVista(
            @RequestParam("agrupacionId") Long agrupacionId,
            @RequestParam("anio") int anio,
            @RequestParam("mes") int mes
    ) {
        return licenciasService.listarActivasPorAgrupacionYMesDTOAVista(agrupacionId, anio, mes);
    }

    @ResponseBody
    @GetMapping("/activos")
    public List<LicenciaTablaDTO> listarActivosDTO(
            @RequestParam("agrupacionId") Long agrupacionId,
            @RequestParam("anio") int anio,
            @RequestParam("mes") int mes
    ) {
        return licenciasService.listarActivasPorAgrupacionYMesDTO(agrupacionId, anio, mes);
    }

    @ResponseBody
    @PostMapping("/{id}/desactivar")
    public Licencias desactivarLicencia(
            @PathVariable Long id,
            @RequestParam("anio") int anio,
            @RequestParam("mes") int mes
    ) {
        return licenciasService.eliminarFechasDeMes(id, anio, mes);
    }


    @ResponseBody
    @DeleteMapping("/fechas/eliminar/{idFecha}")
    public void eliminarLicenciaFecha(@PathVariable Long idFecha) {
        licenciasService.eliminarPorId(idFecha);
    }

    @ResponseBody
    @PostMapping("/crear")
    public ResponseEntity<?> crearLicencia(@RequestBody CrearLicenciaDTO dto) {
        List<LocalDate> fechas = dto.getFechas().stream()
                .map(LocalDate::parse)
                .collect(Collectors.toList());

        Long idRolUsuario = usuarioService.getUsuarioLogeado().getRolUsuario().getId();

        if (idRolUsuario != 2) {
            YearMonth mesActual = YearMonth.from(LocalDate.now());
            for (LocalDate fecha : fechas) {
                YearMonth ym = YearMonth.from(fecha);
                // Permitir solo meses FUTUROS: si ym no es posterior al mes actual, se bloquea
                if (!ym.isAfter(mesActual)) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(Collections.singletonMap("error", "No puedes registrar licencias en el mes actual ni en meses anteriores."));
                }
            }
        }

        licenciasService.crearLicencia(dto.getColaboradorId(), dto.getMotivoId(), fechas);
        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @PutMapping("/fecha/editar/{idFecha}")
    public ResponseEntity<?> actualizarFechaLicencia(@PathVariable Long idFecha, @RequestBody ActualizarFechaDTO dto) {
        Long idRolUsuario = usuarioService.getUsuarioLogeado().getRolUsuario().getId();
        LocalDate nuevaFecha = LocalDate.parse(dto.getNuevaFecha());

        if (idRolUsuario != 2) {
            YearMonth mesActual = YearMonth.from(LocalDate.now());
            YearMonth mesNueva = YearMonth.from(nuevaFecha);
            if (!mesNueva.isAfter(mesActual)) { // mesNueva <= mesActual
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Collections.singletonMap("error", "No puedes mover la fecha al mes actual ni a meses anteriores."));
            }
        }

        licenciasService.actualizarFecha(idFecha, dto.getNuevaFecha());
        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @GetMapping("/{id}/fechas")
    public List<String> getFechasLicencia(@PathVariable Long id,
                                          @RequestParam int anio,
                                          @RequestParam int mes) { // mes: 0 = enero
        return licenciasService.getPorId(id)
                .getFechas()
                .stream()
                .map(LicenciaFecha::getFecha)
                .filter(f -> f.getYear() == anio && f.getMonthValue() == (mes + 1))
                .map(LocalDate::toString)
                .collect(Collectors.toList());
    }


    @ResponseBody
    @PutMapping("/{id}/fechas")
    public ResponseEntity<?> actualizarFechasLicencia(
            @PathVariable Long id,
            @RequestBody List<String> fechas,
            @RequestParam int anio,
            @RequestParam int mes
    ) {
        List<LocalDate> fechasLocalDate = fechas.stream()
                .map(LocalDate::parse)
                .collect(Collectors.toList());

        Long idRolUsuario = usuarioService.getUsuarioLogeado().getRolUsuario().getId();
        if (idRolUsuario != 2) {
            YearMonth mesActual = YearMonth.from(LocalDate.now());
            for (LocalDate fecha : fechasLocalDate) {
                YearMonth ym = YearMonth.from(fecha);
                if (!ym.isAfter(mesActual)) { // ym <= mesActual
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(Collections.singletonMap("error", "No puedes editar licencias en el mes actual ni en meses anteriores."));
                }
            }
        }

        licenciasService.actualizarFechasLicencia(id, fechasLocalDate, anio, mes);
        return ResponseEntity.ok().build();
    }


    @ResponseBody
    @GetMapping("/colaborador/fechas-vacaciones")
    public FechasLicenciaColaboradorDTO obtenerFechasVacacionesColaborador(
            @RequestParam("colaboradorId") Long colaboradorId,
            @RequestParam("agrupacionId") Long agrupacionId,
            @RequestParam("anio") int anio
    ) {
        List<Licencias> licencias = licenciasService.LicenciasColaboradorAgrupacion(colaboradorId, agrupacionId);

        List<FechaMotivoLicenciaDTO> fechas = new ArrayList<>();
        String nombreColaborador = "";

        for (Licencias licencia : licencias) {
            nombreColaborador = licencia.getColaborador().getNombreCompleto();
            String motivo = licencia.getTipoLicencia().getNombre();

            for (LicenciaFecha licenciaFecha : licencia.getFechas()) {
                LocalDate fecha = licenciaFecha.getFecha();
                if (fecha.getYear() == anio) {
                    fechas.add(new FechaMotivoLicenciaDTO(fecha, motivo));
                }
            }
        }

        // Opcional: ordenar por fecha
        fechas.sort(Comparator.comparing(FechaMotivoLicenciaDTO::getFecha));

        return new FechasLicenciaColaboradorDTO(colaboradorId, nombreColaborador, fechas);
    }
}