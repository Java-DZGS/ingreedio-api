package pl.edu.pw.mini.ingreedio.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pw.mini.ingreedio.api.IntegrationTest;
import pl.edu.pw.mini.ingreedio.api.ingredient.model.Ingredient;
import pl.edu.pw.mini.ingreedio.api.ingredient.service.IngredientService;
import pl.edu.pw.mini.ingreedio.api.product.exception.IngredientNotFoundException;
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
    public void createUser() {
        user = userService.createUser("user", "us@er.com");
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
    public void givenIngredientId_whenLikeIngredient_thenSuccess() {
        // Given
        long ingredientId = ingredientService.addIngredient(
            Ingredient.builder().name("ingredient1").build()).getId();

        // When
        ingredientService.likeIngredient(ingredientId, user);
        List<Ingredient> ingredients = ingredientService.getLikedIngredients(user);

        // Then
        assertThat(ingredients).map(Ingredient::getId).contains(ingredientId);
    }

    @Test
    public void givenNonExistingIngredientId_whenLikeIngredient_thenFailure() {
        // Given

        // When
        Exception problem = catchException(() -> ingredientService.likeIngredient(10000L, user));

        // Then
        assertThat(problem).isInstanceOf(IngredientNotFoundException.class);
    }

    @Test
    public void givenIngredientId_whenUnlikeIngredient_thenSuccess() {
        // Given
        long ingredientId = ingredientService.addIngredient(
            Ingredient.builder().name("ingredient1").build()).getId();
        ingredientService.likeIngredient(ingredientId, user);

        // When
        ingredientService.unlikeIngredient(ingredientId, user);
        List<Ingredient> ingredients = ingredientService.getLikedIngredients(user);

        // Then
        assertThat(ingredients).map(Ingredient::getId).doesNotContain(ingredientId);
    }

    @Test
    public void givenNonExistingIngredientId_whenUnlikeIngredient_thenFailure() {
        // Given

        // When
        Exception problem = catchException(() -> ingredientService.unlikeIngredient(10000L, user));

        // Then
        assertThat(problem).isInstanceOf(IngredientNotFoundException.class);
    }

    @Test
    public void givenUser_whenLikeIngredients_thenGetLikedIngredients() {
        // Given
        final long ingredient1Id = ingredientService.addIngredient(
            Ingredient.builder().name("ingredient1").build()).getId();
        final long ingredient2Id = ingredientService.addIngredient(
            Ingredient.builder().name("ingredient2").build()).getId();
        final long ingredient3Id = ingredientService.addIngredient(
            Ingredient.builder().name("ingredient3").build()).getId();
        final long ingredient4Id = ingredientService.addIngredient(
            Ingredient.builder().name("ingredient4").build()).getId();

        ingredientService.likeIngredient(ingredient1Id, user);
        ingredientService.likeIngredient(ingredient2Id, user);
        ingredientService.likeIngredient(ingredient3Id, user);

        // When
        List<Long> likedIngredients = ingredientService.getLikedIngredients(user)
            .stream().map(Ingredient::getId).toList();

        // Then
        assertThat(likedIngredients.size()).isEqualTo(3);
        assertThat(likedIngredients).containsAll(
            List.of(ingredient1Id, ingredient2Id, ingredient1Id));
        assertThat(likedIngredients).doesNotContain(ingredient4Id);
    }

    @Test
    public void givenIngredientId_whenAddAllergen_thenSuccess() {
        // Given
        long ingredientId = ingredientService.addIngredient(
            Ingredient.builder().name("ingredient1").build()).getId();

        // When
        ingredientService.addAllergen(ingredientId, user);
        List<Ingredient> allergens = ingredientService.getAllergens(user);

        // Then
        assertThat(allergens).map(Ingredient::getId).contains(ingredientId);
    }

    @Test
    public void givenNonExistingIngredientId_whenAddAllergen_thenFailure() {
        // Given

        // When
        Exception problem = catchException(() -> ingredientService.addAllergen(10000L, user));

        // Then
        assertThat(problem).isInstanceOf(IngredientNotFoundException.class);
    }

    @Test
    @Transactional
    public void givenIngredientId_whenRemoveAllergen_thenSuccess() {
        // Given
        Long ingredientId = ingredientService.addIngredient(
            Ingredient.builder().name("ingredient1").build()).getId();
        ingredientService.addAllergen(ingredientId, user);

        // When
        ingredientService.removeAllergen(ingredientId, user);
        List<Ingredient> allergens = ingredientService.getAllergens(user);

        // Then
        assertThat(allergens).map(Ingredient::getId).doesNotContain(ingredientId);
    }

    @Test
    public void givenNonExistingIngredientId_whenRemoveAllergen_thenFailure() {
        // Given

        Exception problem = catchException(() -> ingredientService.removeAllergen(10000L, user));

        // Then
        assertThat(problem).isInstanceOf(IngredientNotFoundException.class);
    }

    @Test
    public void givenUser_whenAddAllergens_thenGetAllergens() {
        // Given
        final long ingredient1Id = ingredientService.addIngredient(
            Ingredient.builder().name("ingredient1").build()).getId();
        final long ingredient2Id = ingredientService.addIngredient(
            Ingredient.builder().name("ingredient2").build()).getId();
        final long ingredient3Id = ingredientService.addIngredient(
            Ingredient.builder().name("ingredient3").build()).getId();
        final long ingredient4Id = ingredientService.addIngredient(
            Ingredient.builder().name("ingredient4").build()).getId();

        ingredientService.addAllergen(ingredient1Id, user);
        ingredientService.addAllergen(ingredient2Id, user);
        ingredientService.addAllergen(ingredient3Id, user);

        // When
        List<Long> allergens = ingredientService.getAllergens(user).stream()
            .map(Ingredient::getId).toList();

        // Then
        assertThat(allergens.size()).isEqualTo(3);
        assertThat(allergens).containsAll(
            List.of(ingredient1Id, ingredient2Id, ingredient3Id));
        assertThat(allergens).doesNotContain(ingredient4Id);
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
        final Long ingredient1Id = ingredientService.addIngredient(
            Ingredient.builder().name("LAURKA DLA MAMY").build()).getId();
        final Long ingredient2Id = ingredientService.addIngredient(
            Ingredient.builder().name("SULFUR").build()).getId();
        final Long ingredient3Id = ingredientService.addIngredient(
            Ingredient.builder().name("LAURYL SULFATE").build()).getId();
        final Long ingredient4Id = ingredientService.addIngredient(
            Ingredient.builder().name("LAURYL SULFIDE").build()).getId();

        String query = "LAU SUL";
        ingredientService.likeIngredient(ingredient4Id, user);

        // When
        List<Ingredient> ingredients =
            ingredientService.getIngredients(10, query, user, true);

        // Then
        assertThat(ingredients.size()).isEqualTo(4);
        assertThat(ingredients.getFirst().getId()).isEqualTo(ingredient4Id);
        assertThat(ingredients).map(Ingredient::getId).containsAll(
            List.of(ingredient1Id, ingredient2Id, ingredient3Id, ingredient4Id)
        );
    }

    @Test
    @WithMockUser(username = "user", password = "user")
    public void givenAllergens_whenSearch_thenAllergensAreSkipped() {
        // Given
        final Long ingredient1Id = ingredientService.addIngredient(
            Ingredient.builder().name("LAURKA DLA MAMY").build()).getId();
        final Long ingredient2Id = ingredientService.addIngredient(
            Ingredient.builder().name("SULFUR").build()).getId();
        final Long ingredient3Id = ingredientService.addIngredient(
            Ingredient.builder().name("LAURYL SULFATE").build()).getId();
        final Long ingredient4Id = ingredientService.addIngredient(
            Ingredient.builder().name("LAURYL SULFIDE").build()).getId();

        String query = "LAU SUL";
        ingredientService.addAllergen(ingredient3Id, user);

        // When
        List<Ingredient> ingredients =
            ingredientService.getIngredients(10, query, user, true);

        // Then
        assertThat(ingredients.size()).isEqualTo(3);
        assertThat(ingredients).map(Ingredient::getId).doesNotContain(ingredient3Id);
        assertThat(ingredients).map(Ingredient::getId).containsAll(
            List.of(ingredient1Id, ingredient2Id, ingredient4Id)
        );
    }
}
