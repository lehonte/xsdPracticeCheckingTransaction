package org.example.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entities.ProducessedRequests;
import org.example.enums.TransactionStatus;
import org.example.events.StrangeTransactionEvent;
import org.example.exceptions.AlreadyProdussedRequestException;
import org.example.repositories.ProducessedRequestsRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaListenerService {

    private final CheckingService checkingService;
    private final ResultService resultService;
    private final ProducessedRequestsRepository producessedRequestsRepository;

    @KafkaListener(topics = "strange_transaction_topic")
    public void checkTransaction(StrangeTransactionEvent event) {

        try {
            producessedRequestsRepository.save(new ProducessedRequests(event.transactionNumber()));
        } catch (AlreadyProdussedRequestException e) {
            log.info("Транзакцию {} уже взяли на обработку", event.transactionNumber());
            return;
        }

        TransactionStatus resultStatus = checkingService.checking(event.phoneNumber());

        resultService.resultOfChecking(resultStatus, event.transactionNumber());
    }
}
