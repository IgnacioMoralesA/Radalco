package cl.ufro.dci.radalco.vehiculo.service;

import cl.ufro.dci.radalco.vehiculo.model.Vehiculo;
import cl.ufro.dci.radalco.vehiculo.repository.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class VehiculoService {

    @Autowired
    private VehiculoRepository repo;

    public boolean actualizarRevisionTecnica(String Matricula, LocalDate fechaRevisionActual){
        Optional<Vehiculo> vehiculoOpt = repo.findById(Matricula);
        if(vehiculoOpt.isPresent()){
            Vehiculo vehiculo = vehiculoOpt.get();
            LocalDate nuevaRevision = fechaRevisionActual.plusDays(vehiculo.getFrecuenciaRevisionTecnica());
            vehiculo.setRevisionTecnicaExpiracion(nuevaRevision);
            repo.save(vehiculo);
            return true;
        }
        return false;
    }
}
