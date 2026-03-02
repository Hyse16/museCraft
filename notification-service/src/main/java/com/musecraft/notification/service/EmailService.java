package com.musecraft.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendNewsletterEmail(String to, String brandName, String title, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("[" + brandName + "] " + title);
        message.setText(content);

        try {
            mailSender.send(message);
            log.info("뉴스레터 이메일 발송 완료: {}", to);
        } catch (Exception e) {
            log.error("뉴스레터 이메일 발송 실패 - to: {}, error: {}", to, e.getMessage());
            throw e;
        }
    }

    public void sendSubscriptionExpiredEmail(String to, String brandName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("[museCraft] " + brandName + " 구독이 만료되었습니다.");
        message.setText(String.format(
                "%s 브랜드의 구독이 만료되었습니다.\n\n" +
                "계속해서 프리미엄 콘텐츠를 이용하시려면 구독을 갱신해주세요.\n" +
                "https://musecraft.com/brands",
                brandName
        ));

        try {
            mailSender.send(message);
            log.info("구독 만료 이메일 발송 완료: {}", to);
        } catch (Exception e) {
            log.error("구독 만료 이메일 발송 실패 - to: {}, error: {}", to, e.getMessage());
        }
    }

    public void sendSettlementEmail(String to, String creatorName, int amount, String period) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("[museCraft] " + period + " 정산 내역");
        message.setText(String.format(
                "%s 크리에이터님, %s 정산이 완료되었습니다.\n\n" +
                "정산 금액: %,d원\n\n" +
                "자세한 내역은 대시보드에서 확인하세요.\n" +
                "https://musecraft.com/dashboard/settlement",
                creatorName, period, amount
        ));

        mailSender.send(message);
        log.info("정산 이메일 발송 완료: {}", to);
    }
}
