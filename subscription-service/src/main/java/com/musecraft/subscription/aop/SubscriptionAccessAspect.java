package com.musecraft.subscription.aop;

import com.musecraft.common.exception.BusinessException;
import com.musecraft.common.exception.ErrorCode;
import com.musecraft.subscription.annotation.RequireSubscription;
import com.musecraft.subscription.domain.SubscriptionTier;
import com.musecraft.subscription.service.SubscriptionCacheService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @RequireSubscription 어노테이션이 붙은 메서드에 구독 티어 체크를 적용하는 AOP
 *
 * 게이트웨이에서 전달된 X-User-Id, X-Brand-Id 헤더를 읽어 Redis 캐시로 구독 상태를 검증
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class SubscriptionAccessAspect {

    private final SubscriptionCacheService cacheService;

    @Around("@annotation(requireSubscription)")
    public Object checkSubscription(ProceedingJoinPoint joinPoint,
                                    RequireSubscription requireSubscription) throws Throwable {

        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes()).getRequest();

        String userIdHeader = request.getHeader("X-User-Id");
        String brandIdHeader = request.getHeader("X-Brand-Id");

        if (userIdHeader == null || brandIdHeader == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        Long subscriberId = Long.parseLong(userIdHeader);
        Long brandId = Long.parseLong(brandIdHeader);
        SubscriptionTier required = requireSubscription.value();

        SubscriptionTier current = cacheService.getSubscriptionTier(subscriberId, brandId);

        if (!hasAccess(current, required)) {
            log.warn("구독 접근 거부: userId={}, brandId={}, required={}, current={}",
                    subscriberId, brandId, required, current);
            throw new BusinessException(ErrorCode.SUBSCRIPTION_TIER_REQUIRED,
                    required.name() + " 이상의 구독이 필요합니다.");
        }

        return joinPoint.proceed();
    }

    /**
     * 현재 티어가 요구 티어 이상인지 확인
     * PREMIUM > BASIC > FREE 순서
     */
    private boolean hasAccess(SubscriptionTier current, SubscriptionTier required) {
        if (current == null) return false;
        return current.ordinal() >= required.ordinal();
    }
}
