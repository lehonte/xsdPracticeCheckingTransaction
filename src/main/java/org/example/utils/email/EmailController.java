package org.example.utils.email;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.enums.CheckingResult;
import org.example.enums.Reason;
import org.example.enums.TransactionStatus;
import org.example.services.OutboxToSendService;
import org.example.utils.CheckingService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class EmailController {

    private final ConfirmByEmailService confirmByEmailService;
    private final CheckingService checkingService;
    private final OutboxToSendService outboxToSendService;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @PostMapping("/confirm-code")
    public String handleFormSubmit(@RequestParam String codeConfirm,
                                   @RequestParam String transactionNumber,
                                   Model model) {
        log.info("Получен код из формы: {}", codeConfirm);
        if (codeConfirm != null) model.addAttribute("message", "Код получен");
        else model.addAttribute("message", "Код не получен");


        Optional<DataToSend> data = getPending(transactionNumber);

        CheckingResult result;
        if (confirmByEmailService.confirmCode(codeConfirm, transactionNumber) && data.isPresent()) {


            result = checkingService.saveResultOfChecking(email, transactionNumber, amount, owner);
            outboxToSendService.saveToSend(result, transactionNumber);
        }
        else if (data.isPresent()) {
            result = new CheckingResult(TransactionStatus.BLOCKED, Reason.R4);
            outboxToSendService.saveToSend(result, transactionNumber);
        }

        return "empty-page";
    }

    private Optional<DataToSend> getPending(String transactionNumber) {
        String json = redisTemplate.opsForValue().get(transactionNumber);
        if (json == null) return Optional.empty();
        try {
            return Optional.of(objectMapper.readValue(json, DataToSend.class));
        } catch (JsonProcessingException e) {
            throw  new IllegalStateException("Не удалось прочитать данные подтверждения", e);
        }
    }
}
