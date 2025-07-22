package cl.ufro.dci.radalco.email.service;

import cl.ufro.dci.radalco.vehiculo.model.Vehiculo;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class emailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void sendExpirationNotifications(List<Vehiculo> vehicles, int daysBefore) throws MessagingException {
        log.debug("Enviando notificación para {} vehículos", vehicles.size());
        vehicles.forEach(v -> log.debug("Vehículo: {} - Revisión: {}",
                v.getMatricula(),
                v.getRevisionTecnicaExpiracion())
        );

        Map<String, List<Vehiculo>> classifiedVehicles = classifyVehicles(vehicles);

        String subject = "Vehículos con revisión técnica por vencer (Próximos " + daysBefore + " días)";
        String[] recipients = {"i.morales04@ufromail.cl"}; // Destinatarios

        // Opción 1: Email simple (texto plano)
        //sendSimpleEmail(recipients, subject, buildPlainTextContent(vehicles, daysBefore));

        // Opción 2: Email HTML (más elegante)
        sendHtmlEmail(recipients, subject, classifiedVehicles);
    }

    // =============== Métodos Privados ===============

    /**
     * Envía un email simple (texto plano).
     */
    private void sendSimpleEmail(String[] recipients, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipients);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }

    /**
     * Envía un email con formato HTML (requiere Thymeleaf).
     */
    private void sendHtmlEmail(String[] recipients, String subject, Map<String, List<Vehiculo>> classifiedVehicles) throws MessagingException {
        log.debug("Datos a enviar al template:");
        log.debug("Vencidos: {}", classifiedVehicles.getOrDefault("VENCIDO", List.of()).size());
        log.debug("Por vencer: {}", classifiedVehicles.getOrDefault("POR_VENCER", List.of()).size());

        Context context = new Context();
        context.setVariable("vencidos", classifiedVehicles.getOrDefault("VENCIDO", List.of()));
        context.setVariable("porVencer", classifiedVehicles.getOrDefault("POR_VENCER", List.of()));
        context.setVariable("hoy", LocalDate.now().format(DATE_FORMATTER)); // Para mostrar
        context.setVariable("today", LocalDate.now()); // Para cálculos

        // Debug: muestra las variables que se están enviando
        log.debug("Variables en contexto: {}", context.getVariableNames());

        String htmlContent = templateEngine.process("emails/expiration-notification", context);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setTo(recipients);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // true = HTML

        mailSender.send(mimeMessage);
    }

    /**
     * Construye el contenido del email en texto plano.
     */
    // En el método buildPlainTextContent (adaptado)
    private String buildPlainTextContent(List<Vehiculo> vehiculos, int daysBefore) {
        StringBuilder content = new StringBuilder();
        content.append("**Atención:** Los siguientes vehículos tienen revisión técnica por vencer en los próximos ")
                .append(daysBefore).append(" días.\n\n");

        content.append("============================================\n");
        content.append("| Matrícula  | Compañía           | Vencimiento  |\n");
        content.append("============================================\n");

        for (Vehiculo vehiculo : vehiculos) {
            StringBuilder append = content.append(String.format("| %-10s | %-18s | %-12s |\n",
                    vehiculo.getMatricula(),
                    vehiculo.getNombreCompania().substring(0, Math.min(18, vehiculo.getNombreCompania().length())),
                    vehiculo.getRevisionTecnicaExpiracion().format(DATE_FORMATTER)));
        }

        content.append("\n\nAcción requerida: Renovar la revisión técnica.\n");
        return content.toString();
    }

    private Map<String, List<Vehiculo>> classifyVehicles(List<Vehiculo> vehiculos) {
        LocalDate hoy = LocalDate.now();

        Map<String, List<Vehiculo>> result = vehiculos.stream()
                .collect(Collectors.groupingBy(v -> {
                    if (v.getRevisionTecnicaExpiracion().isBefore(hoy)) {
                        return "VENCIDO";
                    } else {
                        return "POR_VENCER";
                    }
                }));

        log.debug("Clasificación resultante: {}", result);
        return result;
    }
}
