package cl.ufro.dci.radalco.vehiculo.repository;

import cl.ufro.dci.radalco.vehiculo.model.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface VehiculoRepository extends JpaRepository<Vehiculo, String> {

    // Versión corregida
    List<Vehiculo> findByRevisionTecnicaExpiracionBefore(LocalDate fecha);

    List<Vehiculo> findByRevisionTecnicaExpiracionBetween(LocalDate inicio, LocalDate fin);

    Vehiculo findByMatricula(String matricula);

    // Alternativa con @Query explícita (si prefieres)
    @Query("SELECT v FROM Vehiculo v WHERE v.revisionTecnicaExpiracion < :fecha")
    List<Vehiculo> buscarRevisionesVencidas(@Param("fecha") LocalDate fecha);
}
