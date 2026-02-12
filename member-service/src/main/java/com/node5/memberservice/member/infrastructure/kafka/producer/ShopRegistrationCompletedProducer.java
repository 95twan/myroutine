package com.node5.memberservice.member.infrastructure.kafka.producer;

import com.node5.common.event.ShopRegistrationCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShopRegistrationCompletedProducer {

    private final KafkaTemplate<String, ShopRegistrationCompletedEvent> kafkaTemplate;

    @Value("${kafka.topics.shop-registration-completed}")
    private String topic;

    public void send(ShopRegistrationCompletedEvent shopRegistrationCompletedEvent) {
        String key = shopRegistrationCompletedEvent.shopId().toString();
        kafkaTemplate.send(topic, key, shopRegistrationCompletedEvent).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("회원 권한 추가 성공 토픽 발행 실패, key={}", key, ex);
            }
        });
    }
}
