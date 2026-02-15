package com.node5.memberservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KafkaDLTConfig {

    private final ConsumerFactory<String, Object> consumerFactory;

    @Bean
    public DefaultErrorHandler dltErrorHandler() {
        ConsumerRecordRecoverer recoverer = (record, ex) -> {
            log.error("DLT final failure topic={}, key={}, ex={}", record.topic(), record.key(), ex.getMessage(), ex);
        };

        return new DefaultErrorHandler(recoverer, new FixedBackOff(0L, 0L));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> dltKafkaListenerContainerFactory(
            @Qualifier("dltErrorHandler") DefaultErrorHandler errorHandler
    ) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }
}
