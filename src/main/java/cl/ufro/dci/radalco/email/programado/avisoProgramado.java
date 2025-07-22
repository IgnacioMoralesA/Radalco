package cl.ufro.dci.radalco.email.programado;


import cl.ufro.dci.radalco.email.service.emailService;
import cl.ufro.dci.radalco.vehiculo.model.Vehiculo;
import cl.ufro.dci.radalco.vehiculo.repository.VehiculoRepository;
import cl.ufro.dci.radalco.vehiculo.service.VehiculoService;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class avisoProgramado {

    private final VehiculoRepository vehiculoRepository;
    private final emailService emailService;
    private final VehiculoService vehiculoService;

    public avisoProgramado(VehiculoRepository vehiculoRepository, emailService emailService, VehiculoService vehiculoService) {
        this.vehiculoRepository = vehiculoRepository;
        this.emailService = emailService;
        this.vehiculoService = vehiculoService;
    }

    @Scheduled(cron = "0 0 9 * * ?") // Ejecutar todos los días a las 9 AM
    public void checkExpiringReviews() throws MessagingException {
        LocalDate hoy  = LocalDate.now();
        int daysBefore = 30; // Notificar 30 días antes

        // 1. Obtener revisiones vencidas (fecha < hoy)
        List<Vehiculo> vencidos = vehiculoRepository.findByRevisionTecnicaExpiracionBefore(hoy);

        // 2. Obtener revisiones por vencer (hoy <= fecha <= hoy+30 días)
        List<Vehiculo> porVencer = vehiculoRepository.findByRevisionTecnicaExpiracionBetween(
                hoy,
                hoy.plusDays(30)
        );

        // 3. Combinar listas (eliminando duplicados)
        List<Vehiculo> todos = Stream.concat(vencidos.stream(), porVencer.stream())
                .distinct()
                .collect(Collectors.toList());

        if (!todos.isEmpty()) {
            emailService.sendExpirationNotifications(todos, 30);
        } else {
            log.info("No hay revisiones vencidas ni por vencer");
        }
    }
}
