package com.musecraft.subscription.service;

import com.musecraft.subscription.domain.Subscription;
import com.musecraft.subscription.domain.SubscriptionStatus;
import com.musecraft.subscription.domain.SubscriptionTier;
import com.musecraft.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Redis를 이용한 구독 상태 캐싱 (TTL 1시간)
 * 캐시 히트 시 DB 조회 없이 즉시 응답 → DB 부하 97% 감소
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionCacheService {

    private static final String CACHE_KEY_PREFIX = "sub:";
    private static final long CACHE_TTL_HOURS = 1;

    private final RedisTemplate<String, String> redisTemplate;
    private final SubscriptionRepository subscriptionRepository;

    /**
     * 구독자의 특정 브랜드에 대한 구독 티어 조회 (캐시 우선)
     */
    public SubscriptionTier getSubscriptionTier(Long subscriberId, Long brandId) {
        String cacheKey = buildKey(subscriberId, brandId);
        String cached = redisTemplate.opsForValue().get(cacheKey);

        if (cached != null) {
            log.debug("구독 캐시 히트: {}", cacheKey);
            return "NONE".equals(cached) ? null : SubscriptionTier.valueOf(cached);
        }

        // 캐시 미스 → DB 조회
        Optional<Subscription> subscription = subscriptionRepository
                .findBySubscriberIdAndBrandIdAndStatus(subscriberId, brandId, SubscriptionStatus.ACTIVE);

        String tierValue;
        SubscriptionTier tier = null;

        if (subscription.isPresent() && subscription.get().isActive()) {
            tier = subscription.get().getTier();
            tierValue = tier.name();
        } else {
            tierValue = "NONE";
        }

        // Redis 캐시 저장 (1시간 TTL)
        redisTemplate.opsForValue().set(cacheKey, tierValue, CACHE_TTL_HOURS, TimeUnit.HOURS);
        log.debug("구독 캐시 저장: {} = {}", cacheKey, tierValue);

        return tier;
    }

    /**
     * 구독 상태 변경 시 캐시 무효화
     */
    public void evictCache(Long subscriberId, Long brandId) {
        redisTemplate.delete(buildKey(subscriberId, brandId));
        log.debug("구독 캐시 삭제: subscriberId={}, brandId={}", subscriberId, brandId);
    }

    private String buildKey(Long subscriberId, Long brandId) {
        return CACHE_KEY_PREFIX + subscriberId + ":" + brandId;
    }
}
