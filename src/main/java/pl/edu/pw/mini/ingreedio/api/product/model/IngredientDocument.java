package pl.edu.pw.mini.ingreedio.api.product.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IngredientDocument {
    @Field("id")
    private Long id;

    @Field("name")
    private String name;
}
