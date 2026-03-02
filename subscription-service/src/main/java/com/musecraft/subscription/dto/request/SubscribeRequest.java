package com.musecraft.subscription.dto.request;

import com.musecraft.subscription.domain.SubscriptionTier;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SubscribeRequest {

    @NotNull(message = "브랜드 ID는 필수입니다.")
    private Long brandId;

    @NotNull(message = "구독 티어는 필수입니다.")
    private SubscriptionTier tier;

    private String impUid;  // 포트원 결제 고유번호 (FREE 티어 제외)
}
