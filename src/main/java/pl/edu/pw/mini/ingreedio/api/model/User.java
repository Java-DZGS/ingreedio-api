package pl.edu.pw.mini.ingreedio.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@RequiredArgsConstructor
@Getter
@Setter
public class User {
    private final Integer id;
    private final String email;
    private final String displayName;
    private final List<Ingredient> favoriteProducts;
    private final List<Ingredient> allergens;
}
