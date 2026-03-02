package com.musecraft.brand.controller;

import com.musecraft.brand.dto.request.BrandGenerateRequest;
import com.musecraft.brand.dto.response.BrandResponse;
import com.musecraft.brand.service.BrandService;
import com.musecraft.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    // 게이트웨이가 X-User-Id 헤더로 userId 전달
    @PostMapping("/generate")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<BrandResponse> generateBrand(
            @RequestHeader("X-User-Id") Long creatorId,
            @Valid @RequestBody BrandGenerateRequest request) {
        return ApiResponse.ok("브랜드가 생성되었습니다.", brandService.generateBrand(creatorId, request));
    }

    @GetMapping("/my")
    public ApiResponse<BrandResponse> getMyBrand(@RequestHeader("X-User-Id") Long creatorId) {
        return ApiResponse.ok(brandService.getMyBrand(creatorId));
    }

    @GetMapping("/{brandId}")
    public ApiResponse<BrandResponse> getBrand(@PathVariable Long brandId) {
        return ApiResponse.ok(brandService.getBrand(brandId));
    }

    @GetMapping
    public ApiResponse<Page<BrandResponse>> getActiveBrands(
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.ok(brandService.getActiveBrands(pageable));
    }

    @GetMapping("/admin/pending")
    public ApiResponse<java.util.List<BrandResponse>> getPendingBrands() {
        return ApiResponse.ok(brandService.getPendingBrands());
    }

    @PatchMapping("/{brandId}/activate")
    public ApiResponse<BrandResponse> activateBrand(@PathVariable Long brandId) {
        return ApiResponse.ok("브랜드가 승인되었습니다.", brandService.activateBrand(brandId));
    }
}
