package com.node5.memberservice.member.application;

import com.node5.memberservice.member.domain.MemberOutbox;
import com.node5.memberservice.member.domain.MemberOutboxRepository;
import com.node5.memberservice.member.domain.MemberOutboxStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class MemberOutboxService {

    private final MemberOutboxRepository memberOutboxRepository;
    private final Map<String, MemberOutboxHandler> memberOutboxHandlerMap;

    public MemberOutboxService(
            MemberOutboxRepository memberOutboxRepository,
            List<MemberOutboxHandler> memberOutboxHandlerList
    ) {
        this.memberOutboxRepository = memberOutboxRepository;
        this.memberOutboxHandlerMap = memberOutboxHandlerList.stream().collect(Collectors.toMap(MemberOutboxHandler::getEventType, handler -> handler));

    }

    @Transactional
    public List<MemberOutbox> claimReadyBatch(Pageable pageable) {
        List<MemberOutbox> memberOutboxes = memberOutboxRepository.getBatchForUpdateByStatus(MemberOutboxStatus.READY, pageable);
        for (MemberOutbox memberOutbox : memberOutboxes) {
            memberOutbox.memberOutboxProcessing();
        }

        return memberOutboxes;
    }

    @Transactional
    public void publish(MemberOutbox memberOutbox) {
        MemberOutboxHandler memberOutboxHandler = memberOutboxHandlerMap.get(memberOutbox.getEventType());

        if (memberOutboxHandler == null) {
            memberOutbox.incrementRetryCount();

            if (memberOutbox.getRetryCount() >= 3) {
                memberOutbox.memberOutboxFailed();
            } else {
                memberOutbox.memberOutboxReady();
            }

            memberOutboxRepository.save(memberOutbox);
            return;
        }

        try {
            memberOutboxHandler.publish(memberOutbox);
            memberOutbox.memberOutboxSent();
        } catch (Exception e) {
            memberOutbox.incrementRetryCount();

            if (memberOutbox.getRetryCount() >= 3) {
                memberOutbox.memberOutboxFailed();
            } else {
                memberOutbox.memberOutboxReady();
            }
        }

        memberOutboxRepository.save(memberOutbox);
    }
}
