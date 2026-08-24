package org.example.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dtoEvents.ResultOfChecking;
import org.example.enums.CheckingResult;
import org.example.enums.TransactionStatus;
import org.example.eventEntities.OutboxEvent;
import org.example.eventEntities.ProducessedTransactions;
import org.example.events.CheckTransactionEvent;
import org.example.repositories.OutboxEventRepository;
import org.example.repositories.ProducessedRequestsRepository;
import org.example.utils.CheckingService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaListenerService {

    private final CheckingService checkingService;
    private final ProducessedRequestsRepository producessedRequestsRepository;
    private final TransactionTemplate transactionTemplate;
    private final OutboxToSendService outboxToSendService;

    @KafkaListener(topics = "check_transaction_topic")
    public void listeningKafkaTransactions(CheckTransactionEvent event) {
        transactionTemplate.executeWithoutResult(status -> {
            checkTransaction(event);
        }); //это мы, чтобы не создавать отдельный бин для транзакции, обворачиваем метод который должен быть транзакцией
    }//в транзакцию и вызываем из слушателя чтобы сначала закоммитилась бд (или откат) а потом только офсет
    //иначе бд комитится после выхода из метода когда офсет уже отправлен и откат тогда будет для бд, а для кафки нет (для нее сообщение уже просмотрено и не отправится)

    public void checkTransaction(CheckTransactionEvent event) {
        String email = event.getEmail();
        String transactionNumber = event.getTransactionNumber();
        BigDecimal amount = event.getAmount();
        String owner = event.getOwner();

        if (producessedRequestsRepository.existsByTransactionNumber(transactionNumber)) {
            log.info("Транзакцию {} уже взяли на обработку", transactionNumber);
            return;
        }
        // Если не существует — сохраняем факт начала обработки
        producessedRequestsRepository.save(new ProducessedTransactions(transactionNumber));

        log.info("Транзакция {} получена на обработку", transactionNumber);
        CheckingResult result = checkingService.checking(email, transactionNumber, amount, owner);
        log.info("Транзакция {} прошла первичную обработку", transactionNumber);

        if (result.status() == TransactionStatus.BLOCKED) outboxToSendService.saveToSend(result, transactionNumber);
    }

}

