package cl.ufro.dci.radalco.vehiculo.controller;

import cl.ufro.dci.radalco.vehiculo.DTO.RevisionDTO;
import cl.ufro.dci.radalco.vehiculo.model.Vehiculo;
import cl.ufro.dci.radalco.vehiculo.repository.VehiculoRepository;
import cl.ufro.dci.radalco.vehiculo.service.VehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/vehiculos")
public class VehiculoController {

    private final VehiculoService vehiculoService;
    private final VehiculoRepository repo;

    @Autowired
    public VehiculoController(VehiculoService vehiculoService, VehiculoRepository repo) {
        this.vehiculoService = vehiculoService;
        this.repo = repo;
    }

    // ======================
    // Endpoints de la API (REST)
    // ======================

    @RestController
    @RequestMapping("/api/vehiculos")
    public static class VehiculoApiController {

        private final VehiculoService vehiculoService;
        private final VehiculoRepository repo;

        @Autowired
        public VehiculoApiController(VehiculoService vehiculoService,  VehiculoRepository repo) {
            this.vehiculoService = vehiculoService;
            this.repo = repo;
        }

        /**
         * Actualiza la fecha de revisión técnica (versión con DTO)
         */
        @PutMapping("/{matricula}/revision-tecnica")
        public ResponseEntity<Void> actualizarRevisionTecnica(
                @PathVariable String matricula,
                @RequestBody RevisionDTO revisionDTO) {
            boolean actualizado = vehiculoService.actualizarRevisionTecnica(
                    matricula,
                    revisionDTO.getRevisionTecnicaExpiracion()
            );
            return actualizado ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        }

        /**
         * Obtiene información de un vehículo específico
         */
        @GetMapping("/{matricula}")
        public ResponseEntity<Vehiculo> obtenerVehiculo(@PathVariable String matricula) {
            return vehiculoService.obtenerPorMatricula(matricula)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }

        /**
         * Lista vehículos con revisión técnica próxima a vencer
         */
        @GetMapping("/proximos-vencer")
        public ResponseEntity<List<Vehiculo>> obtenerProximosAVencer(
                @RequestParam(defaultValue = "30") int dias) {
            LocalDate hoy = LocalDate.now();
            LocalDate fechaLimite = hoy.plusDays(dias);
            return ResponseEntity.ok(vehiculoService.obtenerProximosAVencer(hoy, fechaLimite));
        }

        /**
         * Lista vehículos con revisión técnica vencida
         */
        @GetMapping("/vencidos")
        public ResponseEntity<List<Vehiculo>> obtenerVencidos() {
            return ResponseEntity.ok(vehiculoService.obtenerVencidos(LocalDate.now()));
        }
    }

    // ======================
    // Endpoints de Vistas (MVC)
    // ======================

    /**
     * Muestra la lista de vehículos (HTML)
     */
    @GetMapping("/")
    public String listarVehiculos(Model model) {
        model.addAttribute("vehiculos", vehiculoService.obtenerTodos());
        model.addAttribute("vehiculosProximos",
                vehiculoService.obtenerProximosAVencer(LocalDate.now(), LocalDate.now().plusDays(30)));
        model.addAttribute("vehiculosVencidos",
                vehiculoService.obtenerVencidos(LocalDate.now()));
        return "lista-vehiculos";
    }

    /**
     * Endpoint de confirmación desde emails
     */
    @GetMapping("/confirmar-revision")
    public String confirmarRevision(
            @RequestParam String matricula,
            Model model) {

        Optional<Vehiculo> vehiculoOpt = vehiculoService.obtenerPorMatricula(matricula);

        if (vehiculoOpt.isEmpty()) {
            model.addAttribute("error", "No se encontró el vehículo con matrícula: " + matricula);
            return "error-confirmacion";
        }

        boolean actualizado = vehiculoService.actualizarRevisionTecnica(matricula, repo.findByMatricula(matricula).getRevisionTecnicaExpiracion());

        if (!actualizado) {
            model.addAttribute("error", "No se pudo actualizar la revisión técnica");
            return "error-confirmacion";
        }

        Vehiculo vehiculo = vehiculoService.obtenerPorMatricula(matricula).orElseThrow();

        model.addAttribute("vehiculo", vehiculo);
        model.addAttribute("fechaActual", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        model.addAttribute("nuevaFecha", vehiculo.getRevisionTecnicaExpiracion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        return "confirmacion-revision";
    }


    /**
     * Muestra formulario de edición
     */
    @GetMapping("/editar/{matricula}")
    public String mostrarFormularioEdicion(@PathVariable String matricula, Model model) {
        vehiculoService.obtenerPorMatricula(matricula).ifPresent(vehiculo -> {
            model.addAttribute("vehiculo", vehiculo);

            // Prepara DTO para el formulario
            RevisionDTO revisionDTO = new RevisionDTO();
            revisionDTO.setRevisionTecnicaExpiracion(vehiculo.getRevisionTecnicaExpiracion());
            model.addAttribute("revisionDTO", revisionDTO);
        });
        return "editar-vehiculo";
    }

    /**
     * Procesa el formulario de edición
     */
    @PostMapping("/editar/{matricula}")
    public String procesarEdicion(
            @PathVariable String matricula,
            @ModelAttribute RevisionDTO revisionDTO) {
        vehiculoService.actualizarRevisionTecnica(matricula, revisionDTO.getRevisionTecnicaExpiracion());
        return "redirect:/vehiculos";
    }
}