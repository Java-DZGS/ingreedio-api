package pl.edu.pw.mini.ingreedio.api.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String email;

    @Column
    private String displayName;

    @ManyToMany
    @JoinTable(
        name = "users_ingredients",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "ingredient_id"))
    @Builder.Default
    private Set<Ingredient> likedIngredients = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "users_allergens",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "ingredient_id"))
    @Builder.Default
    private Set<Ingredient> allergens = new HashSet<>();
  
    @ElementCollection
    @CollectionTable(
        name = "users_products",
        joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "product_id")
    @Builder.Default
    private Set<Long> likedProducts = new HashSet<>();
}
