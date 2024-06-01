package pl.edu.pw.mini.ingreedio.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pw.mini.ingreedio.api.IntegrationTest;
import pl.edu.pw.mini.ingreedio.api.auth.service.AuthService;
import pl.edu.pw.mini.ingreedio.api.ingredient.dto.IngredientDto;
import pl.edu.pw.mini.ingreedio.api.ingredient.model.Ingredient;
import pl.edu.pw.mini.ingreedio.api.ingredient.repository.IngredientRepository;
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
    @Transactional
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
    @Transactional
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
    @Transactional
    @WithMockUser(username = "user", password = "user")
    public void givenNonExistingIngredientId_whenLikeIngredient_thenFailure() {
        // Given

        // When
        boolean result = ingredientService.likeIngredient(10000L);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @Transactional
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
    @Transactional
    @WithMockUser(username = "user", password = "user")
    public void givenNonExistingIngredientId_whenUnLikeIngredient_thenFailure() {
        // Given

        // When
        boolean result = ingredientService.unlikeIngredient(10000L);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @Transactional
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
    @Transactional
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
    @Transactional
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
    @Transactional
    @WithMockUser(username = "user", password = "user")
    public void givenNonExistingIngredientId_whenRemoveAllergen_thenFailure() {
        // Given

        // When
        boolean result = ingredientService.removeAllergen(10000L);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @Transactional
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
    @Transactional
    public void givenQuery_whenSearch_thenCorrectResult() {
        // Given
        String query = "LAU SUL";
        int count = 10;

        // When
        List<IngredientDto> ingredients = ingredientService.getIngredients(count, query);

        // Then
        assertThat(ingredients.size()).isLessThanOrEqualTo(count);
        assertThat(ingredients.getFirst().id()).isEqualTo(3050L);
    }

    @Test
    @Transactional
    @WithMockUser(username = "user", password = "user")
    public void givenLiked_whenSearch_thenLikedArePromoted() {
        // Given
        String query = "LAU SUL";
        int count = 10;
        long liked = 2945L;
        ingredientService.likeIngredient(liked);
        User user = userService.getUserByUsername(authService.getCurrentUsername()).orElseThrow();

        // When
        List<IngredientDto> ingredients =
            ingredientService.getIngredients(count, query, user, true);

        // Then
        assertThat(ingredients.size()).isLessThanOrEqualTo(count);
        assertThat(ingredients.getFirst().id()).isEqualTo(liked);
    }

    @Test
    @Transactional
    @WithMockUser(username = "user", password = "user")
    public void givenAllergens_whenSearch_thenAllergensAreSkipped() {
        // Given
        String query = "LAU SUL";
        int count = 10;
        Set<Long> allergens = Set.of(3050L, 1899L, 2945L);
        allergens.forEach(ingredientService::addAllergen);
        User user = userService.getUserByUsername(authService.getCurrentUsername()).orElseThrow();

        // When
        List<IngredientDto> ingredients =
            ingredientService.getIngredients(count, query, user, true);

        // Then
        assertThat(ingredients.size()).isLessThanOrEqualTo(count);
        assertThat(ingredients).doesNotContainAnyElementsOf(
            allergens.stream()
                .map(ingredientService::getIngredientById)
                .map(Optional::orElseThrow)
                .toList());
    }
}
