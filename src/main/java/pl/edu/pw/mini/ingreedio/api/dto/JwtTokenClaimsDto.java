package pl.edu.pw.mini.ingreedio.api.dto;

import java.util.Set;

public record JwtTokenClaimsDto(
    String username,
    Set<String> roles,
    Set<String> permissions
) { }

