package pl.edu.pw.mini.ingreedio.api.product.model;

import java.util.List;
import java.util.Map;
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
public class Product {
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
    private String provider;
    @Field
    private String brand;
    @Field
    private String shortDescription;
    @Field
    private String longDescription;
    @Field
    private String volume;
    @Field
    private List<String> ingredients;
    @Field
    private Integer rating;
    @Field
    private Integer ratingSum;
    @Field
    @Nullable
    private List<Long> likedBy;
    @Field
    @Nullable
    private Map<Long, Integer> ratings;
}
