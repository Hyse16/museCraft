package com.musecraft.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;      // access token 만료 시간 (초)
    private UserResponse user;
}
