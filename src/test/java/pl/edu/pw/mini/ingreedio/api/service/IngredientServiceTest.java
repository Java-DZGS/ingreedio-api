package pl.edu.pw.mini.ingreedio.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import pl.edu.pw.mini.ingreedio.api.IntegrationTest;
import pl.edu.pw.mini.ingreedio.api.dto.IngredientDto;
import pl.edu.pw.mini.ingreedio.api.model.Ingredient;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IngredientServiceTest extends IntegrationTest {
    @Autowired
    IngredientService ingredientService;

    @Test
    @Order(1)
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
    @Order(2)
    public void givenIngredientId_whenLikeIngredient_thenSuccess() {
        // Given

        // When
        boolean result = ingredientService.likeIngredient(1L);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @WithMockUser(username = "user", password = "user")
    @Order(3)
    public void givenNonExistingIngredientId_whenLikeIngredient_thenFailure() {
        // Given

        // When
        boolean result = ingredientService.likeIngredient(10000L);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @WithMockUser(username = "user", password = "user")
    @Order(4)
    public void givenIngredientId_whenUnLikeIngredient_thenSuccess() {
        // Given

        // When
        boolean result = ingredientService.unlikeIngredient(1L);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @WithMockUser(username = "user", password = "user")
    @Order(5)
    public void givenNonExistingIngredientId_whenUnLikeIngredient_thenFailure() {
        // Given

        // When
        boolean result = ingredientService.unlikeIngredient(10000L);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @WithMockUser(username = "user", password = "user")
    @Order(6)
    public void givenUser_whenLikeIngredients_thenGetLikedIngredients() {
        // Given

        // When
        ingredientService.likeIngredient(1L);
        ingredientService.likeIngredient(2L);
        ingredientService.likeIngredient(3L);
        List<IngredientDto> likedIngredients = ingredientService.getLikedIngredients();

        // Then
        assertThat(likedIngredients.size()).isEqualTo(3);
        assertThat(likedIngredients.stream().anyMatch(i -> i.id().equals(1L))).isTrue();
        assertThat(likedIngredients.stream().anyMatch(i -> i.id().equals(2L))).isTrue();
        assertThat(likedIngredients.stream().anyMatch(i -> i.id().equals(3L))).isTrue();
        assertThat(likedIngredients.stream().anyMatch(i -> i.id().equals(4L))).isFalse();
    }

    @Test
    @WithMockUser(username = "user", password = "user")
    @Order(7)
    public void givenIngredientId_whenAddAllergen_thenSuccess() {
        // Given

        // When
        boolean result = ingredientService.addAllergen(1L);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @WithMockUser(username = "user", password = "user")
    @Order(8)
    public void givenNonExistingIngredientId_whenAddAllergen_thenFailure() {
        // Given

        // When
        boolean result = ingredientService.addAllergen(10000L);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @WithMockUser(username = "user", password = "user")
    @Order(9)
    public void givenIngredientId_whenRemoveallergen_thenSuccess() {
        // Given

        // When
        boolean result = ingredientService.removeAllergen(1L);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @WithMockUser(username = "user", password = "user")
    @Order(10)
    public void givenNonExistingIngredientId_whenRemoveAllergen_thenFailure() {
        // Given

        // When
        boolean result = ingredientService.removeAllergen(10000L);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @WithMockUser(username = "user", password = "user")
    @Order(11)
    public void givenUser_whenAddAllergens_thenGetAllergens() {
        // Given

        // When
        ingredientService.addAllergen(1L);
        ingredientService.addAllergen(2L);
        ingredientService.addAllergen(3L);
        List<IngredientDto> allergens = ingredientService.getAllergens();

        // Then
        assertThat(allergens.size()).isEqualTo(3);
        assertThat(allergens.stream().anyMatch(i -> i.id().equals(1L))).isTrue();
        assertThat(allergens.stream().anyMatch(i -> i.id().equals(2L))).isTrue();
        assertThat(allergens.stream().anyMatch(i -> i.id().equals(3L))).isTrue();
        assertThat(allergens.stream().anyMatch(i -> i.id().equals(4L))).isFalse();
    }
}
