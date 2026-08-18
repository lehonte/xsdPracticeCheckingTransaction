package org.example.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaCofig {

    @Bean
    public NewTopic resultOfChecking() {
        return TopicBuilder.name("result_of_checking")
                .partitions(3)
                .replicas(1)
                .config(TopicConfig.RETENTION_MS_CONFIG, "86400000")
                .config(TopicConfig.RETENTION_BYTES_CONFIG, "524288000")
                .build();
    }
}
