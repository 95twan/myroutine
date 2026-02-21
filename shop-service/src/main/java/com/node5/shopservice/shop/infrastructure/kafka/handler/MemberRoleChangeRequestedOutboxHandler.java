package com.node5.shopservice.shop.infrastructure.kafka.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.node5.common.event.MemberRoleChangeRequestedEvent;
import com.node5.shopservice.shop.application.ShopOutboxHandler;
import com.node5.shopservice.shop.domain.ShopOutbox;
import com.node5.shopservice.shop.exception.ShopErrorCode;
import com.node5.shopservice.shop.exception.ShopException;
import com.node5.shopservice.shop.infrastructure.kafka.producer.MemberRoleChangeRequestedProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberRoleChangeRequestedOutboxHandler implements ShopOutboxHandler {

    private final MemberRoleChangeRequestedProducer producer;
    private final ObjectMapper objectMapper;

    @Override
    public String getEventType() {
        return "MemberRoleChangeRequestedEvent";
    }

    @Override
    public void publish(ShopOutbox outbox) {
        MemberRoleChangeRequestedEvent event;
        try {
            event = objectMapper.readValue(outbox.getPayload(), MemberRoleChangeRequestedEvent.class);
        } catch (JsonProcessingException e) {
            throw new ShopException(ShopErrorCode.JSON_PROCESSING_EXCEPTION);
        }
        producer.send(event);
    }
}
