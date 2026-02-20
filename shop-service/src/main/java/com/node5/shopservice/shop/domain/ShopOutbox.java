package com.node5.shopservice.shop.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "\"shop_outbox\"", schema = "shop")
public class ShopOutbox {

    @Id
    private UUID id;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "event_key", nullable = false)
    private UUID eventKey;

    @Column(columnDefinition = "text", nullable = false)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ShopOutboxStatus status;

    @Column(name = "retry_count", nullable = false)
    private Integer retryCount;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    private ShopOutbox(UUID id, String eventType, UUID eventKey, String payload, ShopOutboxStatus status, Integer retryCount) {
        this.id = id;
        this.eventType = eventType;
        this.eventKey = eventKey;
        this.payload = payload;
        this.status = status;
        this.retryCount = retryCount;
    }

    public static ShopOutbox create(String eventType, UUID eventKey, String payload) {
        return new ShopOutbox(UUID.randomUUID(), eventType, eventKey, payload, ShopOutboxStatus.READY, 0);
    }

    public void incrementRetryCount() {
        this.retryCount++;
    }

    public void shopOutboxReady() {
        this.status = ShopOutboxStatus.READY;
    }

    public void shopOutboxProcessing() {
        this.status = ShopOutboxStatus.PROCESSING;
    }

    public void shopOutboxSent() {
        this.status = ShopOutboxStatus.SENT;
    }

    public void shopOutboxFailed() {
        this.status = ShopOutboxStatus.FAILED;
    }

}
