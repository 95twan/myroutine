package com.node5.memberservice.member.infrastructure.kafka.consumer;

import com.node5.common.event.MemberRoleChangeDeadEvent;
import com.node5.common.event.MemberRoleChangeRequestedEvent;
import com.node5.memberservice.member.infrastructure.kafka.producer.MemberRoleChangeDeadProducer;
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

    private final MemberRoleChangeDeadProducer memberRoleChangeDeadProducer;

    @KafkaListener(
            topics = "${kafka.topics.member-role-change-requested}.dlt",
            containerFactory = "dltKafkaListenerContainerFactory"
    )
    public void consume(MemberRoleChangeRequestedEvent event, Acknowledgment ack, ConsumerRecord<String, MemberRoleChangeRequestedEvent> record) {
        String reasonCode = "DLT_EXHAUSTED";
        String reasonMessage = header(record, KafkaHeaders.DLT_EXCEPTION_MESSAGE);
        MemberRoleChangeDeadEvent memberRoleChangeDeadEvent = new MemberRoleChangeDeadEvent(
                event.shopId(),
                event.memberId(),
                event.sagaType(),
                reasonCode,
                reasonMessage
        );
        memberRoleChangeDeadProducer.sendAndWait(memberRoleChangeDeadEvent);

        ack.acknowledge();
    }

    private String header(ConsumerRecord<?, ?> record, String key) {
        Header h = record.headers().lastHeader(key);
        return h == null ? null : new String(h.value(), StandardCharsets.UTF_8);
    }
}
