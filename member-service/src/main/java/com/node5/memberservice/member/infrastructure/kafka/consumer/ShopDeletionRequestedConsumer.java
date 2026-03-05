package com.node5.memberservice.member.infrastructure.kafka.consumer;

import com.node5.common.event.ShopDeletionFailedEvent;
import com.node5.common.event.ShopDeletionRequestedEvent;
import com.node5.memberservice.member.application.MemberService;
import com.node5.memberservice.member.application.dto.RoleModifyCommand;
import com.node5.memberservice.member.domain.MemberRole;
import com.node5.memberservice.member.infrastructure.kafka.producer.ShopDeletionFailedProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShopDeletionRequestedConsumer {

    private final MemberService memberService;
    private final ShopDeletionFailedProducer shopDeletionFailedProducer;

    @KafkaListener(topics = "${kafka.topics.shop-deletion-requested}")
    public void consume(ShopDeletionRequestedEvent event, Acknowledgment ack) {
        try {
            memberService.deleteMemberRole(event.memberId(), event.shopId(), new RoleModifyCommand(MemberRole.SELLER));
        } catch (Exception e) {
            ShopDeletionFailedEvent shopDeletionFailedEvent = new ShopDeletionFailedEvent(event.shopId());
            shopDeletionFailedProducer.send(shopDeletionFailedEvent);
        } finally {
            ack.acknowledge();
        }
    }
}
