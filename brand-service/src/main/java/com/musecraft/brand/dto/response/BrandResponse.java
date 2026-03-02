package com.musecraft.brand.dto.response;

import com.musecraft.brand.domain.Brand;
import com.musecraft.brand.domain.BrandStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
public class BrandResponse {
    private Long id;
    private Long creatorId;
    private String name;
    private String slogan;
    private String brandStory;
    private Map<String, String> colorPalette;
    private String logoUrl;
    private String keywords;
    private BrandStatus status;
    private LocalDateTime createdAt;

    public static BrandResponse from(Brand brand) {
        return BrandResponse.builder()
                .id(brand.getId())
                .creatorId(brand.getCreatorId())
                .name(brand.getName())
                .slogan(brand.getSlogan())
                .brandStory(brand.getBrandStory())
                .colorPalette(brand.getColorPalette())
                .logoUrl(brand.getLogoUrl())
                .keywords(brand.getKeywords())
                .status(brand.getStatus())
                .createdAt(brand.getCreatedAt())
                .build();
    }
}
