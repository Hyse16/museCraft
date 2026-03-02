package com.musecraft.settlement.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SettlementScheduler {

    private final JobLauncher jobLauncher;
    private final Job settlementJob;

    /**
     * 매월 1일 자정에 전월 정산 배치 실행
     * cron: 초 분 시 일 월 요일
     */
    @Scheduled(cron = "0 0 0 1 * ?")
    public void runMonthlySettlement() {
        log.info("월별 정산 배치 시작");
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("runTime", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(settlementJob, params);
            log.info("월별 정산 배치 완료");
        } catch (Exception e) {
            log.error("월별 정산 배치 실패: {}", e.getMessage(), e);
        }
    }
}
