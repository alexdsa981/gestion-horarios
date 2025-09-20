package com.ipor.horariostua.core.exportacion;

import com.ipor.horariostua.core.bloquehorario.BloqueHorario;
import com.ipor.horariostua.core.bloquehorario.BloqueHorarioService;
import com.ipor.horariostua.core.bloquehorario.bloquehorarioDTO.Export_BH_DTO;
import com.ipor.horariostua.core.bloquehorario.sede.SedeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/app/exportar")
public class ExportarController {

    @Autowired
    private BloqueHorarioService bloqueHorarioService;

    @Autowired
    private ExportarService exportarService;

    @Autowired
    private SedeService sedeService;

    @GetMapping("/horarios")
    public ResponseEntity<byte[]> exportarHorarios(
            @RequestParam("ano") int ano,
            @RequestParam("mes") int mes,
            @RequestParam("sede") Long sedeId
    ) {
        try {
            String nombreSede = sedeService.getSedePorId(sedeId).getNombre();
            List<BloqueHorario> listaBloques = bloqueHorarioService.listarPorAnoMesSede(ano, mes, sedeId);

            List<Export_BH_DTO> listaExport = new ArrayList<>();
            for (BloqueHorario bloqueHorario : listaBloques) {
                listaExport.add(new Export_BH_DTO(bloqueHorario));
            }

            byte[] excelBytes = exportarService.generarExcel(listaExport, ano, mes, nombreSede);

            String[] nombresMes = {"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};
            String nombreMes = (mes >= 1 && mes <= 12) ? nombresMes[mes - 1] : String.valueOf(mes);
            String nombreSedeLimpio = limpiarNombreArchivo(nombreSede);
            String fileName = String.format("%s_%d_%s.xlsx", nombreMes, ano, nombreSedeLimpio);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private static String limpiarNombreArchivo(String texto) {
        String normalizado = Normalizer.normalize(texto, Normalizer.Form.NFD);
        String sinTildes = normalizado.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return sinTildes.replaceAll("[^a-zA-Z0-9]", "");
    }
}