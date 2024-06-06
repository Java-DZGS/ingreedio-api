package pl.edu.pw.mini.ingreedio.api.product.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record ProductPageDto(List<ProductDto> products, int totalPages) { }
