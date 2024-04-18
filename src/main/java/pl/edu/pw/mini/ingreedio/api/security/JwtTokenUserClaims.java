package pl.edu.pw.mini.ingreedio.api.security;

import java.util.Set;
import lombok.Builder;

@Builder
public record JwtTokenUserClaims(
    String username,
    Set<String> roles,
    Set<String> permissions
) { }

