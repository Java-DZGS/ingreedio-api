package pl.edu.pw.mini.ingreedio.api.category.dto;

import lombok.Builder;

@Builder
public record CategoryDto(Long id, String name) { }
