package com.node5.shopservice.shop.infrastructure.kafka.producer;

import com.node5.common.event.ShopDeletionRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShopDeletionRequestedProducer {

    private final KafkaTemplate<String, ShopDeletionRequestedEvent> kafkaTemplate;

    @Value("${kafka.topics.shop-deletion-requested}")
    private String topic;

    public void send(ShopDeletionRequestedEvent shopDeletionRequestedEvent) {
        String key = shopDeletionRequestedEvent.shopId().toString();
        kafkaTemplate.send(topic, key, shopDeletionRequestedEvent).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("가게 삭제 토픽 발행 실패, key={}", key, ex);
            }
        });
    }
}
