package com.node5.memberservice.member.infrastructure.kafka.consumer;

import com.node5.common.event.ShopDeletionDeadEvent;
import com.node5.common.event.ShopDeletionRequestedEvent;
import com.node5.common.event.ShopRegistrationDeadEvent;
import com.node5.common.event.ShopRegistrationRequestedEvent;
import com.node5.memberservice.member.infrastructure.kafka.producer.ShopDeletionDeadProducer;
import com.node5.memberservice.member.infrastructure.kafka.producer.ShopRegistrationDeadProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

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
    public void consume(ShopRegistrationRequestedEvent event, Acknowledgment ack, ConsumerRecord<String, ShopRegistrationRequestedEvent> record) {

        String reasonCode  = "DLT_EXHAUSTED";
        String reasonMessage = header(record, KafkaHeaders.DLT_EXCEPTION_MESSAGE);

        ShopRegistrationDeadEvent shopRegistrationDeadEvent = new ShopRegistrationDeadEvent(event.shopId(), reasonCode, reasonMessage);
        shopRegistrationDeadProducer.sendAndWait(shopRegistrationDeadEvent);
        ack.acknowledge();

    }

    @KafkaListener(
            topics = "${kafka.topics.shop-deletion-requested}.dlt",
            containerFactory = "dltKafkaListenerContainerFactory"
    )
    public void consume(ShopDeletionRequestedEvent event, Acknowledgment ack, ConsumerRecord<String, ShopDeletionRequestedEvent> record) {

        String reasonCode  = "DLT_EXHAUSTED";
        String reasonMessage = header(record, KafkaHeaders.DLT_EXCEPTION_MESSAGE);

        ShopDeletionDeadEvent shopDeletionDeadEvent = new ShopDeletionDeadEvent(event.shopId(), reasonCode, reasonMessage);
        shopDeletionDeadProducer.sendAndWait(shopDeletionDeadEvent);
        ack.acknowledge();

    }

    private String header(ConsumerRecord<?, ?> record, String key) {
        Header h = record.headers().lastHeader(key);
        return h == null ? null : new String(h.value(), StandardCharsets.UTF_8);
    }
}
