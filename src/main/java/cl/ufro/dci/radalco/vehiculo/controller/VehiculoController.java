package cl.ufro.dci.radalco.vehiculo.controller;

import cl.ufro.dci.radalco.vehiculo.repository.VehiculoRepository;
import cl.ufro.dci.radalco.vehiculo.service.VehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    @Autowired
    private VehiculoService Service;

    @PutMapping("/actualizar-revision/{matricula}")
    public ResponseEntity<String> actualizarRevision(
            @PathVariable String matricula,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate revision
    ){
        boolean actualizado = Service.actualizarRevisionTecnica( matricula, revision );
        if(actualizado){
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}