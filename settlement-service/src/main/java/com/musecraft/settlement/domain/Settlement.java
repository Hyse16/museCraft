package com.musecraft.settlement.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "settlements",
        indexes = @Index(name = "idx_creator_period", columnList = "creatorId, settlementMonth"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Settlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long creatorId;

    @Column(nullable = false)
    private Long brandId;

    @Column(nullable = false)
    private LocalDate settlementMonth;   // 정산 월 (예: 2026-02-01)

    @Column(nullable = false)
    private int totalRevenue;            // 해당 월 총 수익 (원)

    @Column(nullable = false)
    private int platformFee;             // 플랫폼 수수료 (30%)

    @Column(nullable = false)
    private int creatorAmount;           // 크리에이터 지급액 (70%)

    @Column(nullable = false)
    private int subscriberCount;         // 해당 월 구독자 수

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SettlementStatus status = SettlementStatus.PENDING;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public void complete() {
        this.status = SettlementStatus.COMPLETED;
    }
}
