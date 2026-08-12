package org.example.services;

import lombok.RequiredArgsConstructor;
import org.example.enums.TransactionStatus;
import org.example.events.StrangeTransactionEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaListenerService {

    private final CheckingService checkingService;
    private final ResultService resultService;

    @KafkaListener(topics = "strange_transaction_topic")
    public void checkTransaction(StrangeTransactionEvent event) {
        TransactionStatus resultStatus = checkingService.checking(event.phoneNumber());

        resultService.resultOfChecking(resultStatus);
    }
}
