package com.node5.memberservice.member.infrastructure.kafka.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.node5.common.event.ShopRegistrationCompletedEvent;
import com.node5.memberservice.member.application.MemberOutboxHandler;
import com.node5.memberservice.member.domain.MemberOutbox;
import com.node5.memberservice.member.exception.MemberErrorCode;
import com.node5.memberservice.member.exception.MemberException;
import com.node5.memberservice.member.infrastructure.kafka.producer.ShopRegistrationCompletedProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShopRegistrationCompletedOutboxHandler implements MemberOutboxHandler {

    private final ShopRegistrationCompletedProducer shopRegistrationCompletedProducer;
    private final ObjectMapper objectMapper;

    @Override
    public String getEventType() {
        return "ShopRegistrationCompletedEvent";
    }

    @Override
    public void publish(MemberOutbox outbox) {
        ShopRegistrationCompletedEvent event;
        try {
            event = objectMapper.readValue(outbox.getPayload(), ShopRegistrationCompletedEvent.class);
        } catch (JsonProcessingException e) {
            throw new MemberException(MemberErrorCode.JSON_PROCESSING_EXCEPTION);
        }
        shopRegistrationCompletedProducer.send(event);
    }
}
