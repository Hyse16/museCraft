package com.musecraft.settlement.batch;

import com.musecraft.settlement.domain.Settlement;
import com.musecraft.settlement.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 월말 크리에이터 정산 Spring Batch Job
 *
 * 실행 시점: 매월 1일 00:00 (ScheduledJobLauncher에서 실행)
 * Chunk 크기: 1,000건 (실패 시 해당 청크만 재시도)
 *
 * 처리 흐름:
 *   READ: PENDING 상태 정산 건 1000개씩 조회
 *   PROCESS: 플랫폼 수수료(30%) 계산
 *   WRITE: 정산 완료 처리 + Kafka 이벤트 발행
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class MonthlySettlementJob {

    private static final int CHUNK_SIZE = 1000;
    private static final double PLATFORM_FEE_RATE = 0.30;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final SettlementRepository settlementRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Bean
    public Job settlementJob() {
        return new JobBuilder("monthlySettlementJob", jobRepository)
                .start(settlementStep())
                .build();
    }

    @Bean
    public Step settlementStep() {
        return new StepBuilder("settlementStep", jobRepository)
                .<Settlement, Settlement>chunk(CHUNK_SIZE, transactionManager)
                .reader(settlementReader())
                .processor(settlementProcessor())
                .writer(settlementWriter())
                .faultTolerant()
                .retryLimit(3)
                .retry(Exception.class)
                .build();
    }

    @Bean
    public RepositoryItemReader<Settlement> settlementReader() {
        LocalDate lastMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);

        return new RepositoryItemReaderBuilder<Settlement>()
                .name("settlementReader")
                .repository(settlementRepository)
                .methodName("findAll")
                .pageSize(CHUNK_SIZE)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<Settlement, Settlement> settlementProcessor() {
        return settlement -> {
            // 이미 완료된 정산은 건너뜀
            if (settlement.getStatus().name().equals("COMPLETED")) {
                return null;
            }

            int platformFee = (int) (settlement.getTotalRevenue() * PLATFORM_FEE_RATE);
            int creatorAmount = settlement.getTotalRevenue() - platformFee;

            log.info("정산 처리 - creatorId: {}, revenue: {}, fee: {}, amount: {}",
                    settlement.getCreatorId(), settlement.getTotalRevenue(), platformFee, creatorAmount);

            return Settlement.builder()
                    .id(settlement.getId())
                    .creatorId(settlement.getCreatorId())
                    .brandId(settlement.getBrandId())
                    .settlementMonth(settlement.getSettlementMonth())
                    .totalRevenue(settlement.getTotalRevenue())
                    .platformFee(platformFee)
                    .creatorAmount(creatorAmount)
                    .subscriberCount(settlement.getSubscriberCount())
                    .build();
        };
    }

    @Bean
    public ItemWriter<Settlement> settlementWriter() {
        return items -> {
            for (Settlement settlement : items) {
                settlement.complete();
                settlementRepository.save(settlement);

                // Kafka 이벤트 발행 → Notification Service가 크리에이터에게 이메일 발송
                String event = String.format(
                        "{\"creatorId\":%d,\"amount\":%d,\"period\":\"%s\"}",
                        settlement.getCreatorId(),
                        settlement.getCreatorAmount(),
                        settlement.getSettlementMonth().format(DateTimeFormatter.ofPattern("yyyy년 MM월"))
                );
                kafkaTemplate.send("payment.completed", event);
            }
            log.info("정산 배치 Writer 완료: {}건", items.size());
        };
    }
}
