package pl.edu.pw.mini.ingreedio.api.product.model;

import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.lang.Nullable;

@Document(collection = "products")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductDocument {
    @Transient
    public static final String SEQUENCE_NAME = "products_sequence";

    @Id
    private Long id;

    @Field
    private String name;

    @Field
    private String smallImageUrl;

    @Field
    private String largeImageUrl;

    @Field
    private ProviderDocument provider;

    @Field
    private BrandDocument brand;

    @Field
    private Set<CategoryDocument> categories;

    @Field
    private String shortDescription;

    @Field
    private String longDescription;

    @Field
    private String volume;

    @Field
    private Set<IngredientDocument> ingredients;

    @Field
    private Integer rating;

    @Field
    private Integer ratingSum;

    @Field
    @Nullable
    private Set<Integer> likedBy;

    @Field
    @Nullable
    private Map<Integer, Integer> ratings;
}
