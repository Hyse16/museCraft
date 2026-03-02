package com.musecraft.brand.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "brands")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long creatorId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 300)
    private String slogan;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String brandStory;

    // 컬러 팔레트: {"primary": "#7c3aed", "secondary": "#06b6d4", "accent": "#3fb950"}
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, String> colorPalette;

    @Column(length = 500)
    private String logoUrl;

    @Column(length = 500)
    private String keywords;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BrandStatus status = BrandStatus.PENDING;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void activate() {
        this.status = BrandStatus.ACTIVE;
    }

    public void suspend() {
        this.status = BrandStatus.SUSPENDED;
    }

    public void updateLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
}
