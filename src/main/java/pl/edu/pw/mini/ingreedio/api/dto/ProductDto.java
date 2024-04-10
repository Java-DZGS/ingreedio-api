package pl.edu.pw.mini.ingreedio.api.dto;

import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
public record ProductDto(
        Long id,
        String name,
        String smallImageUrl,
        String provider,
        String shortDescription
){

}
