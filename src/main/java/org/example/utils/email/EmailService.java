package org.example.utils.email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendSimpleEmailMessage(String toAdress, String subject, String message) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(toAdress);
        msg.setSubject(subject);
        msg.setText(message);
        mailSender.send(msg);
    }
}
