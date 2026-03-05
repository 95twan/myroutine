package com.node5.memberservice.member.infrastructure.kafka.consumer;

import com.node5.common.event.MemberRoleChangeAction;
import com.node5.common.event.MemberRoleChangeFailedEvent;
import com.node5.common.event.MemberRoleChangeRequestedEvent;
import com.node5.memberservice.member.application.MemberService;
import com.node5.memberservice.member.application.dto.RoleModifyCommand;
import com.node5.memberservice.member.domain.MemberRole;
import com.node5.memberservice.member.exception.MemberException;
import com.node5.memberservice.member.infrastructure.kafka.producer.MemberRoleChangeFailedProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberRoleChangeRequestedConsumer {

    private final MemberService memberService;
    private final MemberRoleChangeFailedProducer memberRoleChangeFailedProducer;

    @KafkaListener(
            topics = "${kafka.topics.member-role-change-requested}",
            containerFactory = "retryKafkaListenerContainerFactory"
    )
    public void consume(MemberRoleChangeRequestedEvent event, Acknowledgment ack) {
        try {
            if (event.action() == MemberRoleChangeAction.ADD_SELLER) {
                memberService.addMemberRole(event, new RoleModifyCommand(MemberRole.SELLER));
            } else if (event.action() == MemberRoleChangeAction.REMOVE_SELLER) {
                memberService.deleteMemberRole(event, new RoleModifyCommand(MemberRole.SELLER));
            }
            ack.acknowledge();
        } catch (MemberException e) {
            MemberRoleChangeFailedEvent memberRoleChangeFailedEvent = new MemberRoleChangeFailedEvent(
                    event.shopId(),
                    event.memberId(),
                    event.sagaType(),
                    e.getErrorCode().getCode(),
                    e.getErrorCode().getMessage()
            );
            memberRoleChangeFailedProducer.sendAndWait(memberRoleChangeFailedEvent);
            ack.acknowledge();
        }
    }
}
