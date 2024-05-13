package pl.edu.pw.mini.ingreedio.api.product.dto;

import java.util.List;
import lombok.Builder;
import org.springframework.data.mongodb.core.mapping.Field;

@Builder
public record ProductRequestDto(String name,
                                String smallImageUrl,
                                String largeImageUrl,
                                String provider,
                                String brand,
                                String shortDescription,
                                String longDescription,
                                String volume,
                                List<String> ingredients) {
}
