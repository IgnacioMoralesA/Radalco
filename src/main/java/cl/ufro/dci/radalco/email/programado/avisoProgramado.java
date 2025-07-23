package cl.ufro.dci.radalco.email.programado;

import cl.ufro.dci.radalco.email.service.emailService;
import cl.ufro.dci.radalco.vehiculo.model.Vehiculo;
import cl.ufro.dci.radalco.vehiculo.service.VehiculoService;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class avisoProgramado {

    private final emailService emailService;
    private final VehiculoService vehiculoService;

    @Autowired
    public avisoProgramado(emailService emailService, VehiculoService vehiculoService) {
        this.emailService = emailService;
        this.vehiculoService = vehiculoService;
    }

    @Scheduled(cron = "0 * * * * ?")
    @Transactional(propagation = Propagation.REQUIRED) // Añade esta anotación
    public void enviarAvisosVencimiento() {
        try {
            LocalDate hoy = LocalDate.now();
            LocalDate fechaLimite = hoy.plusDays(30);

            // Obtener todos los vehículos relevantes
            List<Vehiculo> vehiculosVencidos = vehiculoService.obtenerVencidos(hoy);
            List<Vehiculo> vehiculosPorVencer = vehiculoService.obtenerProximosAVencer(hoy, fechaLimite);

            if (!vehiculosVencidos.isEmpty() || !vehiculosPorVencer.isEmpty()) {
                // Enviar un solo correo con ambas listas
                String emailDestinatario = "i.morales04@ufromail.cl"; // Reemplaza con el email destino
                emailService.enviarAvisoVencimiento(emailDestinatario, vehiculosVencidos, vehiculosPorVencer);
            }
        } catch (Exception e) {
            log.error("Error en el proceso programado de avisos de vencimiento", e);
        }
    }
}
