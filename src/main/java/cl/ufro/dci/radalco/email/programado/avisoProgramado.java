package cl.ufro.dci.radalco.email.programado;


import cl.ufro.dci.radalco.email.service.emailService;
import cl.ufro.dci.radalco.vehiculo.model.Vehiculo;
import cl.ufro.dci.radalco.vehiculo.repository.VehiculoRepository;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Component
public class avisoProgramado {

    @Autowired
    private emailService emailService;

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Scheduled(cron = "0 0 9 * * ?") // Ejecuta diario a las 9 AM
    public void enviarAvisosVencimiento() throws MessagingException {
        LocalDate hoy = LocalDate.now();
        LocalDate fechaLimite = hoy.plusDays(30); // Avisar con 30 días de anticipación

        List<Vehiculo> vehiculosPorVencer = vehiculoRepository
                .findByRevisionTecnicaExpiracionBetween(hoy, fechaLimite);

        for (Vehiculo vehiculo : vehiculosPorVencer) {
            long diasRestantes = ChronoUnit.DAYS.between(hoy, vehiculo.getRevisionTecnicaExpiracion());

            // Suponiendo que cada vehículo tiene un email asociado
            emailService.enviarAvisoVencimiento(
                    vehiculo.getMatricula(),
                    "i.morales.04@ufromail.cl",
                    vehiculo.getRevisionTecnicaExpiracion(),
                    diasRestantes
                );
        }
    }
}
