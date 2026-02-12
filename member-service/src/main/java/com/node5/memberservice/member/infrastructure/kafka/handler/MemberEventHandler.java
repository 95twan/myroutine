package com.node5.memberservice.member.infrastructure.kafka.handler;

import com.node5.common.event.MemberDeletedEvent;
import com.node5.common.event.ShopRegistrationCompletedEvent;
import com.node5.memberservice.member.infrastructure.kafka.producer.MemberDeletedProducer;
import com.node5.memberservice.member.infrastructure.kafka.producer.ShopRegistrationCompletedProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class MemberEventHandler {
    private final MemberDeletedProducer memberDeletedProducer;
    private final ShopRegistrationCompletedProducer shopRegistrationCompletedProducer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(MemberDeletedEvent event) {
        memberDeletedProducer.send(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ShopRegistrationCompletedEvent event) {
        shopRegistrationCompletedProducer.send(event);
    }
}
