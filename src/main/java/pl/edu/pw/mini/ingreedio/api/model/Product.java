package pl.edu.pw.mini.ingreedio.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@RequiredArgsConstructor
@Getter
@Setter
public class Product {
    private final Integer id;
    private final String name;
    private final List<Category> categories;
    private final List<Ingredient> ingredients;
    private final String url;
    private final String provider;
    private final String brand;
    private final String shortDescription;
    private final String longDescription;
    private final Double volume;

}
