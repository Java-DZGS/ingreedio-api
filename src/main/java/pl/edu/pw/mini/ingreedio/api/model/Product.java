package pl.edu.pw.mini.ingreedio.api.model;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class Product {
    private final Long id;
    private final String name;
    private final List<Category> categories;
    private final List<Ingredient> ingredients;
    private final String url;
    private final String provider;
    private final String brand;
    private final String shortDescription;
    private final String longDescription;
    private final Double volume;

    public Product(Long id,
                   String name,
                   List<Category> categories,
                   List<Ingredient> ingredients,
                   String url,
                   String provider,
                   String brand,
                   String shortDescription,
                   String longDescription,
                   Double volume) {
        this.id = id;
        this.name = name;
        this.categories = categories;
        this.ingredients = ingredients;
        this.url = url;
        this.provider = provider;
        this.brand = brand;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.volume = volume;
    }
}
