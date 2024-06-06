package pl.edu.pw.mini.ingreedio.api.product.dto;

import lombok.Builder;
import pl.edu.pw.mini.ingreedio.api.brand.dto.BrandDto;
import pl.edu.pw.mini.ingreedio.api.provider.dto.ProviderDto;

@Builder
public record ProductViewDto(Long id, String name, BrandDto brand, String smallImageUrl,
                             ProviderDto provider, String shortDescription, Boolean isLiked,
                             Integer rating) { }
