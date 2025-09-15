package com.ipor.horariostua.core.exportacion;

import com.ipor.horariostua.core.bloquehorario.BloqueHorario;
import com.ipor.horariostua.core.bloquehorario.BloqueHorarioService;
import com.ipor.horariostua.core.bloquehorario.bloquehorarioDTO.Export_BH_DTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/app/exportar")
public class ExportarController {

    @Autowired
    BloqueHorarioService bloqueHorarioService;
    @Autowired
    ExportarService exportarService;

    @GetMapping("/horarios")
    public ResponseEntity<byte[]> exportarHorarios(
            @RequestParam("desde") LocalDate fechaDesde,
            @RequestParam("hasta") LocalDate fechaHasta
    ) {
        System.out.println("Entrando al endpoint /horarios.");
        List<BloqueHorario> lista_bh = bloqueHorarioService.listarPorFecha(fechaDesde, fechaHasta);
        System.out.println("Bloques obtenidos: " + (lista_bh != null ? lista_bh.size() : "null"));

        List<Export_BH_DTO> lista_export = new ArrayList<>();
        for (BloqueHorario bloqueHorario : lista_bh){
            Export_BH_DTO export_bh = new Export_BH_DTO(bloqueHorario);
            lista_export.add(export_bh);
        }
        System.out.println("Export DTOs generados: " + lista_export.size());

        int año = fechaDesde.getYear();
        int mes = fechaDesde.getMonthValue();
        System.out.println("Año: " + año + ", Mes: " + mes);

        try {
            byte[] excelBytes = exportarService.generarExcel(lista_export, año, mes);
            System.out.println("Excel generado. Bytes: " + (excelBytes != null ? excelBytes.length : "null"));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Horarios.xlsx");

            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println("ERROR al generar el Excel:");
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}