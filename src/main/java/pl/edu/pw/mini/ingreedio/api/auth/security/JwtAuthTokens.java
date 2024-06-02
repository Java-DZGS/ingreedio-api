package pl.edu.pw.mini.ingreedio.api.auth.security;

import lombok.Builder;
import pl.edu.pw.mini.ingreedio.api.auth.model.RefreshToken;

@Builder
public record JwtAuthTokens(String accessToken, RefreshToken refreshToken) {
}
