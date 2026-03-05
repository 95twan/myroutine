package com.node5.memberservice.member.infrastructure.kafka.producer;

import com.node5.common.event.ShopDeletionDeadEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShopDeletionDeadProducer {

    private final KafkaTemplate<String, ShopDeletionDeadEvent> kafkaTemplate;

    @Value("${kafka.topics.shop-deletion-dead}")
    private String topic;

    public void sendAndWait(ShopDeletionDeadEvent shopDeletionDeadEvent) {
        String key = shopDeletionDeadEvent.shopId().toString();

        try {
            kafkaTemplate.send(topic, key, shopDeletionDeadEvent).whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("회원 권한 삭제 Dead 토픽 발행 실패, key={}", key, ex);
                }
            }).get();
        } catch (Exception e) {
            throw new RuntimeException("failed to publish ShopDeletionDeadEvent", e);
        }
    }
}
