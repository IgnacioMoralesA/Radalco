package cl.ufro.dci.radalco.email.service;

import cl.ufro.dci.radalco.vehiculo.model.Vehiculo;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    @Value("")
    private String baseUrl;

    public void enviarAvisoVencimiento(String matricula, String emailDestino, LocalDate fechaVencimiento, long diasRestantes) throws MessagingException {
        Context context = new Context();
        context.setVariable("matricula", matricula);
        context.setVariable("fechaVencimiento", fechaVencimiento);
        context.setVariable("diasRestantes", diasRestantes);
        context.setVariable("enlaceConfirmacion", generarEnlaceConfirmacion(matricula));

        String contenido = templateEngine.process("email/aviso-vencimiento", context);

        MimeMessage mensaje = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, true);

        helper.setTo(emailDestino);
        helper.setSubject("[Urgente] Revisión técnica próxima a vencer - " + matricula);
        helper.setText(contenido, true); // true indica que es HTML

        mailSender.send(mensaje);
    }

    private String generarEnlaceConfirmacion(String matricula) {
        return baseUrl + "/confirmar-revision?matricula=" + URLEncoder.encode(matricula, StandardCharsets.UTF_8);
    }
}
