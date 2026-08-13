package org.example.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.eventEntities.OutboxEvent;
import org.example.eventEntities.ProducessedTransactions;
import org.example.enums.TransactionStatus;
import org.example.events.ResultOfChekingEvent;
import org.example.events.StrangeTransactionEvent;
import org.example.repositories.OutboxEventRepository;
import org.example.repositories.ProducessedRequestsRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaListenerService {

    private final CheckingService checkingService;
    private final ProducessedRequestsRepository producessedRequestsRepository;
    private final ObjectMapper jacksonObjectMapper;
    private final OutboxEventRepository outboxEventRepository;

    @Transactional
    @KafkaListener(topics = "strange_transaction_topic")
    public void checkTransaction(StrangeTransactionEvent event) {

        if (producessedRequestsRepository.existsByTransactionNumber(event.transactionNumber())) {
            log.info("Транзакцию {} уже взяли на обработку", event.transactionNumber());
            return;
        }
        // Если не существует — сохраняем факт начала обработки
        producessedRequestsRepository.save(new ProducessedTransactions(event.transactionNumber()));

        TransactionStatus resultStatus = checkingService.checking(event.phoneNumber());
        ResultOfChekingEvent resultEvent = new ResultOfChekingEvent(resultStatus, event.transactionNumber());

        try {
            String json = jacksonObjectMapper.writeValueAsString(resultEvent);

            OutboxEvent outboxEvent = new OutboxEvent(
                    event.transactionNumber(),
                    "result_of_checking",
                    json);

            outboxEventRepository.save(outboxEvent); //сохраняем в события, что нужно отправить

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Упала сериализация транзакции: " + event.transactionNumber(), e);
        }
    }
}
