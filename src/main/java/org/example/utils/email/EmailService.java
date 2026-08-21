package org.example.utils.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendEmailMessage(String code, String email, String transactionNumber, BigDecimal amount, String owner) {

        Context context = new Context();
        context.setVariable("owner", owner);
        context.setVariable("code", code);
        context.setVariable("transaction_number", transactionNumber);
        context.setVariable("amount", amount);
        String htmlContent = templateEngine.process("confirmation-email", context);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper mimiHelper = new MimeMessageHelper(mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
            mimiHelper.setTo(email);
            mimiHelper.setSubject("Подтвердите перевод");
            mimiHelper.setText(htmlContent, true);
        } catch (MessagingException e) {
            throw new IllegalStateException("Не удалось сформировать письмо", e);
        }

        mailSender.send(mimeMessage);
    }
}
