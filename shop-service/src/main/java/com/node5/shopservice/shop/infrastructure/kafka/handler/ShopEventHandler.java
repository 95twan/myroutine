package com.node5.shopservice.shop.infrastructure.kafka.handler;

import com.node5.common.event.ShopDeletedEvent;
import com.node5.common.event.ShopRegisteredEvent;
import com.node5.shopservice.shop.infrastructure.kafka.producer.ShopDeletedProducer;
import com.node5.shopservice.shop.infrastructure.kafka.producer.ShopRegisteredProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ShopEventHandler {
    private final ShopDeletedProducer shopDeletedProducer;
    private final ShopRegisteredProducer shopRegisteredProducer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ShopDeletedEvent event) {
        shopDeletedProducer.send(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    private void handle(ShopRegisteredEvent event) { shopRegisteredProducer.send(event);}
}
