package pl.edu.pw.mini.ingreedio.api.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record FullProductDto(Long id, String name, String largeImageUrl, String provider,
                             String brand, String longDescription, Integer volume,
                             List<String> ingredients) {
}