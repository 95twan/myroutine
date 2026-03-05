package com.node5.shopservice.shop.infrastructure.kafka.consumer;

import com.node5.common.event.ShopDeletionCompletedEvent;
import com.node5.shopservice.shop.application.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShopDeletionCompletedConsumer {

    private final ShopService shopService;

    @KafkaListener(
            topics = "${kafka.topics.shop-deletion-completed}",
            containerFactory = "retryKafkaListenerContainerFactory"
    )
    public void consume(ShopDeletionCompletedEvent event, Acknowledgment ack) {
        shopService.deleteShopCompleted(event.shopId());
        ack.acknowledge();
    }
}
