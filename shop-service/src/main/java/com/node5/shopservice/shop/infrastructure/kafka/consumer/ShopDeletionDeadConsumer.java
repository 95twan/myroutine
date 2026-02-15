package com.node5.shopservice.shop.infrastructure.kafka.consumer;

import com.node5.common.event.ShopDeletionDeadEvent;
import com.node5.shopservice.shop.application.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShopDeletionDeadConsumer {

    private final ShopService shopService;

    @KafkaListener(topics = "${kafka.topics.shop-deletion-dead}")
    public void consume(ShopDeletionDeadEvent event, Acknowledgment ack) {
        shopService.deleteShopDead(event.shopId());
        ack.acknowledge();
    }
}
