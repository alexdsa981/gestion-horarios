package com.ipor.horariostua.core.licencias;

import com.ipor.horariostua.core.bloquehorario.agrupacion.colaboradores.DetalleColaboradorAgrupacion;
import com.ipor.horariostua.core.bloquehorario.agrupacion.colaboradores.DetalleColaboradorAgrupacionService;
import com.ipor.horariostua.core.bloquehorario.colaborador.Colaborador;
import com.ipor.horariostua.core.bloquehorario.colaborador.ColaboradorService;

import com.ipor.horariostua.core.licencias.dto.LicenciaTablaDTO;
import com.ipor.horariostua.core.licencias.dto.FechaLicenciaVistaDTO;
import com.ipor.horariostua.core.licencias.tipo.TipoLicencia;
import com.ipor.horariostua.core.licencias.tipo.TipoLicenciaService;
import com.ipor.horariostua.core.usuario.Usuario;
import com.ipor.horariostua.core.usuario.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LicenciasService {
    @Autowired
    private LicenciasRepository licenciasRepository;
    @Autowired
    private TipoLicenciaService tipoLicenciaService;
    @Autowired
    private ColaboradorService colaboradorService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private DetalleColaboradorAgrupacionService detalleColaboradorAgrupacionService;
    @Autowired
    private LicenciaFechaRepository licenciaFechaRepository;

    public Licencias getPorId(Long id) {
        return licenciasRepository.findById(id).get();
    }

    @Transactional
    public Licencias eliminarFechasDeMes(Long id, int anio, int mes) {
        Licencias lic = licenciasRepository.findById(id).orElse(null);
        if (lic != null && lic.getFechas() != null) {
            int mesJava = mes + 1;
            List<LicenciaFecha> fechasAEliminar = lic.getFechas().stream()
                    .filter(f -> f.getFecha().getYear() == anio && f.getFecha().getMonthValue() == mesJava)
                    .collect(Collectors.toList());

            for (LicenciaFecha fecha : fechasAEliminar) {
                licenciaFechaRepository.delete(fecha);
            }
            lic.getFechas().removeAll(fechasAEliminar);

            if (lic.getFechas().isEmpty()) {
                lic.setIsActive(false);
            }
            licenciasRepository.save(lic);
        }
        return lic;
    }

    public Licencias crearLicencia(Long colaboradorId, Long motivoId, List<LocalDate> fechas) {
        Colaborador colaborador = colaboradorService.getColaboradorPorId(colaboradorId);
        TipoLicencia tipoLicencia = tipoLicenciaService.findById(motivoId);
        Usuario usuario = usuarioService.getUsuarioLogeado();

        Licencias licencia = new Licencias();
        licencia.setColaborador(colaborador);
        licencia.setTipoLicencia(tipoLicencia);
        licencia.setUsuario(usuario);
        licencia.setAgrupacion(usuario.getAgrupacionSeleccionada());
        licencia.setIsActive(true);

        List<LicenciaFecha> fechaList = new ArrayList<>();
        for (LocalDate fecha : fechas) {
            LicenciaFecha lf = new LicenciaFecha();
            lf.setLicencia(licencia);
            lf.setFecha(fecha);
            fechaList.add(lf);
        }
        licencia.setFechas(fechaList);
        return licenciasRepository.save(licencia);
    }


    public List<FechaLicenciaVistaDTO> listarActivasPorAgrupacionYMesDTOAVista(Long agrupacionId, int anio, int mes) {
        List<Licencias> licencias = licenciasRepository.findByIsActiveTrueAndAgrupacionId(agrupacionId);
        List<FechaLicenciaVistaDTO> fechaLicenciasDTO = new ArrayList<>();

        int mesJava = mes + 1;
        YearMonth ym = YearMonth.of(anio, mesJava);

        // Primer y último día del mes seleccionado
        LocalDate primerDiaDelMes = ym.atDay(1);
        LocalDate ultimoDiaDelMes = ym.atEndOfMonth();

        // Ampliar rango: 7 días antes y después
        LocalDate inicioRango = primerDiaDelMes.minusDays(7);
        LocalDate finRango = ultimoDiaDelMes.plusDays(7);

        for (Licencias licencia : licencias) {
            DetalleColaboradorAgrupacion detalle = detalleColaboradorAgrupacionService.getDetallePorColaboradorYAgrupacion(
                    licencia.getColaborador().getId(), agrupacionId
            );

            for (LicenciaFecha licenciaFecha : licencia.getFechas()) {
                LocalDate fecha = licenciaFecha.getFecha();
                if (!fecha.isBefore(inicioRango) && !fecha.isAfter(finRango)) {
                    FechaLicenciaVistaDTO dto = new FechaLicenciaVistaDTO(licencia, licenciaFecha, detalle.getEventoColor());
                    fechaLicenciasDTO.add(dto);
                }
            }
        }
        return fechaLicenciasDTO;
    }

    public List<LicenciaTablaDTO> listarActivasPorAgrupacionYMesDTO(Long agrupacionId, int anio, int mes) {
        List<Licencias> licencias = licenciasRepository.findByIsActiveTrueAndAgrupacionId(agrupacionId);
        List<LicenciaTablaDTO> licenciasDTO = new ArrayList<>();

        int mesJava = mes + 1;

        for (Licencias licencia : licencias){
            long diasFiltrados = licencia.getFechas().stream()
                    .map(LicenciaFecha::getFecha)
                    .filter(f -> f.getYear() == anio && f.getMonthValue() == mesJava)
                    .count();

            if (diasFiltrados > 0) {
                LicenciaTablaDTO dto = new LicenciaTablaDTO(licencia, (int)diasFiltrados);
                licenciasDTO.add(dto);
            }
        }
        return licenciasDTO;
    }


    public void actualizarFechasLicencia(Long licenciaId, List<LocalDate> nuevasFechas, int anio, int mes) {
        Licencias licencia = licenciasRepository.findById(licenciaId).orElse(null);
        if (licencia == null) return;

        int mesJava = mes + 1;

        // Elimina solo las fechas del mes/año seleccionado
        List<LicenciaFecha> restantes = licencia.getFechas().stream()
                .filter(lf -> !(lf.getFecha().getYear() == anio && lf.getFecha().getMonthValue() == mesJava))
                .toList();
        licencia.getFechas().clear();
        licencia.getFechas().addAll(restantes);

        // Agrega las nuevas (pueden ser de cualquier mes/año)
        List<LicenciaFecha> nuevasFechasList = nuevasFechas.stream().map(fecha -> {
            LicenciaFecha lf = new LicenciaFecha();
            lf.setLicencia(licencia);
            lf.setFecha(fecha);
            return lf;
        }).toList();
        licencia.getFechas().addAll(nuevasFechasList);

        licenciasRepository.save(licencia);
    }

    public void eliminarPorId(Long idFecha) {
        licenciaFechaRepository.deleteById(idFecha);
    }

    public void actualizarFecha(Long idFecha, String nuevaFechaStr) {
        LicenciaFecha licenciaFecha = licenciaFechaRepository.findById(idFecha)
                .orElseThrow(() -> new RuntimeException("No existe la fecha"));
        licenciaFecha.setFecha(LocalDate.parse(nuevaFechaStr));
        licenciaFechaRepository.save(licenciaFecha);
    }

    public List<Licencias> LicenciasColaboradorAgrupacion(Long colaboradorId, Long agrupacionId){
        return licenciasRepository.findByColaboradorIdAndAgrupacionIdAndIsActiveTrue(colaboradorId, agrupacionId);
    }

}