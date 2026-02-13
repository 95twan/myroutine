package com.node5.memberservice.member.infrastructure.kafka.producer;

import com.node5.common.event.ShopDeletionCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShopDeletionCompletedProducer {

    private final KafkaTemplate<String, ShopDeletionCompletedEvent> kafkaTemplate;

    @Value("${kafka.topics.shop-deletion-completed}")
    private String topic;

    public void send(ShopDeletionCompletedEvent shopDeletionCompletedEvent) {
        String key = shopDeletionCompletedEvent.shopId().toString();
        kafkaTemplate.send(topic, key, shopDeletionCompletedEvent).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("회원 권한 삭제 성공 토픽 발행 실패, key={}", key, ex);
            }
        });
    }
}
