package com.musecraft.subscription.repository;

import com.musecraft.subscription.domain.Subscription;
import com.musecraft.subscription.domain.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findBySubscriberIdAndBrandIdAndStatus(
            Long subscriberId, Long brandId, SubscriptionStatus status);

    List<Subscription> findBySubscriberId(Long subscriberId);

    // 만료 배치용: 만료 시각이 지났으나 아직 ACTIVE 상태인 구독 조회
    @Query("SELECT s FROM Subscription s WHERE s.status = 'ACTIVE' AND s.expiresAt < :now")
    List<Subscription> findExpiredSubscriptions(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.brandId = :brandId AND s.status = 'ACTIVE'")
    long countActiveByBrandId(@Param("brandId") Long brandId);
}
