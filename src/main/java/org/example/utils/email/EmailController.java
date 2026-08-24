package org.example.utils.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.enums.CheckingResult;
import org.example.enums.Reason;
import org.example.enums.TransactionStatus;
import org.example.services.OutboxToSendService;
import org.example.utils.CheckingService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class EmailController {

    private final ConfirmByEmailService confirmByEmailService;
    private final CheckingService checkingService;
    private final OutboxToSendService outboxToSendService;

    @PostMapping("/confirm-code")
    @ResponseBody
    public ResponseEntity<String> handleFormSubmit(@RequestParam String codeConfirm,
                                   @RequestParam String transactionNumber) {
        log.info("Получен код из формы: {}", codeConfirm);
        log.info("Получен номер транзакции из формы: {}", transactionNumber);

        DataToSend data;
        try {
            data = confirmByEmailService.getPending(transactionNumber);
        } catch (RuntimeException e) {
            log.warn("Транзакция {} не найдена (срок действия истек или транзакции не существовало)", transactionNumber);
            return ResponseEntity.badRequest().body("Транзакция не найдена или срок действия кода истек");
        }

        CheckingResult result;
        if (codeConfirm != null && confirmByEmailService.confirmCode(codeConfirm, data.code())) {
            log.info("Полученный код {} подтвержден", codeConfirm);
            result = checkingService.saveResultOfChecking(data.email(), transactionNumber, data.amount(), data.owner());
            outboxToSendService.saveToSend(result, transactionNumber);
            return ResponseEntity.ok("Транзакция успешно подтверждена");
        }
        else if (codeConfirm != null){
            log.info("Полученный код {} неверный", codeConfirm);
            result = new CheckingResult(TransactionStatus.BLOCKED, Reason.R4);
            outboxToSendService.saveToSend(result, transactionNumber);
            return ResponseEntity.ok("Неверный код подтверждения. Транзакция заблокирована");
        }

        return ResponseEntity.badRequest().body("Произошла ошибка при подтверждении");
    }
}
