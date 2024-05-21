package pl.edu.pw.mini.ingreedio.api.product.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ReviewRequestDto(
    @Min(value = 0)
    @Max(value = 10)
    Integer rating,
    String content) {

}
