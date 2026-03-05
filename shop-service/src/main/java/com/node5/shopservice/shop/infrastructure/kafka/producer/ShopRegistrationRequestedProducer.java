package com.node5.shopservice.shop.infrastructure.kafka.producer;

import com.node5.common.event.ShopRegistrationRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShopRegistrationRequestedProducer {

    private final KafkaTemplate<String, ShopRegistrationRequestedEvent> kafkaTemplate;

    @Value("${kafka.topics.shop-registration-requested}")
    private String topic;

    public void send(ShopRegistrationRequestedEvent shopRegistrationRequestedEvent) {
        String key = shopRegistrationRequestedEvent.shopId().toString();
        kafkaTemplate.send(topic, key, shopRegistrationRequestedEvent).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("가게 생성 토픽 발행 실패, key={}", key, ex);
            }
        });
    }
}
