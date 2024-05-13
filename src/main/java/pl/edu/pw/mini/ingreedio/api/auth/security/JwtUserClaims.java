package pl.edu.pw.mini.ingreedio.api.auth.security;

import java.util.Set;
import lombok.Builder;

@Builder
public record JwtUserClaims(
    String username,
    Set<String> roles,
    Set<String> permissions
) { }