package com.node5.memberservice.member.infrastructure.kafka.producer;

import com.node5.common.event.MemberRoleChangeCompletedEvent;
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
public class MemberRoleChangeCompletedProducer {

    private final KafkaTemplate<String, MemberRoleChangeCompletedEvent> kafkaTemplate;

    @Value("${kafka.topics.member-role-change-completed}")
    private String topic;

    public void send(MemberRoleChangeCompletedEvent memberRoleChangeCompletedEvent) {
        String key = memberRoleChangeCompletedEvent.memberId().toString();
        try {
            kafkaTemplate.send(topic, key, memberRoleChangeCompletedEvent).get(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MemberException(MemberErrorCode.KAFKA_EVENT_SEND_FAIL);
        } catch (ExecutionException | TimeoutException e) {
            log.error("회원 권한 변경 성공 토픽 발행 실패, key={}", key, e);
            throw new MemberException(MemberErrorCode.KAFKA_EVENT_SEND_FAIL);
        }
    }
}
