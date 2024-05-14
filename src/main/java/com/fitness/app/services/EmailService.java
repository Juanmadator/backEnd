package com.fitness.app.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendConfirmationEmail(String to, String subject, String content) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

        try {
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true); // Establecer el contenido como HTML

            mailSender.send(message);
        } catch (MessagingException e) {
           log.info("Error",e);
        }
    }


    public void sendPasswordResetEmail(String to, String token, String appUrl) {
        String subject = "Solicitud de restablecimiento de contraseña";
        String content = "Para restablecer su contraseña, haga clic en el siguiente enlace:\n"
                + appUrl + "/reset-password?token=" + token;
        sendConfirmationEmail(to, subject, content);
    }
}

