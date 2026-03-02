package com.musecraft.subscription.service;

import com.musecraft.common.exception.BusinessException;
import com.musecraft.common.exception.ErrorCode;
import com.musecraft.subscription.domain.Subscription;
import com.musecraft.subscription.domain.SubscriptionStatus;
import com.musecraft.subscription.domain.SubscriptionTier;
import com.musecraft.subscription.dto.request.SubscribeRequest;
import com.musecraft.subscription.dto.response.SubscriptionResponse;
import com.musecraft.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionService {

    // 티어별 월 요금 (원)
    private static final Map<SubscriptionTier, Integer> TIER_PRICE = Map.of(
            SubscriptionTier.FREE, 0,
            SubscriptionTier.BASIC, 9900,
            SubscriptionTier.PREMIUM, 19900
    );

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionCacheService cacheService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Transactional
    public SubscriptionResponse subscribe(Long subscriberId, SubscribeRequest request) {
        // 중복 구독 체크
        subscriptionRepository.findBySubscriberIdAndBrandIdAndStatus(
                subscriberId, request.getBrandId(), SubscriptionStatus.ACTIVE)
                .ifPresent(s -> { throw new BusinessException(ErrorCode.PAYMENT_FAILED,
                        "이미 해당 브랜드를 구독 중입니다."); });

        // 무료 티어는 결제 검증 생략
        if (request.getTier() != SubscriptionTier.FREE) {
            // TODO: 포트원(아임포트) 결제 검증 로직 연동
            log.info("결제 검증 - impUid: {}, brandId: {}, tier: {}",
                    request.getImpUid(), request.getBrandId(), request.getTier());
        }

        LocalDateTime now = LocalDateTime.now();
        Subscription subscription = Subscription.builder()
                .subscriberId(subscriberId)
                .brandId(request.getBrandId())
                .tier(request.getTier())
                .startedAt(now)
                .expiresAt(now.plusMonths(1))
                .priceKrw(TIER_PRICE.get(request.getTier()))
                .build();

        Subscription saved = subscriptionRepository.save(subscription);

        // 캐시 무효화
        cacheService.evictCache(subscriberId, request.getBrandId());

        return SubscriptionResponse.from(saved);
    }

    @Transactional
    public void cancel(Long subscriberId, Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .filter(s -> s.getSubscriberId().equals(subscriberId))
                .orElseThrow(() -> new BusinessException(ErrorCode.SUBSCRIPTION_NOT_FOUND));

        subscription.cancel();

        // 캐시 무효화
        cacheService.evictCache(subscriberId, subscription.getBrandId());

        // Kafka 이벤트 발행
        kafkaTemplate.send("subscription.expired",
                String.format("{\"subscriberId\":%d,\"brandId\":%d}", subscriberId, subscription.getBrandId()));
    }

    public List<SubscriptionResponse> getMySubscriptions(Long subscriberId) {
        return subscriptionRepository.findBySubscriberId(subscriberId)
                .stream()
                .map(SubscriptionResponse::from)
                .collect(Collectors.toList());
    }

    public long getSubscriberCount(Long brandId) {
        return subscriptionRepository.countActiveByBrandId(brandId);
    }
}
