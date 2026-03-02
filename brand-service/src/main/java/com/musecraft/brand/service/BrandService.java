package com.musecraft.brand.service;

import com.musecraft.brand.client.ClaudeApiClient;
import com.musecraft.brand.domain.AiGenerationLog;
import com.musecraft.brand.domain.Brand;
import com.musecraft.brand.domain.BrandStatus;
import com.musecraft.brand.dto.request.BrandGenerateRequest;
import com.musecraft.brand.dto.response.BrandResponse;
import com.musecraft.brand.repository.AiGenerationLogRepository;
import com.musecraft.brand.repository.BrandRepository;
import com.musecraft.common.exception.BusinessException;
import com.musecraft.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandService {

    private static final int AI_DAILY_LIMIT = 5; // 사용자당 일일 AI 생성 한도

    private final BrandRepository brandRepository;
    private final AiGenerationLogRepository aiLogRepository;
    private final ClaudeApiClient claudeApiClient;

    @Transactional
    public BrandResponse generateBrand(Long creatorId, BrandGenerateRequest request) {
        if (brandRepository.existsByCreatorId(creatorId)) {
            throw new BusinessException(ErrorCode.BRAND_ALREADY_EXISTS);
        }

        // 일일 생성 한도 체크
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        long todayCount = aiLogRepository.countByUserIdAndCreatedAtAfter(creatorId, todayStart);
        if (todayCount >= AI_DAILY_LIMIT) {
            throw new BusinessException(ErrorCode.AI_DAILY_LIMIT_EXCEEDED);
        }

        // Claude API로 브랜드 아이덴티티 생성
        ClaudeApiClient.BrandIdentityResult result;
        try {
            result = claudeApiClient.generateBrandIdentity(request.getName(), request.getKeywords());
        } catch (Exception e) {
            log.error("브랜드 AI 생성 실패 - creatorId: {}", creatorId, e);
            throw new BusinessException(ErrorCode.AI_GENERATION_FAILED);
        }

        Brand brand = Brand.builder()
                .creatorId(creatorId)
                .name(request.getName())
                .slogan(result.slogan())
                .brandStory(result.brandStory())
                .colorPalette(result.colorPalette())
                .keywords(request.getKeywords())
                .build();

        Brand saved = brandRepository.save(brand);

        // AI 생성 로그 기록
        aiLogRepository.save(AiGenerationLog.builder()
                .userId(creatorId)
                .brandId(saved.getId())
                .model("claude-sonnet-4-6")
                .inputTokens(result.inputTokens())
                .outputTokens(result.outputTokens())
                .generationType("BRAND_IDENTITY")
                .build());

        return BrandResponse.from(saved);
    }

    public BrandResponse getMyBrand(Long creatorId) {
        Brand brand = brandRepository.findByCreatorId(creatorId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));
        return BrandResponse.from(brand);
    }

    public BrandResponse getBrand(Long brandId) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));
        return BrandResponse.from(brand);
    }

    public Page<BrandResponse> getActiveBrands(Pageable pageable) {
        return brandRepository.findByStatus(BrandStatus.ACTIVE, pageable)
                .map(BrandResponse::from);
    }

    public List<BrandResponse> getPendingBrands() {
        return brandRepository.findAllByStatus(BrandStatus.PENDING)
                .stream().map(BrandResponse::from).toList();
    }

    @Transactional
    public BrandResponse activateBrand(Long brandId) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));
        brand.activate();
        return BrandResponse.from(brand);
    }

    @Transactional
    public BrandResponse updateLogoUrl(Long creatorId, String logoUrl) {
        Brand brand = brandRepository.findByCreatorId(creatorId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));
        brand.updateLogoUrl(logoUrl);
        return BrandResponse.from(brand);
    }
}
