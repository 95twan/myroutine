package com.node5.memberservice.member.infrastructure.kafka.consumer;

import com.node5.common.event.ShopRegistrationRequestedEvent;
import com.node5.common.event.ShopRegistrationFailedEvent;
import com.node5.memberservice.member.application.MemberService;
import com.node5.memberservice.member.application.dto.RoleModifyCommand;
import com.node5.memberservice.member.domain.MemberRole;
import com.node5.memberservice.member.infrastructure.kafka.producer.ShopRegistrationFailedProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShopRegistrationRequestedConsumer {

    private final MemberService memberService;
    private final ShopRegistrationFailedProducer shopRegistrationFailedProducer;

    @KafkaListener(topics = "${kafka.topics.shop-registration-requested}")
    public void consume(ShopRegistrationRequestedEvent event, Acknowledgment ack) {
        try {
            memberService.addMemberRole(event.memberId(), event.shopId(), new RoleModifyCommand(MemberRole.SELLER));
        } catch (Exception e) {
            ShopRegistrationFailedEvent shopRegistrationFailedEvent = new ShopRegistrationFailedEvent(event.shopId());
            shopRegistrationFailedProducer.send(shopRegistrationFailedEvent);
        } finally {
            ack.acknowledge();
        }
    }
}
