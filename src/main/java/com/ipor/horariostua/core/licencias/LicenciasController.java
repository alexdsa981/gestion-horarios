package com.ipor.horariostua.core.licencias;

import com.ipor.horariostua.core.licencias.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/app/licencias")
public class LicenciasController {

    @Autowired
    private LicenciasService licenciasService;


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
    public ResponseEntity<Void> crearLicencia(@RequestBody CrearLicenciaDTO dto) {
        List<LocalDate> fechas = dto.getFechas().stream()
                .map(LocalDate::parse)
                .collect(Collectors.toList());
        licenciasService.crearLicencia(dto.getColaboradorId(), dto.getMotivoId(), fechas);
        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @PutMapping("/fecha/editar/{idFecha}")
    public void actualizarFechaLicencia(@PathVariable Long idFecha, @RequestBody ActualizarFechaDTO dto) {
        licenciasService.actualizarFecha(idFecha, dto.getNuevaFecha());
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
    public ResponseEntity<Void> actualizarFechasLicencia(
            @PathVariable Long id,
            @RequestBody List<String> fechas,
            @RequestParam int anio,
            @RequestParam int mes
    ) {
        List<LocalDate> fechasLocalDate = fechas.stream()
                .map(LocalDate::parse)
                .collect(Collectors.toList());
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
            String motivo = licencia.getTipoLicencia().getNombre(); // Aquí obtienes el motivo

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