package com.node5.memberservice.member.outbox;

import com.node5.memberservice.member.application.MemberOutboxService;
import com.node5.memberservice.member.domain.MemberOutbox;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MemberOutboxJobRunner {

    private final MemberOutboxService memberOutboxService;

    public int runOnce() {
        List<MemberOutbox> outboxes =
                memberOutboxService.claimReadyBatch(PageRequest.of(0, 100));

        int processed = 0;
        for (MemberOutbox outbox : outboxes) {
            memberOutboxService.publish(outbox);
            processed++;
        }
        return processed;
    }
}
