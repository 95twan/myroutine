package com.node5.shopservice.shop.infrastructure.kafka.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.node5.common.event.ShopRegistrationRequestedEvent;
import com.node5.shopservice.shop.application.ShopOutboxHandler;
import com.node5.shopservice.shop.domain.ShopOutbox;
import com.node5.shopservice.shop.exception.ShopErrorCode;
import com.node5.shopservice.shop.exception.ShopException;
import com.node5.shopservice.shop.infrastructure.kafka.producer.ShopRegistrationRequestedProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShopRegistrationRequestedOutboxHandler implements ShopOutboxHandler {

    private final ShopRegistrationRequestedProducer shopRegistrationRequestedProducer;
    private final ObjectMapper objectMapper;

    @Override
    public String getEventType() {
        return "ShopRegistrationRequestedEvent";
    }

    @Override
    public void publish(ShopOutbox outbox) {
        ShopRegistrationRequestedEvent event;
        try {
            event = objectMapper.readValue(outbox.getPayload(), ShopRegistrationRequestedEvent.class);
        } catch (JsonProcessingException e) {
            throw new ShopException(ShopErrorCode.JSON_PROCESSING_EXCEPTION);
        }
        shopRegistrationRequestedProducer.send(event);
    }
}
