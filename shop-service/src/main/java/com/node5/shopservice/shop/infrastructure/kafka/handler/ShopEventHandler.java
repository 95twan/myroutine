package com.node5.shopservice.shop.infrastructure.kafka.handler;

import com.node5.common.event.ShopDeletedEvent;
import com.node5.common.event.ShopRegistrationRequestedEvent;
import com.node5.shopservice.shop.infrastructure.kafka.producer.ShopDeletedProducer;
import com.node5.shopservice.shop.infrastructure.kafka.producer.ShopRegistrationRequestedProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ShopEventHandler {
    private final ShopDeletedProducer shopDeletedProducer;
    private final ShopRegistrationRequestedProducer shopRegistrationRequestedProducer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ShopDeletedEvent event) {
        shopDeletedProducer.send(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    private void handle(ShopRegistrationRequestedEvent event) { shopRegistrationRequestedProducer.send(event);}
}
