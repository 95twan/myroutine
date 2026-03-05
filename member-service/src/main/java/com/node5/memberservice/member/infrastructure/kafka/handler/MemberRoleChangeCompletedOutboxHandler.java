package com.node5.memberservice.member.infrastructure.kafka.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.node5.common.event.MemberRoleChangeCompletedEvent;
import com.node5.memberservice.member.application.MemberOutboxHandler;
import com.node5.memberservice.member.domain.MemberOutbox;
import com.node5.memberservice.member.exception.MemberErrorCode;
import com.node5.memberservice.member.exception.MemberException;
import com.node5.memberservice.member.infrastructure.kafka.producer.MemberRoleChangeCompletedProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberRoleChangeCompletedOutboxHandler implements MemberOutboxHandler {

    private final MemberRoleChangeCompletedProducer producer;
    private final ObjectMapper objectMapper;

    @Override
    public String getEventType() {
        return "MemberRoleChangeCompletedEvent";
    }

    @Override
    public void publish(MemberOutbox outbox) {
        MemberRoleChangeCompletedEvent event;
        try {
            event = objectMapper.readValue(outbox.getPayload(), MemberRoleChangeCompletedEvent.class);
        } catch (JsonProcessingException e) {
            throw new MemberException(MemberErrorCode.JSON_PROCESSING_EXCEPTION);
        }
        producer.send(event);
    }
}
