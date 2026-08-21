package org.example.utils.email;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.Duration;

@Component
@RequiredArgsConstructor
@Setter
public class ConfirmByEmailService {

    private final EmailService emailService;

    private final RedisTemplate<String, String> redisTemplate;
    private static final SecureRandom random = new SecureRandom();
    private final ObjectMapper objectMapper;

    public void startConfirmation(String email, String transactionNumber, BigDecimal amount, String owner) {
        String code = generateCode();
        DataToSend dataToSend = new DataToSend(code, email, amount, owner);

        try {
            String json = objectMapper.writeValueAsString(dataToSend);
            redisTemplate.opsForValue().set(transactionNumber, json, Duration.ofMinutes(2));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Не удалось сохранить данные подтверждения", e);
        }

        emailService.sendEmailMessage(code, email, transactionNumber, amount, owner);
    }

    public boolean confirmCode(String transactionNumber, String codeConfirm) {
        Object cache = redisTemplate.opsForValue().get(transactionNumber);
        return codeConfirm.equals(cache);
    }

    private String generateCode() {
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

}
