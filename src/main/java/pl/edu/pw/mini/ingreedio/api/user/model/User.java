package pl.edu.pw.mini.ingreedio.api.user.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.pw.mini.ingreedio.api.product.model.Ingredient;
import pl.edu.pw.mini.ingreedio.api.product.model.Review;

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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "users_ingredients",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "ingredient_id"))
    @Builder.Default
    private Set<Ingredient> likedIngredients = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
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

    @OneToMany(mappedBy = "user")
    private Set<Review> reviews = new HashSet<>();
}