package com.node5.shopservice.shop.infrastructure.kafka.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.node5.common.event.ShopDeletionRequestedEvent;
import com.node5.shopservice.shop.application.ShopOutboxHandler;
import com.node5.shopservice.shop.domain.ShopOutbox;
import com.node5.shopservice.shop.exception.ShopErrorCode;
import com.node5.shopservice.shop.exception.ShopException;
import com.node5.shopservice.shop.infrastructure.kafka.producer.ShopDeletionRequestedProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShopDeletionRequestedOutboxHandler implements ShopOutboxHandler {

    private final ShopDeletionRequestedProducer shopDeletionRequestedProducer;
    private final ObjectMapper objectMapper;

    @Override
    public String getEventType() {
        return "ShopDeletionRequestedEvent";
    }

    @Override
    public void publish(ShopOutbox outbox) {
        ShopDeletionRequestedEvent event;
        try {
            event = objectMapper.readValue(outbox.getPayload(), ShopDeletionRequestedEvent.class);
        } catch (JsonProcessingException e) {
            throw new ShopException(ShopErrorCode.JSON_PROCESSING_EXCEPTION);
        }
        shopDeletionRequestedProducer.send(event);
    }
}
