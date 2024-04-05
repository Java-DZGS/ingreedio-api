package pl.edu.pw.mini.ingreedio.api.dto;

import lombok.Builder;

@Builder
public record JwtResponseDto(String accessToken, String refreshToken) {}
