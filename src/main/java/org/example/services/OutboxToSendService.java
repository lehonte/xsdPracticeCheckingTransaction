package org.example.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dtoEvents.ResultOfChecking;
import org.example.enums.CheckingResult;
import org.example.eventEntities.OutboxEvent;
import org.example.repositories.OutboxEventRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxToSendService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public void saveToSend(CheckingResult result, String transactionNumber) {
        ResultOfChecking resultEvent = new ResultOfChecking(result.status(), transactionNumber, result.reason().getReason());

        try {
            String json = objectMapper.writeValueAsString(resultEvent);

            OutboxEvent outboxEvent = new OutboxEvent(
                    transactionNumber,
                    "result_of_checking",
                    json);

            outboxEventRepository.save(outboxEvent); //сохраняем в события, что нужно отправить
            log.info("Транзакцию {} добавлена в очередь на отправку", transactionNumber);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Упала сериализация транзакции: " + transactionNumber, e);
        }
    }
}
