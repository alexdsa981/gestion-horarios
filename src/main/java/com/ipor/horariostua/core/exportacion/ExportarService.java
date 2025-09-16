package com.ipor.horariostua.core.exportacion;

import com.ipor.horariostua.core.bloquehorario.bloquehorarioDTO.Export_BH_DTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.YearMonth;
import java.util.*;

@Service
public class ExportarService {

    public byte[] generarExcel(List<Export_BH_DTO> listaExport, int anio, int mes) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Horarios");

        // ---- Estilos ----
        // Header IPRESS (amarillo suave)
        XSSFCellStyle styleHeaderIpress = workbook.createCellStyle();
        XSSFColor colorHeaderIpress = new XSSFColor(new java.awt.Color(255, 242, 204), null);
        styleHeaderIpress.setFillForegroundColor(colorHeaderIpress);
        styleHeaderIpress.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleHeaderIpress.setAlignment(HorizontalAlignment.CENTER);
        styleHeaderIpress.setVerticalAlignment(VerticalAlignment.CENTER);
        Font fontHeaderBold = workbook.createFont();
        fontHeaderBold.setBold(true);
        styleHeaderIpress.setFont(fontHeaderBold);
        styleHeaderIpress.setBorderTop(BorderStyle.THIN);
        styleHeaderIpress.setBorderBottom(BorderStyle.THIN);
        styleHeaderIpress.setBorderLeft(BorderStyle.THIN);
        styleHeaderIpress.setBorderRight(BorderStyle.THIN);

        // Subheader IPRESS / Nombre (amarillo más suave)
        XSSFCellStyle styleSubHeaderIpress = workbook.createCellStyle();
        XSSFColor colorSubHeaderIpress = new XSSFColor(new java.awt.Color(255, 255, 204), null);
        styleSubHeaderIpress.setFillForegroundColor(colorSubHeaderIpress);
        styleSubHeaderIpress.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleSubHeaderIpress.setAlignment(HorizontalAlignment.CENTER);
        styleSubHeaderIpress.setVerticalAlignment(VerticalAlignment.CENTER);
        styleSubHeaderIpress.setFont(fontHeaderBold);
        styleSubHeaderIpress.setBorderTop(BorderStyle.THIN);
        styleSubHeaderIpress.setBorderBottom(BorderStyle.THIN);
        styleSubHeaderIpress.setBorderLeft(BorderStyle.THIN);
        styleSubHeaderIpress.setBorderRight(BorderStyle.THIN);

        // Header Tipo Documento / Número Documento (naranja suave)
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

        // Subheader Documento / Número (naranja muy claro)
        XSSFCellStyle styleSubHeaderDoc = workbook.createCellStyle();
        XSSFColor colorSubHeaderDoc = new XSSFColor(new java.awt.Color(255, 242, 204), null);
        styleSubHeaderDoc.setFillForegroundColor(colorSubHeaderDoc);
        styleSubHeaderDoc.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleSubHeaderDoc.setAlignment(HorizontalAlignment.CENTER);
        styleSubHeaderDoc.setVerticalAlignment(VerticalAlignment.CENTER);
        styleSubHeaderDoc.setFont(fontHeaderBold);
        styleSubHeaderDoc.setBorderTop(BorderStyle.THIN);
        styleSubHeaderDoc.setBorderBottom(BorderStyle.THIN);
        styleSubHeaderDoc.setBorderLeft(BorderStyle.THIN);
        styleSubHeaderDoc.setBorderRight(BorderStyle.THIN);

        // Header días (azul suave)
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

        // Subheader días ingreso/salida (gris muy claro)
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

        // Celda general con borde
        XSSFCellStyle styleCell = workbook.createCellStyle();
        styleCell.setBorderTop(BorderStyle.THIN);
        styleCell.setBorderBottom(BorderStyle.THIN);
        styleCell.setBorderLeft(BorderStyle.THIN);
        styleCell.setBorderRight(BorderStyle.THIN);

        // Número de días del mes
        YearMonth yearMonth = YearMonth.of(anio, mes);
        int diasMes = yearMonth.lengthOfMonth();

        // ---- Fila 0: Headers principales con merges ----
        Row headerRow = sheet.createRow(0);
        headerRow.setHeightInPoints(24);
        int col = 0;

        // NUEVO: Colaborador y Agrupación
        headerRow.createCell(col).setCellValue("Colaborador");
        headerRow.getCell(col).setCellStyle(styleHeaderIpress);
        col++;

        headerRow.createCell(col).setCellValue("Agrupación");
        headerRow.getCell(col).setCellStyle(styleHeaderIpress);
        col++;

        // IPRESS: columnas siguientes
        headerRow.createCell(col).setCellValue("IPRESS");
        headerRow.getCell(col).setCellStyle(styleHeaderIpress);
        headerRow.createCell(col + 1).setCellStyle(styleHeaderIpress);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, col, col + 1));
        col += 2;

        // Tipo Documento / Número Documento: columnas 4 y 5
        headerRow.createCell(col).setCellValue("Tipo Documento");
        headerRow.getCell(col).setCellStyle(styleHeaderDoc);
        col++;
        headerRow.createCell(col).setCellValue("Numero Documento");
        headerRow.getCell(col).setCellStyle(styleHeaderDoc);
        col++;

        int inicioDiasCol = col;
        // Días: cada uno abarca dos columnas con color azul
        for (int i = 1; i <= diasMes; i++) {
            headerRow.createCell(inicioDiasCol + (i - 1) * 2).setCellValue("Día " + i);
            headerRow.getCell(inicioDiasCol + (i - 1) * 2).setCellStyle(styleHeaderDia);
            headerRow.createCell(inicioDiasCol + (i - 1) * 2 + 1).setCellStyle(styleHeaderDia);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, inicioDiasCol + (i - 1) * 2, inicioDiasCol + (i - 1) * 2 + 1));
        }

        // ---- Fila 1: Subheaders ----
        Row subHeaderRow = sheet.createRow(1);
        subHeaderRow.setHeightInPoints(36); // Más altura para subheaders

        col = 0;
        subHeaderRow.createCell(col).setCellValue("Nombre Colaborador");
        subHeaderRow.getCell(col).setCellStyle(styleSubHeaderIpress);
        col++;
        subHeaderRow.createCell(col).setCellValue("Nombre Agrupación");
        subHeaderRow.getCell(col).setCellStyle(styleSubHeaderIpress);
        col++;
        subHeaderRow.createCell(col).setCellValue("Código Único");
        subHeaderRow.getCell(col).setCellStyle(styleSubHeaderIpress);
        col++;
        subHeaderRow.createCell(col).setCellValue("Nombre IPRESS");
        subHeaderRow.getCell(col).setCellStyle(styleSubHeaderIpress);
        col++;
        subHeaderRow.createCell(col).setCellValue("Tipo Documento");
        subHeaderRow.getCell(col).setCellStyle(styleSubHeaderDoc);
        col++;
        subHeaderRow.createCell(col).setCellValue("Numero Documento");
        subHeaderRow.getCell(col).setCellStyle(styleSubHeaderDoc);

        // Subheaders Ingreso/Salida por cada día
        for (int i = 1; i <= diasMes; i++) {
            subHeaderRow.createCell(inicioDiasCol + (i - 1) * 2).setCellValue("Ingreso");
            subHeaderRow.getCell(inicioDiasCol + (i - 1) * 2).setCellStyle(styleSubHeaderDia);
            subHeaderRow.createCell(inicioDiasCol + (i - 1) * 2 + 1).setCellValue("Salida");
            subHeaderRow.getCell(inicioDiasCol + (i - 1) * 2 + 1).setCellStyle(styleSubHeaderDia);
        }

        // ---- Fila 2+: Datos ----
        // clave = colaborador + agrupación para evitar duplicados
        Map<String, Map<Integer, Export_BH_DTO>> map = new LinkedHashMap<>();
        for (Export_BH_DTO dto : listaExport) {
            String clave = dto.getNombreColaborador() + "|" + dto.getNombreAgrupacion();
            int dia = dto.getFecha().getDayOfMonth();
            map.computeIfAbsent(clave, k -> new HashMap<>()).put(dia, dto);
        }

        int rowIdx = 2;
        for (Map.Entry<String, Map<Integer, Export_BH_DTO>> entry : map.entrySet()) {
            Row row = sheet.createRow(rowIdx++);
            String[] datosClave = entry.getKey().split("\\|");
            int colIdx = 0;

            // Nombre Colaborador
            row.createCell(colIdx).setCellValue(datosClave[0]);
            row.getCell(colIdx++).setCellStyle(styleCell);

            // Nombre Agrupación
            row.createCell(colIdx).setCellValue(datosClave.length > 1 ? datosClave[1] : "");
            row.getCell(colIdx++).setCellStyle(styleCell);

            // Código Único
            row.createCell(colIdx).setCellValue("00008389");
            row.getCell(colIdx++).setCellStyle(styleCell);

            // Nombre IPRESS
            row.createCell(colIdx).setCellValue("IPOR: INSTITUTO PERUANO DE ONCOLOGIA & RADIOTERAPIA");
            row.getCell(colIdx++).setCellStyle(styleCell);

            // Tipo Documento
            row.createCell(colIdx).setCellValue("DNI");
            row.getCell(colIdx++).setCellStyle(styleCell);

            // Número Documento
            row.createCell(colIdx).setCellValue("12345678"); // Ejemplo
            row.getCell(colIdx++).setCellStyle(styleCell);

            Map<Integer, Export_BH_DTO> horariosPorDia = entry.getValue();
            for (int d = 1; d <= diasMes; d++) {
                Export_BH_DTO dtoDia = horariosPorDia.get(d);
                row.createCell(inicioDiasCol + (d - 1) * 2)
                        .setCellValue(dtoDia != null && dtoDia.getHoraInicio() != null ? dtoDia.getHoraInicio().toString() : "");
                row.getCell(inicioDiasCol + (d - 1) * 2).setCellStyle(styleCell);
                row.createCell(inicioDiasCol + (d - 1) * 2 + 1)
                        .setCellValue(dtoDia != null && dtoDia.getHoraFin() != null ? dtoDia.getHoraFin().toString() : "");
                row.getCell(inicioDiasCol + (d - 1) * 2 + 1).setCellStyle(styleCell);
            }
        }

        // ---- Filtros solo en subheaders ----
        int lastCol = inicioDiasCol + diasMes * 2 - 1;
        sheet.setAutoFilter(new CellRangeAddress(1, rowIdx - 1, 0, lastCol));

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