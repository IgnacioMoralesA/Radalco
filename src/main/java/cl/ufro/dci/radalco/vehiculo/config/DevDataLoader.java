/*package cl.ufro.dci.radalco.vehiculo.config;

import cl.ufro.dci.radalco.vehiculo.DTO.VehiculoDTO;
import cl.ufro.dci.radalco.vehiculo.model.Vehiculo;
import cl.ufro.dci.radalco.vehiculo.DTO.VehiculoMapper;
import cl.ufro.dci.radalco.vehiculo.repository.VehiculoRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class DevDataLoader implements CommandLineRunner {

    @Autowired
    private VehiculoMapper vehiculoMapper;

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Value("C:/Users/ignac/1er Semestre 2025/Radalco/PERMISOCIRCULACIÖN.xlsx")
    private String excelFilePath;

    @Override
    public void run(String... args) throws Exception {
        List<VehiculoDTO> vehiculosDTO = loadData(excelFilePath);
        List<Vehiculo> vehiculos = vehiculosDTO.stream()
                .map(vehiculoMapper::toEntity)
                .toList();

        vehiculoRepository.saveAll(vehiculos);
        log.info("Se cargaron " + vehiculos.size() + " vehículos.");
    }

    public List<VehiculoDTO> loadData(String filePath) throws IOException {
        List<VehiculoDTO> vehiculosDTO = new ArrayList<>();
        try (FileInputStream fileInputStream = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fileInputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            // Comienza a leer desde la fila 2 (índice 1) para omitir los encabezados
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue; // Salta filas vacías

                String matricula = getStringCellValue(row.getCell(0));
                String[] companiaData = getStringCellValue(row.getCell(1)).trim().split("\\s+(?=\\d+$)");
                String nombreCompania = companiaData.length > 0 ? companiaData[0] : null;
                String rutCompania = companiaData.length > 1 ? companiaData[1] : null;

                LocalDate revisionTecnicaExpiracion = parseDate(row.getCell(2), formatter);
                LocalDate permisoCirculacionExpiracion = parseDate(row.getCell(3), formatter);
                LocalDate seguroObligatorioExpiracion = parseDate(row.getCell(4), formatter);

                Integer anioVehiculo = row.getCell(5) != null && row.getCell(5).getCellType() == CellType.NUMERIC ? (int) row.getCell(5).getNumericCellValue() : null;

                // Verifica que matricula no sea nula antes de crear el objeto Vehiculo
                if (matricula != null) {
                    if (anioVehiculo != null) { // Asegúrate de que anioVehiculo no sea nulo
                        Vehiculo vehiculo = new Vehiculo(
                                matricula, nombreCompania, rutCompania,
                                revisionTecnicaExpiracion, permisoCirculacionExpiracion,
                                seguroObligatorioExpiracion, anioVehiculo
                        );

                        vehiculosDTO.add(vehiculoMapper.toDto(vehiculo));
                    } else {
                        log.info("Fila " + rowIndex + " tiene anioVehiculo nulo, se omitirá.");
                    }
                } else {
                    log.info("Fila " + rowIndex + " tiene matrícula nula, se omitirá.");
                }
            }
        }
        return vehiculosDTO;
    }

    private String getStringCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue()); // Convierte a String
            case FORMULA:
                return cell.getCellFormula(); // Maneja fórmulas si es necesario
            default:
                return null; // O maneja otros tipos según sea necesario
        }
    }

    private LocalDate parseDate(Cell cell, DateTimeFormatter formatter) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return null; // O puedes lanzar una excepción o manejarlo de otra manera
        }

        try {
            if (cell.getCellType() == CellType.STRING) {
                return LocalDate.parse(cell.getStringCellValue(), formatter);
            } else if (cell.getCellType() == CellType.NUMERIC) {
                // Si la celda es numérica, se asume que es una fecha
                return cell.getLocalDateTimeCellValue().toLocalDate();
            } else {
                log.info("Tipo de celda no manejado en " + cell.getAddress());
                return null; // O maneja el error de otra manera
            }
        } catch (DateTimeParseException e) {
            log.error("Error al analizar la fecha en la celda: " + cell.getAddress() + " - Valor: " + cell.getStringCellValue());
            return null; // O maneja el error de otra manera
        }
    }
}

*/