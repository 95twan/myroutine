package com.node5.memberservice.member.infrastructure.kafka.consumer;

import com.node5.common.event.ShopDeletionDeadEvent;
import com.node5.common.event.ShopDeletionRequestedEvent;
import com.node5.common.event.ShopRegistrationDeadEvent;
import com.node5.common.event.ShopRegistrationRequestedEvent;
import com.node5.memberservice.member.infrastructure.kafka.producer.ShopDeletionDeadProducer;
import com.node5.memberservice.member.infrastructure.kafka.producer.ShopRegistrationDeadProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShopDLTConsumer {

    private final ShopRegistrationDeadProducer shopRegistrationDeadProducer;
    private final ShopDeletionDeadProducer shopDeletionDeadProducer;

    @KafkaListener(
            topics = "${kafka.topics.shop-registration-requested}.dlt",
            containerFactory = "dltKafkaListenerContainerFactory"
    )
    public void consume(ShopRegistrationRequestedEvent event, Acknowledgment ack) {

        ShopRegistrationDeadEvent shopRegistrationDeadEvent = new ShopRegistrationDeadEvent(event.shopId());
        shopRegistrationDeadProducer.sendAndWait(shopRegistrationDeadEvent);
        ack.acknowledge();

    }

    @KafkaListener(
            topics = "${kafka.topics.shop-deletion-requested}.dlt",
            containerFactory = "dltKafkaListenerContainerFactory"
    )
    public void consume(ShopDeletionRequestedEvent event, Acknowledgment ack) {

        ShopDeletionDeadEvent shopDeletionDeadEvent = new ShopDeletionDeadEvent(event.shopId());
        shopDeletionDeadProducer.sendAndWait(shopDeletionDeadEvent);
        ack.acknowledge();

    }
}
