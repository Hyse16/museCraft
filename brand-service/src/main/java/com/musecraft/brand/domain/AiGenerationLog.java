package com.musecraft.brand.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_generation_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class AiGenerationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long brandId;

    @Column(nullable = false, length = 50)
    private String model;           // claude-sonnet-4-6

    @Column(nullable = false)
    private int inputTokens;

    @Column(nullable = false)
    private int outputTokens;

    @Column(nullable = false, length = 50)
    private String generationType;  // BRAND_IDENTITY, NEWSLETTER_DRAFT

    @CreationTimestamp
    private LocalDateTime createdAt;
}
