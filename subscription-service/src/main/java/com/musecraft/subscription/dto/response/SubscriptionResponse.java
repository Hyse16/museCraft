package com.musecraft.subscription.dto.response;

import com.musecraft.subscription.domain.Subscription;
import com.musecraft.subscription.domain.SubscriptionStatus;
import com.musecraft.subscription.domain.SubscriptionTier;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SubscriptionResponse {
    private Long id;
    private Long subscriberId;
    private Long brandId;
    private SubscriptionTier tier;
    private SubscriptionStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime expiresAt;
    private int priceKrw;

    public static SubscriptionResponse from(Subscription subscription) {
        return SubscriptionResponse.builder()
                .id(subscription.getId())
                .subscriberId(subscription.getSubscriberId())
                .brandId(subscription.getBrandId())
                .tier(subscription.getTier())
                .status(subscription.getStatus())
                .startedAt(subscription.getStartedAt())
                .expiresAt(subscription.getExpiresAt())
                .priceKrw(subscription.getPriceKrw())
                .build();
    }
}
