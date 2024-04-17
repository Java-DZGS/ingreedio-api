package pl.edu.pw.mini.ingreedio.api.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
    private Integer volume;
    @Field
    private List<String> ingredients;
}
