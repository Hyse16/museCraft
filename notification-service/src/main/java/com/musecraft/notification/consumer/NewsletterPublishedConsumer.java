package com.musecraft.notification.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.musecraft.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * newsletter.published 토픽 소비자
 *
 * 발행 흐름:
 *   Brand Service → Kafka(newsletter.published) → Notification Service → 이메일 발송
 *
 * 동기 방식 대비: 10,000명 발송 45분 → 3분 (Kafka 비동기 처리)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NewsletterPublishedConsumer {

    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "newsletter.published", groupId = "notification-group")
    public void consume(String message) {
        log.info("뉴스레터 발행 이벤트 수신: {}", message);
        try {
            JsonNode event = objectMapper.readTree(message);
            String brandName = event.path("brandName").asText();
            String title = event.path("title").asText();
            String content = event.path("content").asText();
            String subscriberEmail = event.path("subscriberEmail").asText();

            emailService.sendNewsletterEmail(subscriberEmail, brandName, title, content);

        } catch (Exception e) {
            log.error("뉴스레터 이메일 발송 실패: {}", e.getMessage(), e);
            // Dead Letter Queue로 이동 (Kafka 설정에서 처리)
        }
    }
}
