package cl.ufro.dci.radalco.email.service;

import cl.ufro.dci.radalco.vehiculo.model.Vehiculo;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

@Slf4j
@Service
public class emailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${app.base-url:localhost:8080}")
    private String baseUrl;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void enviarAvisoVencimiento(String emailDestinatario,
                                       List<Vehiculo> vehiculosVencidos,
                                       List<Vehiculo> vehiculosPorVencer)
            throws MessagingException {
        Context context = new Context();
        context.setVariable("vehiculosVencidos", vehiculosVencidos);
        context.setVariable("vehiculosPorVencer", vehiculosPorVencer);
        context.setVariable("baseUrl", baseUrl);

        String contenido = templateEngine.process("aviso-vencimiento", context);

        MimeMessage mensaje = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

        helper.setTo(emailDestinatario);
        helper.setSubject("[Radalco] Estado de Revisiones Técnicas");
        helper.setText(contenido, true);

        mailSender.send(mensaje);
    }


    public String generarEnlaceConfirmacion(String matricula) {
        return baseUrl + "/vehiculos/confirmar-revision?matricula="
                + URLEncoder.encode(matricula, StandardCharsets.UTF_8);
    }
}
