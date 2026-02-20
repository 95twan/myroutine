package com.node5.memberservice.member.infrastructure.kafka.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.node5.common.event.ShopDeletionCompletedEvent;
import com.node5.memberservice.member.application.MemberOutboxHandler;
import com.node5.memberservice.member.domain.MemberOutbox;
import com.node5.memberservice.member.exception.MemberErrorCode;
import com.node5.memberservice.member.exception.MemberException;
import com.node5.memberservice.member.infrastructure.kafka.producer.ShopDeletionCompletedProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShopDeletionCompletedOutboxHandler implements MemberOutboxHandler {

    private final ShopDeletionCompletedProducer shopDeletionCompletedProducer;
    private final ObjectMapper objectMapper;

    @Override
    public String getEventType() {
        return "ShopDeletionCompletedEvent";
    }

    @Override
    public void publish(MemberOutbox outbox) {
        ShopDeletionCompletedEvent event;
        try {
            event = objectMapper.readValue(outbox.getPayload(), ShopDeletionCompletedEvent.class);
        } catch (JsonProcessingException e) {
            throw new MemberException(MemberErrorCode.JSON_PROCESSING_EXCEPTION);
        }
        shopDeletionCompletedProducer.send(event);
    }
}
