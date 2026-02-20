package com.node5.memberservice.member.outbox;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberOutboxScheduler {

    private final TaskScheduler taskScheduler;
    private final MemberOutboxJobRunner jobRunner;
    private final OutboxBackoffPolicy backoffPolicy;

    private int emptyStreak = 0;

    @PostConstruct
    public void start() {
        scheduleNext(500);
    }

    private void scheduleNext(long delayMs) {
        taskScheduler.schedule(this::runAndReschedule, Instant.now().plusMillis(delayMs));
    }

    private void runAndReschedule() {
        int processed = 0;
        try {
            processed = jobRunner.runOnce();
        } catch (Exception e) {
            log.error("member outbox run failed", e);
        }

        emptyStreak = backoffPolicy.nextEmptyStreak(processed, emptyStreak);
        long nextDelay = backoffPolicy.nextDelayMs(processed, emptyStreak);
        scheduleNext(nextDelay);
    }

}
