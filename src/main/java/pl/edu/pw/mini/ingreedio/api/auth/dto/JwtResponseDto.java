package pl.edu.pw.mini.ingreedio.api.auth.dto;

import lombok.Builder;

@Builder
public record JwtResponseDto(String accessToken, String refreshToken) {}
