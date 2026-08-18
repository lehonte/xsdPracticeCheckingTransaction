package org.example.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.eventEntities.OutboxEvent;
import org.example.eventEntities.ProducessedTransactions;
import org.example.enums.TransactionStatus;
import org.example.events.ResultOfChekingEvent;
import org.example.events.CheckTransactionEvent;
import org.example.repositories.OutboxEventRepository;
import org.example.repositories.ProducessedRequestsRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaListenerService {

    private final CheckingService checkingService;
    private final ProducessedRequestsRepository producessedRequestsRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;
    private final TransactionTemplate transactionTemplate;

    @KafkaListener(topics = "check_transaction_topic")
    public void liseningKafkaStrangeTransactions(CheckTransactionEvent event) {
        transactionTemplate.executeWithoutResult(status -> {
            checkTransaction(event);
        }); //это мы, чтобы не создавать отдельный бин для транзакции, обворачиваем метод который должен быть транзакцией
    }//в транзакцию и вызываем из слушателя чтобы сначала закоммитилась бд (или откат) а потом только офсет
    //иначе бд комитится после выхода из метода когда офсет уже отправлен и откат тогда будет для бд, а для кафки нет (для нее сообщение уже просмотрено и не отправится)

    public void checkTransaction(CheckTransactionEvent event) {
        if (producessedRequestsRepository.existsByTransactionNumber(event.transactionNumber())) {
            log.info("Транзакцию {} уже взяли на обработку", event.transactionNumber());
            return;
        }
        // Если не существует — сохраняем факт начала обработки
        producessedRequestsRepository.save(new ProducessedTransactions(event.transactionNumber()));

        log.info("Транзакцию {} получена на обработку", event.transactionNumber());
        TransactionStatus resultStatus = checkingService.checking(event.phoneNumber());
        log.info("Транзакцию {} обработали", event.transactionNumber());
        ResultOfChekingEvent resultEvent = new ResultOfChekingEvent(resultStatus, event.transactionNumber());

        try {
            String json = objectMapper.writeValueAsString(resultEvent);

            OutboxEvent outboxEvent = new OutboxEvent(
                    event.transactionNumber(),
                    "result_of_checking",
                    json);

            outboxEventRepository.save(outboxEvent); //сохраняем в события, что нужно отправить
            log.info("Транзакцию {} добавлена в очередь на отправку", event.transactionNumber());

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Упала сериализация транзакции: " + event.transactionNumber(), e);
        }
    }
}

