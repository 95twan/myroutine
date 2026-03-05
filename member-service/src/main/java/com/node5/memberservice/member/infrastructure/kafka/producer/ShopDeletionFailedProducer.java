package com.node5.memberservice.member.infrastructure.kafka.producer;

import com.node5.common.event.ShopDeletionFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShopDeletionFailedProducer {

    private final KafkaTemplate<String, ShopDeletionFailedEvent> kafkaTemplate;

    @Value("${kafka.topics.shop-deletion-failed}")
    private String topic;

    public void send(ShopDeletionFailedEvent shopDeletionFailedEvent) {
        String key = shopDeletionFailedEvent.shopId().toString();
        kafkaTemplate.send(topic, key, shopDeletionFailedEvent).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("회원 권한 삭제 실패 토픽 발행 실패, key={}", key, ex);
            }
        });
    }
}
