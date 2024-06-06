package pl.edu.pw.mini.ingreedio.api.provider.dto;

import lombok.Builder;

@Builder
public record ProviderDto(Long id, String name) { }
