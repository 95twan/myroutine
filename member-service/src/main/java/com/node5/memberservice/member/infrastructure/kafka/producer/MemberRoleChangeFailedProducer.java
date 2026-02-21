package com.node5.memberservice.member.infrastructure.kafka.producer;

import com.node5.common.event.MemberRoleChangeFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberRoleChangeFailedProducer {

    private final KafkaTemplate<String, MemberRoleChangeFailedEvent> kafkaTemplate;

    @Value("${kafka.topics.member-role-change-failed}")
    private String topic;

    public void sendAndWait(MemberRoleChangeFailedEvent memberRoleChangeFailedEvent) {
        String key = memberRoleChangeFailedEvent.memberId().toString();
        try {
            kafkaTemplate.send(topic, key, memberRoleChangeFailedEvent).get(3, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("회원 권한 변경 실패 토픽 발행 실패, key={}", key, e);
            throw new RuntimeException("failed to publish MemberRoleChangeFailedEvent", e);
        }
    }
}
