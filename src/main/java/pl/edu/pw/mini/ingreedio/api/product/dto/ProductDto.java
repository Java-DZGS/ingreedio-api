package pl.edu.pw.mini.ingreedio.api.product.dto;

import java.util.List;
import lombok.Builder;
import pl.edu.pw.mini.ingreedio.api.brand.dto.BrandDto;
import pl.edu.pw.mini.ingreedio.api.category.dto.CategoryDto;
import pl.edu.pw.mini.ingreedio.api.ingredient.dto.IngredientDto;
import pl.edu.pw.mini.ingreedio.api.provider.dto.ProviderDto;

@Builder
public record ProductDto(Long id, String name, String largeImageUrl, ProviderDto provider,
                         BrandDto brand, List<CategoryDto> categories, String longDescription,
                         String volume, List<IngredientDto> ingredients, Boolean isLiked,
                         Integer rating) { }
