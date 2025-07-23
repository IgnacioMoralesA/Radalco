package cl.ufro.dci.radalco.vehiculo.service;

import cl.ufro.dci.radalco.vehiculo.model.Vehiculo;
import cl.ufro.dci.radalco.vehiculo.repository.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class VehiculoService {

    @Autowired
    private VehiculoRepository repo;

    @Transactional
    public boolean actualizarRevisionTecnica(String matricula, LocalDate fechaActualizacion) {
        Optional<Vehiculo> vehiculoOpt = repo.findById(matricula);
        if(vehiculoOpt.isPresent()){
            Vehiculo vehiculo = vehiculoOpt.get();
            LocalDate nuevaRevision = fechaActualizacion.plusMonths(vehiculo.getFrecuenciaRevisionTecnica());
            vehiculo.setRevisionTecnicaExpiracion(nuevaRevision);
            repo.save(vehiculo);
            return true;
        }
        return false;
    }

    public Optional<Vehiculo> obtenerPorMatricula(String matricula) {
        return repo.findById(matricula);
    }

    @Transactional(readOnly = true)
    public List<Vehiculo> obtenerTodos() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public List<Vehiculo> obtenerProximosAVencer(LocalDate inicio, LocalDate fin) {
        return repo.findByRevisionTecnicaExpiracionBetween(inicio, fin);
    }

    @Transactional(readOnly = true)
    public List<Vehiculo> obtenerVencidos(LocalDate fechaReferencia) {
        return repo.findByRevisionTecnicaExpiracionBefore(fechaReferencia);
    }
}
