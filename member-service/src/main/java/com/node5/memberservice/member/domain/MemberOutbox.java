package com.node5.memberservice.member.domain;

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
@Table(name = "\"member_outbox\"", schema = "member")
public class MemberOutbox {

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
    private MemberOutboxStatus status;

    @Column(name = "retry_count", nullable = false)
    private Integer retryCount;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    private MemberOutbox(UUID id, String eventType, UUID eventKey, String payload, MemberOutboxStatus status, Integer retryCount) {
        this.id = id;
        this.eventType = eventType;
        this.eventKey = eventKey;
        this.payload = payload;
        this.status = status;
        this.retryCount = retryCount;
    }

    public static MemberOutbox create(String eventType, UUID eventKey, String payload) {
        return new MemberOutbox(UUID.randomUUID(), eventType, eventKey, payload, MemberOutboxStatus.READY, 0);
    }

    public void incrementRetryCount() {
        this.retryCount++;
    }

    public void memberOutboxReady() {
        this.status = MemberOutboxStatus.READY;
    }

    public void memberOutboxProcessing() {
        this.status = MemberOutboxStatus.PROCESSING;
    }

    public void memberOutboxSent() {
        this.status = MemberOutboxStatus.SENT;
    }

    public void memberOutboxFailed() {
        this.status = MemberOutboxStatus.FAILED;
    }

}
