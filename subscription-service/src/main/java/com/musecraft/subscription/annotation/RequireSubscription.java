package com.musecraft.subscription.annotation;

import com.musecraft.subscription.domain.SubscriptionTier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 구독 티어 접근 제어 어노테이션.
 * 지정한 티어 이상의 구독이 있어야 접근 가능.
 *
 * 사용 예:
 *   @RequireSubscription(SubscriptionTier.BASIC)   // BASIC 이상
 *   @RequireSubscription(SubscriptionTier.PREMIUM) // PREMIUM 만
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireSubscription {
    SubscriptionTier value();
}
