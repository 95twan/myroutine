package com.node5.memberservice.member.infrastructure.kafka.producer;

import com.node5.common.event.ShopRegistrationFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShopRegistrationFailedProducer {

    private final KafkaTemplate<String, ShopRegistrationFailedEvent> kafkaTemplate;

    @Value("${kafka.topics.shop-registration-failed}")
    private String topic;

    public void send(ShopRegistrationFailedEvent shopRegistrationFailedEvent) {
        String key = shopRegistrationFailedEvent.shopId().toString();
        kafkaTemplate.send(topic, key, shopRegistrationFailedEvent).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("회원 권한 추가 실패 토픽 발행 실패, key={}", key, ex);
            }
        });
    }
}
