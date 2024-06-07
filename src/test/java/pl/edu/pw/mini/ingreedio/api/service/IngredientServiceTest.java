package pl.edu.pw.mini.ingreedio.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pw.mini.ingreedio.api.IntegrationTest;
import pl.edu.pw.mini.ingreedio.api.ingredient.model.Ingredient;
import pl.edu.pw.mini.ingreedio.api.ingredient.service.IngredientService;
import pl.edu.pw.mini.ingreedio.api.user.model.User;
import pl.edu.pw.mini.ingreedio.api.user.service.UserService;

@SpringBootTest
@Transactional
public class IngredientServiceTest extends IntegrationTest {
    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private UserService userService;

    private User user;

    @BeforeEach
    public void setupData() {
        user = userService.createUser("Dummy", "dummy@example.com");
    }

    @Test
    public void givenIngredientObject_whenSaveIngredient_thenReturnIngredientObject() {
        // Given
        Ingredient ingredient = Ingredient.builder().name("ingredient1").build();

        // When
        Ingredient savedIngredient = ingredientService.addIngredient(ingredient);

        // Then
        assertThat(savedIngredient).isNotNull();
        assertThat(savedIngredient.getName()).isEqualTo("ingredient1");
    }

    @Test
    public void givenIngredient_whenLikeIngredient_thenSuccess() {
        // Given
        Ingredient ingredient = ingredientService.addIngredient(
            Ingredient.builder().name("ingredient1").build());

        // When
        ingredientService.likeIngredient(ingredient, user);
        Set<Ingredient> ingredients = user.getLikedIngredients();

        // Then
        assertThat(ingredients).map(Ingredient::getId).contains(ingredient.getId());
    }

    @Test
    public void givenIngredient_whenUnlikeIngredient_thenSuccess() {
        // Given
        Ingredient ingredient = ingredientService.addIngredient(
            Ingredient.builder().name("ingredient1").build());
        ingredientService.likeIngredient(ingredient, user);

        // When
        ingredientService.unlikeIngredient(ingredient, user);
        Set<Ingredient> ingredients = user.getLikedIngredients();

        // Then
        assertThat(ingredients).map(Ingredient::getId).doesNotContain(ingredient.getId());
    }

    @Test
    public void givenUser_whenLikeIngredients_thenGetLikedIngredients() {
        // Given
        final Ingredient ingredient1 = ingredientService.addIngredient(
            Ingredient.builder().name("ingredient1").build());
        final Ingredient ingredient2 = ingredientService.addIngredient(
            Ingredient.builder().name("ingredient2").build());
        final Ingredient ingredient3 = ingredientService.addIngredient(
            Ingredient.builder().name("ingredient3").build());
        final Ingredient ingredient4 = ingredientService.addIngredient(
            Ingredient.builder().name("ingredient4").build());

        ingredientService.likeIngredient(ingredient1, user);
        ingredientService.likeIngredient(ingredient2, user);
        ingredientService.likeIngredient(ingredient3, user);

        // When
        Set<Long> likedIngredients = user.getLikedIngredients()
            .stream().map(Ingredient::getId).collect(Collectors.toSet());

        // Then
        assertThat(likedIngredients.size()).isEqualTo(3);
        assertThat(likedIngredients).containsAll(
            List.of(ingredient1.getId(), ingredient2.getId(), ingredient3.getId()));
        assertThat(likedIngredients).doesNotContain(ingredient4.getId());
    }

    @Test
    public void givenIngredient_whenAddAllergen_thenSuccess() {
        // Given
        Ingredient ingredient = ingredientService.addIngredient(
            Ingredient.builder().name("ingredient1").build());

        // When
        ingredientService.addAllergen(ingredient, user);
        Set<Ingredient> allergens = user.getAllergens();

        // Then
        assertThat(allergens).map(Ingredient::getId).contains(ingredient.getId());
    }

    @Test
    @Transactional
    public void givenIngredient_whenRemoveAllergen_thenSuccess() {
        // Given
        Ingredient ingredient = ingredientService.addIngredient(
            Ingredient.builder().name("ingredient1").build());
        ingredientService.addAllergen(ingredient, user);

        // When
        ingredientService.removeAllergen(ingredient, user);
        Set<Ingredient> allergens = user.getAllergens();

        // Then
        assertThat(allergens).map(Ingredient::getId).doesNotContain(ingredient.getId());
    }

    @Test
    public void givenUser_whenAddAllergens_thenGetAllergens() {
        // Given
        final Ingredient ingredient1 = ingredientService.addIngredient(
            Ingredient.builder().name("ingredient1").build());
        final Ingredient ingredient2 = ingredientService.addIngredient(
            Ingredient.builder().name("ingredient2").build());
        final Ingredient ingredient3 = ingredientService.addIngredient(
            Ingredient.builder().name("ingredient3").build());
        final Ingredient ingredient4 = ingredientService.addIngredient(
            Ingredient.builder().name("ingredient4").build());

        ingredientService.addAllergen(ingredient1, user);
        ingredientService.addAllergen(ingredient2, user);
        ingredientService.addAllergen(ingredient3, user);

        // When
        Set<Long> allergens = user.getAllergens().stream()
            .map(Ingredient::getId).collect(Collectors.toSet());

        // Then
        assertThat(allergens.size()).isEqualTo(3);
        assertThat(allergens).containsAll(
            List.of(ingredient1.getId(), ingredient2.getId(), ingredient3.getId()));
        assertThat(allergens).doesNotContain(ingredient4.getId());
    }

    @Test
    public void givenQuery_whenSearch_thenCorrectResult() {
        // Given
        final Long ingredient1Id = ingredientService.addIngredient(
            Ingredient.builder().name("LAURKA DLA MAMY").build()).getId();
        final Long ingredient2Id = ingredientService.addIngredient(
            Ingredient.builder().name("SULFUR").build()).getId();
        final Long ingredient3Id = ingredientService.addIngredient(
            Ingredient.builder().name("LAURYL SULFATE").build()).getId();
        final Long ingredient4Id = ingredientService.addIngredient(
            Ingredient.builder().name("KLAU FSUL").build()).getId();
        final Long ingredient5Id = ingredientService.addIngredient(
            Ingredient.builder().name("LAURYL SULFIDE").build()).getId();

        String query = "LAU SUL";

        // When
        List<Ingredient> ingredients = ingredientService.getIngredients(10, query);

        // Then
        assertThat(ingredients.size()).isEqualTo(4);
        assertThat(ingredients.getFirst().getId()).isEqualTo(ingredient3Id);
        assertThat(ingredients).map(Ingredient::getId).containsAll(
            List.of(ingredient1Id, ingredient2Id, ingredient3Id, ingredient5Id)
        );
        assertThat(ingredients).map(Ingredient::getId).doesNotContain(ingredient4Id);
    }

    @Test
    @WithMockUser(username = "user", password = "user")
    public void givenLiked_whenSearch_thenLikedArePromoted() {
        // Given
        final Ingredient ingredient1 = ingredientService.addIngredient(
            Ingredient.builder().name("LAURKA DLA MAMY").build());
        final Ingredient ingredient2 = ingredientService.addIngredient(
            Ingredient.builder().name("SULFUR").build());
        final Ingredient ingredient3 = ingredientService.addIngredient(
            Ingredient.builder().name("LAURYL SULFATE").build());
        final Ingredient ingredient4 = ingredientService.addIngredient(
            Ingredient.builder().name("LAURYL SULFIDE").build());

        String query = "LAU SUL";
        ingredientService.likeIngredient(ingredient4, user);

        // When
        List<Ingredient> ingredients =
            ingredientService.getIngredients(10, query, user, true);

        // Then
        assertThat(ingredients.size()).isEqualTo(4);
        assertThat(ingredients.getFirst().getId()).isEqualTo(ingredient4.getId());
        assertThat(ingredients).map(Ingredient::getId).containsAll(
            List.of(ingredient1.getId(), ingredient2.getId(), ingredient3.getId(),
                ingredient4.getId())
        );
    }

    @Test
    @WithMockUser(username = "user", password = "user")
    public void givenAllergens_whenSearch_thenAllergensAreSkipped() {
        // Given
        final Ingredient ingredient1 = ingredientService.addIngredient(
            Ingredient.builder().name("LAURKA DLA MAMY").build());
        final Ingredient ingredient2 = ingredientService.addIngredient(
            Ingredient.builder().name("SULFUR").build());
        final Ingredient ingredient3 = ingredientService.addIngredient(
            Ingredient.builder().name("LAURYL SULFATE").build());
        final Ingredient ingredient4 = ingredientService.addIngredient(
            Ingredient.builder().name("LAURYL SULFIDE").build());

        String query = "LAU SUL";
        ingredientService.addAllergen(ingredient3, user);

        // When
        List<Ingredient> ingredients =
            ingredientService.getIngredients(10, query, user, true);

        // Then
        assertThat(ingredients.size()).isEqualTo(3);
        assertThat(ingredients).map(Ingredient::getId).doesNotContain(ingredient3.getId());
        assertThat(ingredients).map(Ingredient::getId).containsAll(
            List.of(ingredient1.getId(), ingredient2.getId(), ingredient4.getId())
        );
    }
}
