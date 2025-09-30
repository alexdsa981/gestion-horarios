package com.ipor.horariostua.core.exportacion;

import com.ipor.horariostua.core.bloquehorario.bloquehorarioDTO.Export_BH_DTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.*;

@Service
public class ExportarService {

    public byte[] generarExcel(List<Export_BH_DTO> listaExport, int anio, int mes, String nombreSede) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Horarios");

        // ---- Estilos ----
        Font fontHeaderBold = workbook.createFont();
        fontHeaderBold.setBold(true);

        XSSFCellStyle styleHeaderIpress = workbook.createCellStyle();
        XSSFColor colorHeaderIpress = new XSSFColor(new java.awt.Color(255, 242, 204), null);
        styleHeaderIpress.setFillForegroundColor(colorHeaderIpress);
        styleHeaderIpress.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleHeaderIpress.setAlignment(HorizontalAlignment.CENTER);
        styleHeaderIpress.setVerticalAlignment(VerticalAlignment.CENTER);
        styleHeaderIpress.setFont(fontHeaderBold);
        styleHeaderIpress.setBorderTop(BorderStyle.THIN);
        styleHeaderIpress.setBorderBottom(BorderStyle.THIN);
        styleHeaderIpress.setBorderLeft(BorderStyle.THIN);
        styleHeaderIpress.setBorderRight(BorderStyle.THIN);

        XSSFCellStyle styleHeaderDoc = workbook.createCellStyle();
        XSSFColor colorHeaderDoc = new XSSFColor(new java.awt.Color(255, 217, 102), null);
        styleHeaderDoc.setFillForegroundColor(colorHeaderDoc);
        styleHeaderDoc.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleHeaderDoc.setAlignment(HorizontalAlignment.CENTER);
        styleHeaderDoc.setVerticalAlignment(VerticalAlignment.CENTER);
        styleHeaderDoc.setFont(fontHeaderBold);
        styleHeaderDoc.setBorderTop(BorderStyle.THIN);
        styleHeaderDoc.setBorderBottom(BorderStyle.THIN);
        styleHeaderDoc.setBorderLeft(BorderStyle.THIN);
        styleHeaderDoc.setBorderRight(BorderStyle.THIN);

        XSSFCellStyle styleHeaderDia = workbook.createCellStyle();
        XSSFColor colorHeaderDia = new XSSFColor(new java.awt.Color(184, 204, 228), null);
        styleHeaderDia.setFillForegroundColor(colorHeaderDia);
        styleHeaderDia.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleHeaderDia.setAlignment(HorizontalAlignment.CENTER);
        styleHeaderDia.setVerticalAlignment(VerticalAlignment.CENTER);
        styleHeaderDia.setFont(fontHeaderBold);
        styleHeaderDia.setBorderTop(BorderStyle.THIN);
        styleHeaderDia.setBorderBottom(BorderStyle.THIN);
        styleHeaderDia.setBorderLeft(BorderStyle.THIN);
        styleHeaderDia.setBorderRight(BorderStyle.THIN);

        XSSFCellStyle styleSubHeaderDia = workbook.createCellStyle();
        XSSFColor colorSubHeaderDia = new XSSFColor(new java.awt.Color(242, 242, 242), null);
        styleSubHeaderDia.setFillForegroundColor(colorSubHeaderDia);
        styleSubHeaderDia.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleSubHeaderDia.setAlignment(HorizontalAlignment.CENTER);
        styleSubHeaderDia.setVerticalAlignment(VerticalAlignment.CENTER);
        styleSubHeaderDia.setFont(fontHeaderBold);
        styleSubHeaderDia.setBorderTop(BorderStyle.THIN);
        styleSubHeaderDia.setBorderBottom(BorderStyle.THIN);
        styleSubHeaderDia.setBorderLeft(BorderStyle.THIN);
        styleSubHeaderDia.setBorderRight(BorderStyle.THIN);

        XSSFCellStyle styleSubHeaderGeneral = workbook.createCellStyle();
        XSSFColor colorSubHeaderGeneral = new XSSFColor(new java.awt.Color(255, 255, 204), null);
        styleSubHeaderGeneral.setFillForegroundColor(colorSubHeaderGeneral);
        styleSubHeaderGeneral.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleSubHeaderGeneral.setAlignment(HorizontalAlignment.CENTER);
        styleSubHeaderGeneral.setVerticalAlignment(VerticalAlignment.CENTER);
        styleSubHeaderGeneral.setFont(fontHeaderBold);
        styleSubHeaderGeneral.setBorderTop(BorderStyle.THIN);
        styleSubHeaderGeneral.setBorderBottom(BorderStyle.THIN);
        styleSubHeaderGeneral.setBorderLeft(BorderStyle.THIN);
        styleSubHeaderGeneral.setBorderRight(BorderStyle.THIN);

        XSSFCellStyle styleCell = workbook.createCellStyle();
        styleCell.setBorderTop(BorderStyle.THIN);
        styleCell.setBorderBottom(BorderStyle.THIN);
        styleCell.setBorderLeft(BorderStyle.THIN);
        styleCell.setBorderRight(BorderStyle.THIN);
        styleCell.setAlignment(HorizontalAlignment.CENTER);
        styleCell.setVerticalAlignment(VerticalAlignment.CENTER);

        // Estilo rojo para turnos largos (>12h)
        XSSFCellStyle styleCellRed = workbook.createCellStyle();
        XSSFColor colorRed = new XSSFColor(new java.awt.Color(255, 199, 206), null); // rojo suave
        styleCellRed.setFillForegroundColor(colorRed);
        styleCellRed.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleCellRed.setBorderTop(BorderStyle.THIN);
        styleCellRed.setBorderBottom(BorderStyle.THIN);
        styleCellRed.setBorderLeft(BorderStyle.THIN);
        styleCellRed.setBorderRight(BorderStyle.THIN);
        styleCellRed.setAlignment(HorizontalAlignment.CENTER);
        styleCellRed.setVerticalAlignment(VerticalAlignment.CENTER);

        // Número de días del mes
        YearMonth yearMonth = YearMonth.of(anio, mes);
        int diasMes = yearMonth.lengthOfMonth();

        // ---- Fila 0: Encabezados principales con merges ----
        Row headerRow = sheet.createRow(0);
        headerRow.setHeightInPoints(24);
        int col = 0;

        // IPRESS
        headerRow.createCell(col).setCellValue("IPRESS");
        headerRow.getCell(col).setCellStyle(styleHeaderIpress);
        sheet.addMergedRegion(new CellRangeAddress(0, 1, col, col + 1));
        col += 2;

        // Colaborador
        headerRow.createCell(col).setCellValue("Colaborador");
        headerRow.getCell(col).setCellStyle(styleHeaderIpress);
        sheet.addMergedRegion(new CellRangeAddress(0, 1, col, col + 3));
        col+=4;


        int inicioDiasCol = col;

        // Días
        for (int i = 1; i <= diasMes; i++) {
            int c1 = inicioDiasCol + (i - 1) * 2;
            int c2 = c1 + 1;
            headerRow.createCell(c1).setCellValue("Día " + i);
            headerRow.getCell(c1).setCellStyle(styleHeaderDia);
            headerRow.createCell(c2).setCellStyle(styleHeaderDia);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, c1, c2));
        }

        // ---- Fila 1: Fechas bajo los días, merge en cada par ----
        Row fechasRow = sheet.createRow(1);
        fechasRow.setHeightInPoints(18);

        for (int i = 0; i < inicioDiasCol; i++) {
            fechasRow.createCell(i).setCellValue("");
        }
        for (int i = 1; i <= diasMes; i++) {
            int c1 = inicioDiasCol + (i - 1) * 2;
            int c2 = c1 + 1;
            LocalDate fecha = LocalDate.of(anio, mes, i);
            String fechaStr = String.format("%02d/%02d/%02d", fecha.getDayOfMonth(), fecha.getMonthValue(), fecha.getYear() % 100);
            fechasRow.createCell(c1).setCellValue(fechaStr);
            fechasRow.getCell(c1).setCellStyle(styleHeaderDia);
            fechasRow.createCell(c2).setCellStyle(styleHeaderDia);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, c1, c2));
        }

        // ---- Fila 2: Subheaders generales y días ----
        Row subHeaderRow = sheet.createRow(2);
        subHeaderRow.setHeightInPoints(36);

        col = 0;
        Cell cell = subHeaderRow.createCell(col++);
        cell.setCellValue("Código Único");
        cell.setCellStyle(styleSubHeaderGeneral);

        cell = subHeaderRow.createCell(col++);
        cell.setCellValue("Nombre");
        cell.setCellStyle(styleSubHeaderGeneral);

        cell = subHeaderRow.createCell(col++);
        cell.setCellValue("Nombre Colaborador");
        cell.setCellStyle(styleSubHeaderGeneral);

        cell = subHeaderRow.createCell(col++);
        cell.setCellValue("Nombre Agrupación");
        cell.setCellStyle(styleSubHeaderGeneral);

        cell = subHeaderRow.createCell(col++);
        cell.setCellValue("Tipo Documento");
        cell.setCellStyle(styleSubHeaderGeneral);

        cell = subHeaderRow.createCell(col++);
        cell.setCellValue("Numero Documento");
        cell.setCellStyle(styleSubHeaderGeneral);

        for (int i = 1; i <= diasMes; i++) {
            int c1 = inicioDiasCol + (i - 1) * 2;
            int c2 = c1 + 1;
            subHeaderRow.createCell(c1).setCellValue("Ingreso");
            subHeaderRow.getCell(c1).setCellStyle(styleSubHeaderDia);
            subHeaderRow.createCell(c2).setCellValue("Salida");
            subHeaderRow.getCell(c2).setCellStyle(styleSubHeaderDia);
        }

        // ---- Fila 3+: Datos ----
        Map<String, Map<Integer, Export_BH_DTO>> map = new LinkedHashMap<>();
        for (Export_BH_DTO dto : listaExport) {
            String clave = dto.getNombreColaborador() + "|" + dto.getNombreAgrupacion();
            int dia = dto.getFecha().getDayOfMonth();
            map.computeIfAbsent(clave, k -> new HashMap<>()).put(dia, dto);
        }

        int rowIdx = 3;
        for (Map.Entry<String, Map<Integer, Export_BH_DTO>> entry : map.entrySet()) {
            Row row = sheet.createRow(rowIdx++);
            Map<Integer, Export_BH_DTO> horariosPorDia = entry.getValue();
            // Toma cualquier DTO, por ejemplo el primero, para los datos generales del colaborador
            Export_BH_DTO dto = horariosPorDia.values().stream().findFirst().orElse(null);

            int colIdx = 0;

            // Código Único
            row.createCell(colIdx).setCellValue("00008389");
            row.getCell(colIdx++).setCellStyle(styleCell);

            // Nombre IPRESS
            row.createCell(colIdx).setCellValue("IPOR: INSTITUTO PERUANO DE ONCOLOGIA & RADIOTERAPIA");
            row.getCell(colIdx++).setCellStyle(styleCell);

            // Nombre Colaborador
            row.createCell(colIdx).setCellValue(dto != null ? dto.getNombreColaborador() : "");
            row.getCell(colIdx++).setCellStyle(styleCell);

            // Nombre Agrupación
            row.createCell(colIdx).setCellValue(dto != null ? dto.getNombreAgrupacion() : "");
            row.getCell(colIdx++).setCellStyle(styleCell);

            // Tipo Documento
            row.createCell(colIdx).setCellValue(dto != null ? dto.getTipo_documento() : "");
            row.getCell(colIdx++).setCellStyle(styleCell);

            // Número Documento
            row.createCell(colIdx).setCellValue(dto != null ? dto.getNum_documento() : "");
            row.getCell(colIdx++).setCellStyle(styleCell);

            // Horarios por día
            for (int d = 1; d <= diasMes; d++) {
                int c1 = inicioDiasCol + (d - 1) * 2;
                int c2 = c1 + 1;
                Export_BH_DTO dtoDia = horariosPorDia.get(d);

                String horaInicioStr = dtoDia != null && dtoDia.getHoraInicio() != null ? dtoDia.getHoraInicio().toString() : "";
                String horaFinStr = dtoDia != null && dtoDia.getHoraFin() != null ? dtoDia.getHoraFin().toString() : "";

                // Calcula si el turno es de más de 12 horas (cuenta minutos)
                boolean turnoLargo = false;
                if (dtoDia != null && dtoDia.getHoraInicio() != null && dtoDia.getHoraFin() != null) {
                    long diffMinutes = java.time.Duration.between(dtoDia.getHoraInicio(), dtoDia.getHoraFin()).toMinutes();
                    if (diffMinutes < 0) diffMinutes += 24 * 60; // cruza medianoche
                    if (diffMinutes > 12 * 60) turnoLargo = true;
                }

                row.createCell(c1).setCellValue(horaInicioStr);
                row.getCell(c1).setCellStyle(turnoLargo ? styleCellRed : styleCell);

                row.createCell(c2).setCellValue(horaFinStr);
                row.getCell(c2).setCellStyle(turnoLargo ? styleCellRed : styleCell);
            }
        }
        // ---- Filtros solo en subheaders ----
        int lastCol = inicioDiasCol + diasMes * 2 - 1;
        sheet.setAutoFilter(new CellRangeAddress(2, rowIdx - 1, 0, lastCol));

        // ---- Ajustar ancho de columnas ----
        for (int c = 0; c <= lastCol; c++) {
            sheet.autoSizeColumn(c);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();
        return bos.toByteArray();
    }
}