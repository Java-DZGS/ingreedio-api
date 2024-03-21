package pl.edu.pw.mini.ingreedio.api.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Ingredient {
    private final Integer id;
    private final String name;

    public Ingredient(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
