package com.node5.shopservice.shop.infrastructure.kafka.producer;

import com.node5.common.event.ShopRegistrationRequestedEvent;
import com.node5.shopservice.shop.exception.ShopErrorCode;
import com.node5.shopservice.shop.exception.ShopException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShopRegistrationRequestedProducer {

    private final KafkaTemplate<String, ShopRegistrationRequestedEvent> kafkaTemplate;

    @Value("${kafka.topics.shop-registration-requested}")
    private String topic;

    public void send(ShopRegistrationRequestedEvent shopRegistrationRequestedEvent) {
        String key = shopRegistrationRequestedEvent.shopId().toString();
        try {
            kafkaTemplate.send(topic, key, shopRegistrationRequestedEvent).get(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ShopException(ShopErrorCode.KAFKA_EVENT_SEND_FAIL);
        } catch (ExecutionException | TimeoutException e) {
            log.error("가게 생성 토픽 발행 실패, key={}", key, e);
            throw new ShopException(ShopErrorCode.KAFKA_EVENT_SEND_FAIL);
        }

    }
}
