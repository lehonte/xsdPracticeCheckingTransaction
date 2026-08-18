package org.example.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.eventEntities.OutboxEvent;
import org.example.events.ResultOfChekingEvent;
import org.example.repositories.OutboxEventRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ResultService {

    private final KafkaTemplate<String, ResultOfChekingEvent> kafkaTemplate;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void resultOfChecking () {
        List<OutboxEvent> outboxEvents = outboxEventRepository.findBySentFalseOrderByCreatedAtAsc(PageRequest.of(0, 100));

        for (OutboxEvent outboxEvent : outboxEvents) {
            try {
                String topic = outboxEvent.getTopic();
                String payload = outboxEvent.getPayload();
                ResultOfChekingEvent event = objectMapper.readValue(payload, ResultOfChekingEvent.class);

                kafkaTemplate.send(topic, event).get(); //get, чтобы ожидать подтверждения, что событие отправилось
                log.info("Транзакцию {} вернули с обработанным статусом", event.transactionNumber());
                outboxEvent.setSent(true);
                outboxEventRepository.save(outboxEvent);
            } catch (JsonProcessingException e) {
                log.error("Проблемы при формировании события {}", outboxEvent.getKey(), e);
                outboxEvent.setSent(true);
                outboxEventRepository.save(outboxEvent);
            } catch (Exception e) {
                log.error("Проблемы при отправлении события {}", outboxEvent.getKey(), e);
            }
        }
    }
}
