package com.musecraft.brand.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class BrandGenerateRequest {

    @NotBlank(message = "브랜드 이름은 필수입니다.")
    @Size(max = 100, message = "브랜드 이름은 최대 100자까지 입력할 수 있습니다.")
    private String name;

    @NotBlank(message = "키워드는 필수입니다.")
    @Size(max = 500, message = "키워드는 최대 500자까지 입력할 수 있습니다.")
    private String keywords;  // 예: "미니멀, 감성, 라이프스타일, 20대 여성"
}
