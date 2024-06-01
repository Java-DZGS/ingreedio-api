package pl.edu.pw.mini.ingreedio.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pw.mini.ingreedio.api.IntegrationTest;
import pl.edu.pw.mini.ingreedio.api.auth.service.AuthService;
import pl.edu.pw.mini.ingreedio.api.ingredient.dto.IngredientDto;
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
    private AuthService authService;
    @Autowired
    private UserService userService;

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
    @WithMockUser(username = "user", password = "user")
    public void givenIngredientId_whenLikeIngredient_thenSuccess() {
        // Given
        Long ingredientId = ingredientService.addIngredient(
            Ingredient.builder().name("ingredient1").build()).getId();

        // When
        boolean result = ingredientService.likeIngredient(ingredientId);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @WithMockUser(username = "user", password = "user")
    public void givenNonExistingIngredientId_whenLikeIngredient_thenFailure() {
        // Given

        // When
        boolean result = ingredientService.likeIngredient(10000L);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @WithMockUser(username = "user", password = "user")
    public void givenIngredientId_whenUnLikeIngredient_thenSuccess() {
        // Given
        Long ingredientId = ingredientService.addIngredient(
            Ingredient.builder().name("ingredient1").build()).getId();
        ingredientService.likeIngredient(ingredientId);

        // When
        boolean result = ingredientService.unlikeIngredient(ingredientId);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @WithMockUser(username = "user", password = "user")
    public void givenNonExistingIngredientId_whenUnLikeIngredient_thenFailure() {
        // Given

        // When
        boolean result = ingredientService.unlikeIngredient(10000L);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @WithMockUser(username = "user", password = "user")
    public void givenUser_whenLikeIngredients_thenGetLikedIngredients() {
        // Given
        Long ingredient1Id = ingredientService.addIngredient(
            Ingredient.builder().name("ingredient1").build()).getId();
        Long ingredient2Id = ingredientService.addIngredient(
            Ingredient.builder().name("ingredient2").build()).getId();
        Long ingredient3Id = ingredientService.addIngredient(
            Ingredient.builder().name("ingredient3").build()).getId();
        Long ingredient4Id = ingredientService.addIngredient(
            Ingredient.builder().name("ingredient4").build()).getId();

        ingredientService.likeIngredient(ingredient1Id);
        ingredientService.likeIngredient(ingredient2Id);
        ingredientService.likeIngredient(ingredient3Id);

        // When
        List<Long> likedIngredients = ingredientService.getLikedIngredients().stream()
            .map(IngredientDto::id).toList();

        // Then
        assertThat(likedIngredients.size()).isEqualTo(3);
        assertThat(likedIngredients.containsAll(
            List.of(ingredient1Id, ingredient2Id, ingredient1Id))).isTrue();
        assertThat(likedIngredients.contains(ingredient4Id)).isFalse();
    }

    @Test
    @WithMockUser(username = "user", password = "user")
    public void givenIngredientId_whenAddAllergen_thenSuccess() {
        // Given
        Long ingredient1Id = ingredientService.addIngredient(
            Ingredient.builder().name("ingredient1").build()).getId();

        // When
        boolean result = ingredientService.addAllergen(ingredient1Id);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @WithMockUser(username = "user", password = "user")
    public void givenNonExistingIngredientId_whenAddAllergen_thenFailure() {
        // Given

        // When
        boolean result = ingredientService.addAllergen(10000L);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @Transactional
    @WithMockUser(username = "user", password = "user")
    public void givenIngredientId_whenRemoveAllergen_thenSuccess() {
        // Given
        Long ingredientId = ingredientService.addIngredient(
            Ingredient.builder().name("ingredient1").build()).getId();
        ingredientService.addAllergen(ingredientId);

        // When
        boolean result = ingredientService.removeAllergen(ingredientId);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @WithMockUser(username = "user", password = "user")
    public void givenNonExistingIngredientId_whenRemoveAllergen_thenFailure() {
        // Given

        // When
        boolean result = ingredientService.removeAllergen(10000L);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @WithMockUser(username = "user", password = "user")
    public void givenUser_whenAddAllergens_thenGetAllergens() {
        // Given
        Long ingredient1Id = ingredientService.addIngredient(
            Ingredient.builder().name("ingredient1").build()).getId();
        Long ingredient2Id = ingredientService.addIngredient(
            Ingredient.builder().name("ingredient2").build()).getId();
        Long ingredient3Id = ingredientService.addIngredient(
            Ingredient.builder().name("ingredient3").build()).getId();
        Long ingredient4Id = ingredientService.addIngredient(
            Ingredient.builder().name("ingredient4").build()).getId();

        ingredientService.addAllergen(ingredient1Id);
        ingredientService.addAllergen(ingredient2Id);
        ingredientService.addAllergen(ingredient3Id);

        // When
        List<Long> allergens = ingredientService.getAllergens().stream()
            .map(IngredientDto::id).toList();

        // Then
        assertThat(allergens.size()).isEqualTo(3);
        assertThat(allergens.containsAll(
            List.of(ingredient1Id, ingredient2Id, ingredient3Id))).isTrue();
        assertThat(allergens.contains(ingredient4Id)).isFalse();
    }

    @Test
    public void givenQuery_whenSearch_thenCorrectResult() {
        // Given
        Long ingredient1Id = ingredientService.addIngredient(
            Ingredient.builder().name("LAURKA DLA MAMY").build()).getId();
        Long ingredient2Id = ingredientService.addIngredient(
            Ingredient.builder().name("SULFUR").build()).getId();
        Long ingredient3Id = ingredientService.addIngredient(
            Ingredient.builder().name("LAURYL SULFATE").build()).getId();
        Long ingredient4Id = ingredientService.addIngredient(
            Ingredient.builder().name("KLAU FSUL").build()).getId();
        Long ingredient5Id = ingredientService.addIngredient(
            Ingredient.builder().name("LAURYL SULFIDE").build()).getId();

        String query = "LAU SUL";

        // When
        List<IngredientDto> ingredients = ingredientService.getIngredients(10, query);

        // Then
        assertThat(ingredients.size()).isEqualTo(4);
        assertThat(ingredients.getFirst().id()).isEqualTo(ingredient3Id);
        assertThat(ingredients).map(IngredientDto::id).containsAll(
            List.of(ingredient1Id, ingredient2Id, ingredient3Id, ingredient5Id)
        );
        assertThat(ingredients).map(IngredientDto::id).doesNotContain(ingredient4Id);
    }

    @Test
    @WithMockUser(username = "user", password = "user")
    public void givenLiked_whenSearch_thenLikedArePromoted() {
        // Given
        Long ingredient1Id = ingredientService.addIngredient(
            Ingredient.builder().name("LAURKA DLA MAMY").build()).getId();
        Long ingredient2Id = ingredientService.addIngredient(
            Ingredient.builder().name("SULFUR").build()).getId();
        Long ingredient3Id = ingredientService.addIngredient(
            Ingredient.builder().name("LAURYL SULFATE").build()).getId();
        Long ingredient4Id = ingredientService.addIngredient(
            Ingredient.builder().name("LAURYL SULFIDE").build()).getId();

        String query = "LAU SUL";
        ingredientService.likeIngredient(ingredient4Id);
        User user = userService.getUserByUsername(authService.getCurrentUsername()).orElseThrow();

        // When
        List<IngredientDto> ingredients =
            ingredientService.getIngredients(10, query, user, true);

        // Then
        assertThat(ingredients.size()).isEqualTo(4);
        assertThat(ingredients.getFirst().id()).isEqualTo(ingredient4Id);
        assertThat(ingredients).map(IngredientDto::id).containsAll(
            List.of(ingredient1Id, ingredient2Id, ingredient3Id, ingredient4Id)
        );
    }

    @Test
    @WithMockUser(username = "user", password = "user")
    public void givenAllergens_whenSearch_thenAllergensAreSkipped() {
        // Given
        Long ingredient1Id = ingredientService.addIngredient(
            Ingredient.builder().name("LAURKA DLA MAMY").build()).getId();
        Long ingredient2Id = ingredientService.addIngredient(
            Ingredient.builder().name("SULFUR").build()).getId();
        Long ingredient3Id = ingredientService.addIngredient(
            Ingredient.builder().name("LAURYL SULFATE").build()).getId();
        Long ingredient4Id = ingredientService.addIngredient(
            Ingredient.builder().name("LAURYL SULFIDE").build()).getId();

        String query = "LAU SUL";
        ingredientService.addAllergen(ingredient3Id);
        User user = userService.getUserByUsername(authService.getCurrentUsername()).orElseThrow();

        // When
        List<IngredientDto> ingredients =
            ingredientService.getIngredients(10, query, user, true);

        // Then
        assertThat(ingredients.size()).isEqualTo(3);
        assertThat(ingredients).map(IngredientDto::id).doesNotContain(ingredient3Id);
        assertThat(ingredients).map(IngredientDto::id).containsAll(
            List.of(ingredient1Id, ingredient2Id, ingredient4Id)
        );
    }
}
