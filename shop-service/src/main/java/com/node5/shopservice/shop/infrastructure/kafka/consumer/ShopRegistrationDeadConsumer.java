package com.node5.shopservice.shop.infrastructure.kafka.consumer;

import com.node5.common.event.ShopRegistrationDeadEvent;
import com.node5.shopservice.shop.application.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShopRegistrationDeadConsumer {

    private final ShopService shopService;

    @KafkaListener(topics = "${kafka.topics.shop-registration-dead}")
    public void consume(ShopRegistrationDeadEvent event, Acknowledgment ack) {
        shopService.registerShopDead(event.shopId());
        ack.acknowledge();
    }
}
