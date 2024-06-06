package pl.edu.pw.mini.ingreedio.api.product.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import pl.edu.pw.mini.ingreedio.api.common.validation.ValidationGroups;

public record ProductRequestDto(
    @NotNull(groups = ValidationGroups.Put.class) String name,
    @NotNull(groups = ValidationGroups.Put.class) String smallImageUrl,
    @NotNull(groups = ValidationGroups.Put.class) String largeImageUrl,
    @NotNull(groups = ValidationGroups.Put.class) Long provider,
    @NotNull(groups = ValidationGroups.Put.class) Long brand,
    @NotNull(groups = ValidationGroups.Put.class) Set<Long> categories,
    @NotNull(groups = ValidationGroups.Put.class) String shortDescription,
    @NotNull(groups = ValidationGroups.Put.class) String longDescription,
    @NotNull(groups = ValidationGroups.Put.class) String volume,
    @NotNull(groups = ValidationGroups.Put.class) Set<Long> ingredients
) { }