package pl.edu.pw.mini.ingreedio.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.Set;
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
        Ingredient ingredient = Ingredient.builder().name("testIngredient").build();

        // When
        Ingredient savedIngredient = ingredientService.addIngredient(ingredient);

        // Then
        assertThat(savedIngredient).isNotNull();
        assertThat(savedIngredient.getName()).isEqualTo("testIngredient");
    }

    @Test
    @WithMockUser(username = "user", password = "user")
    public void givenIngredientId_whenLikeIngredient_thenSuccess() {
        // Given

        // When
        boolean result = ingredientService.likeIngredient(1L);

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

        // When
        boolean result = ingredientService.unlikeIngredient(1L);

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
        ingredientService.likeIngredient(1L);
        ingredientService.likeIngredient(2L);
        ingredientService.likeIngredient(3L);

        // When
        List<Long> likedIngredients = ingredientService.getLikedIngredients().stream()
            .map(IngredientDto::id).toList();

        // Then
        assertThat(likedIngredients.size()).isEqualTo(3);
        assertThat(likedIngredients.containsAll(List.of(1L, 2L, 3L))).isTrue();
        assertThat(likedIngredients.contains(4L)).isFalse();
    }

    @Test
    @WithMockUser(username = "user", password = "user")
    public void givenIngredientId_whenAddAllergen_thenSuccess() {
        // Given

        // When
        boolean result = ingredientService.addAllergen(1L);

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
    @WithMockUser(username = "user", password = "user")
    public void givenIngredientId_whenRemoveAllergen_thenSuccess() {
        // Given

        // When
        boolean result = ingredientService.removeAllergen(1L);

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
        ingredientService.addAllergen(1L);
        ingredientService.addAllergen(2L);
        ingredientService.addAllergen(3L);

        // When
        List<Long> allergens = ingredientService.getAllergens().stream()
            .map(IngredientDto::id).toList();

        // Then
        assertThat(allergens.size()).isEqualTo(3);
        assertThat(allergens.containsAll(List.of(1L, 2L, 3L))).isTrue();
        assertThat(allergens.contains(4L)).isFalse();
    }

    @Test
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
