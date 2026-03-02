package com.musecraft.subscription.controller;

import com.musecraft.common.response.ApiResponse;
import com.musecraft.subscription.dto.request.SubscribeRequest;
import com.musecraft.subscription.dto.response.SubscriptionResponse;
import com.musecraft.subscription.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SubscriptionResponse> subscribe(
            @RequestHeader("X-User-Id") Long subscriberId,
            @Valid @RequestBody SubscribeRequest request) {
        return ApiResponse.ok("구독이 완료되었습니다.", subscriptionService.subscribe(subscriberId, request));
    }

    @DeleteMapping("/{subscriptionId}")
    public ApiResponse<Void> cancel(
            @RequestHeader("X-User-Id") Long subscriberId,
            @PathVariable Long subscriptionId) {
        subscriptionService.cancel(subscriberId, subscriptionId);
        return ApiResponse.ok("구독이 해지되었습니다.");
    }

    @GetMapping("/my")
    public ApiResponse<List<SubscriptionResponse>> getMySubscriptions(
            @RequestHeader("X-User-Id") Long subscriberId) {
        return ApiResponse.ok(subscriptionService.getMySubscriptions(subscriberId));
    }

    @GetMapping("/brands/{brandId}/count")
    public ApiResponse<Long> getSubscriberCount(@PathVariable Long brandId) {
        return ApiResponse.ok(subscriptionService.getSubscriberCount(brandId));
    }
}
