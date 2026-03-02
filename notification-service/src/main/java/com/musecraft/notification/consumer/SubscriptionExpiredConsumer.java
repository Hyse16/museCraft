package com.musecraft.notification.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.musecraft.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * subscription.expired 토픽 소비자
 * 구독 만료 시 구독자에게 알림 이메일 발송
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionExpiredConsumer {

    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "subscription.expired", groupId = "notification-group")
    public void consume(String message) {
        log.info("구독 만료 이벤트 수신: {}", message);
        try {
            JsonNode event = objectMapper.readTree(message);
            String subscriberEmail = event.path("subscriberEmail").asText();
            String brandName = event.path("brandName").asText();

            emailService.sendSubscriptionExpiredEmail(subscriberEmail, brandName);

        } catch (Exception e) {
            log.error("구독 만료 이메일 발송 실패: {}", e.getMessage(), e);
        }
    }
}
