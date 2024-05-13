package pl.edu.pw.mini.ingreedio.api.dto;

public record ReviewRequestDto(Long id,
                               Long userId,
                               Long productId,
                               Integer rating,
                               String content) {

}
