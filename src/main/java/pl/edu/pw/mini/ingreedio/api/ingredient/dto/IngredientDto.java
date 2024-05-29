package pl.edu.pw.mini.ingreedio.api.ingredient.dto;

import lombok.Builder;

@Builder
public record IngredientDto(Long id, String name) {
}
