package pl.edu.pw.mini.ingreedio.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class Ingredient {
    private final Integer id;
    private final String name;
}
