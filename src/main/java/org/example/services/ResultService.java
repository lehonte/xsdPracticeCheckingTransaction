package org.example.services;

import lombok.RequiredArgsConstructor;
import org.example.enums.TransactionStatus;
import org.example.events.ResultOfChekingEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResultService {

    private final KafkaTemplate<String, ResultOfChekingEvent> kafkaTemplate;

    public void resultOfChecking (TransactionStatus status, String transactionNumber) {
        ResultOfChekingEvent event = new ResultOfChekingEvent(status, transactionNumber);
        kafkaTemplate.send("result_of_checking", event);
    }
}
