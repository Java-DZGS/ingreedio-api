package pl.edu.pw.mini.ingreedio.api.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private final Integer id;
    private final String email;
    private final String displayName;
    private final List<Ingredient> favoriteProducts;
    private final List<Ingredient> allergens;

    public User(Integer id,
                String email,
                String displayName,
                List<Ingredient> favoriteProducts,
                List<Ingredient> allergens) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.favoriteProducts = favoriteProducts;
        this.allergens = allergens;
    }
}
