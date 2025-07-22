package cl.ufro.dci.radalco.vehiculo.model;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "vehiculos")
@Data
public class Vehiculo {
    @Id
    private String matricula;

    private String nombreCompania;
    private String rutCompania;

    private LocalDate revisionTecnicaExpiracion;
    private LocalDate permisoCirculacionExpiracion;
    private LocalDate seguroObligatorioExpiracion;
    private int anioVehiculo;
    private int frecuenciaRevisionTecnica;

    public Vehiculo(String matricula, String empresa, String rutEmpresa, LocalDate revisionTecnicaExpiracion, LocalDate permisoCirculacionExpiracion, LocalDate seguroObligatorioExpiracion, int anioVehiculo) {
        this.matricula = matricula;
        this.nombreCompania = empresa;
        this.rutCompania = rutEmpresa;
        this.revisionTecnicaExpiracion = revisionTecnicaExpiracion;
        this.permisoCirculacionExpiracion = permisoCirculacionExpiracion;
        this.seguroObligatorioExpiracion = seguroObligatorioExpiracion;
        this.anioVehiculo = anioVehiculo;
    }

    public Vehiculo() {

    }
}
