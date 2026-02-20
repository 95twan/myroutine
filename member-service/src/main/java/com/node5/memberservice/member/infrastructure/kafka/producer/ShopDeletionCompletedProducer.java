package com.node5.memberservice.member.infrastructure.kafka.producer;

import com.node5.common.event.ShopDeletionCompletedEvent;
import com.node5.memberservice.member.exception.MemberErrorCode;
import com.node5.memberservice.member.exception.MemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShopDeletionCompletedProducer {

    private final KafkaTemplate<String, ShopDeletionCompletedEvent> kafkaTemplate;

    @Value("${kafka.topics.shop-deletion-completed}")
    private String topic;

    public void send(ShopDeletionCompletedEvent shopDeletionCompletedEvent) {
        String key = shopDeletionCompletedEvent.shopId().toString();
        try {
            kafkaTemplate.send(topic, key, shopDeletionCompletedEvent).get(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MemberException(MemberErrorCode.KAFKA_EVENT_SEND_FAIL);
        } catch (ExecutionException | TimeoutException e) {
            log.error("회원 권한 삭제 성공 토픽 발행 실패, key={}", key, e);
            throw new MemberException(MemberErrorCode.KAFKA_EVENT_SEND_FAIL);
        }
    }
}
