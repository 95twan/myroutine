package com.node5.shopservice.shop.infrastructure.kafka.producer;

import com.node5.common.event.ShopRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShopRegisteredProducer {

    private final KafkaTemplate<String, ShopRegisteredEvent> kafkaTemplate;

    @Value("${kafka.topics.shop-registered}")
    private String topic;

    public void send(ShopRegisteredEvent shopRegisteredEvent) {
        String key = shopRegisteredEvent.shopId().toString();
        kafkaTemplate.send(topic, key, shopRegisteredEvent).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("가게 생성 토픽 발행 실패, key={}", key, ex);
            }
        });
    }
}
