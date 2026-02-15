package com.node5.memberservice.member.infrastructure.kafka.producer;

import com.node5.common.event.ShopRegistrationDeadEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShopRegistrationDeadProducer {

    private final KafkaTemplate<String, ShopRegistrationDeadEvent> kafkaTemplate;

    @Value("${kafka.topics.shop-registration-dead}")
    private String topic;

    public void sendAndWait(ShopRegistrationDeadEvent shopRegistrationDeadEvent) {
        String key = shopRegistrationDeadEvent.shopId().toString();

        try {
            kafkaTemplate.send(topic, key, shopRegistrationDeadEvent).whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("회원 권한 추가 Dead 토픽 발행 실패, key={}", key, ex);
                }
            }).get();
        } catch (Exception e) {
            throw new RuntimeException("failed to publish ShopRegistrationDeadEvent", e);
        }
    }
}
