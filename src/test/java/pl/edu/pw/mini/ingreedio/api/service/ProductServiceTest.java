package pl.edu.pw.mini.ingreedio.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pw.mini.ingreedio.api.IntegrationTest;
import pl.edu.pw.mini.ingreedio.api.product.criteria.ProductCriteria;
import pl.edu.pw.mini.ingreedio.api.product.criteria.ProductsSortingCriteria;
import pl.edu.pw.mini.ingreedio.api.product.dto.FullProductDto;
import pl.edu.pw.mini.ingreedio.api.product.dto.ProductDto;
import pl.edu.pw.mini.ingreedio.api.product.dto.ProductListResponseDto;
import pl.edu.pw.mini.ingreedio.api.product.model.Product;
import pl.edu.pw.mini.ingreedio.api.product.repository.ProductRepository;
import pl.edu.pw.mini.ingreedio.api.product.service.ProductService;

public class ProductServiceTest extends IntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Please refer to <a href="https://github.com/Java-DZGS/ingreedio-api/pull/81#issuecomment-2115445411">this</a> PR comment.
     */
    @AfterEach
    void clearProducts() {
        productRepository.deleteAll();
    }

    @Nested
    @Transactional
    class AddAndGetTests {
        @Test
        public void givenProductObject_whenSaveProduct_thenReturnProductObject() {
            // Given
            Product product = Product.builder().name("testProduct").build();

            // When
            Product savedProduct = productService.addProduct(product);

            // Then
            assertThat(savedProduct).isNotNull();
        }

        @Test
        public void givenProductsList_whenGetAllProducts_thenReturnProductsList() {
            // Given
            productService.addProduct(Product.builder().name("testProduct1").build());
            productService.addProduct(Product.builder().name("testProduct2").build());
            productService.addProduct(Product.builder().name("testProduct3").build());

            // When
            List<ProductDto> productsList = productService.getAllProducts();

            // Then
            assertThat(productsList).isNotNull();
            assertThat(productsList.size()).isEqualTo(3);
        }

        @Test
        public void givenProductId_whenGetProductById_thenReturnFullProductDtoObject() {
            // Given
            Product product = productService
                .addProduct(Product.builder().name("testProduct1").build());

            // When
            FullProductDto fullProductDto = productService.getProductById(product.getId())
                .orElseThrow();

            // Then
            assertThat(fullProductDto).isNotNull();
            assertThat(fullProductDto.id()).isEqualTo(product.getId());
            assertThat(fullProductDto.name()).isEqualTo("testProduct1");
        }
    }

    @Nested
    @Transactional
    class FilteringTests {
        @Test
        public void givenNoCriteria_whenMatch_thenReturnAllProducts() {
            // Given
            productService.addProduct(Product.builder().name("testProduct1").build());
            productService.addProduct(Product.builder().name("testProduct2").build());
            productService.addProduct(Product.builder().name("testProduct3").build());

            var criteria = ProductCriteria.builder().build();

            // When
            ProductListResponseDto page = productService.getProductsMatchingCriteria(
                criteria, PageRequest.of(0, 10));

            // Then
            assertThat(page).isNotNull();
            assertThat(page.products().size()).isEqualTo(3);
        }

        @Test
        public void givenBrandsIncludeCriteria_whenFilter_thenReturnCorrectProducts() {
            // Given
            productService.addProduct(Product.builder().brand("daglas").build()); // +1
            productService.addProduct(Product.builder().brand("nivea").build()); // +1
            productService.addProduct(Product.builder().name("daglas").brand("ddaglas").build());
            productService.addProduct(Product.builder().provider("daglas & co.").build());

            var criteria = ProductCriteria.builder()
                .brandsNamesToInclude(Set.of("daglas", "nivea")).build();

            // When
            ProductListResponseDto page = productService.getProductsMatchingCriteria(
                criteria, PageRequest.of(0, 10));

            // Then
            assertThat(page.products().size()).isEqualTo(2);

            for (ProductDto productDto : page.products()) {
                Optional<FullProductDto> product = productService.getProductById(productDto.id());

                assertThat(product).isPresent();
                assertThat(product.get().brand()).isIn("daglas", "nivea");
            }
        }

        @Test
        public void givenBrandsExcludeCriteria_whenFilter_thenReturnCorrectProducts() {
            // Given
            productService.addProduct(Product.builder().brand("daglas").build()); // +1
            productService.addProduct(Product.builder().brand("nivea").build()); // +1
            productService.addProduct(Product.builder().name("perfume").brand("adidas").build());
            productService.addProduct(Product.builder().provider("daglas & co.").build());

            var criteria = ProductCriteria.builder()
                .brandsNamesToExclude(Set.of("adidas", "nivea")).build();

            // When
            ProductListResponseDto page = productService.getProductsMatchingCriteria(
                criteria, PageRequest.of(0, 10));

            // Then
            assertThat(page.products().size()).isEqualTo(2);

            for (ProductDto productDto : page.products()) {
                Optional<FullProductDto> product = productService.getProductById(productDto.id());

                assertThat(product).isPresent();
                assertThat(product.get().brand()).isNotIn("daglas & co.", "nivea");
            }
        }

        @Test
        public void givenIngredientsIncludeCriteria_whenFilter_thenReturnCorrectProducts() {
            // Given
            productService.addProduct(Product.builder().brand("carfour")
                .ingredients(List.of("potato", "carrot", "beet")).build());
            productService.addProduct(Product.builder().brand("carfour")
                .ingredients(List.of("beet")).build());
            productService.addProduct(Product.builder().brand("carfour")
                .ingredients(List.of("potato", "carrot")).build());
            productService.addProduct(Product.builder().brand("carfour")
                .ingredients(List.of("carrot", "beet")).build());
            productService.addProduct(Product.builder().brand("carfour")
                .ingredients(List.of("potato", "beet")).build());

            var criteria1 = ProductCriteria.builder()
                .ingredientsNamesToInclude(Set.of("potato", "beet")).build();
            var criteria2 = ProductCriteria.builder()
                .ingredientsNamesToInclude(Set.of("potato")).build();

            // When
            ProductListResponseDto potatoBeet =
                productService.getProductsMatchingCriteria(criteria1,
                    PageRequest.of(0, 16));

            ProductListResponseDto potato = productService.getProductsMatchingCriteria(criteria2,
                PageRequest.of(0, 16));

            // Then
            assertThat(potatoBeet.products().size()).isEqualTo(2);
            for (ProductDto productDto : potatoBeet.products()) {
                Optional<FullProductDto> product = productService.getProductById(productDto.id());

                assertThat(product).isPresent();
                assertThat(product.get().ingredients()).contains("potato", "beet");
            }

            assertThat(potato.products().size()).isEqualTo(3);
            for (ProductDto productDto : potato.products()) {
                Optional<FullProductDto> product = productService.getProductById(productDto.id());

                assertThat(product).isPresent();
                assertThat(product.get().ingredients()).contains("potato");
            }
        }

        @Test
        @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
        public void givenIngredientsExcludeCriteria_whenFilter_thenReturnCorrectProducts() {
            // Given
            productService.addProduct(Product.builder().brand("carfour")
                .ingredients(List.of("potato", "carrot", "beet")).build());
            productService.addProduct(Product.builder().brand("carfour")
                .ingredients(List.of("beet", "tomato")).build());
            productService.addProduct(Product.builder().brand("carfour")
                .ingredients(List.of("potato", "carrot")).build());
            productService.addProduct(Product.builder().brand("carfour")
                .ingredients(List.of("carrot", "beet")).build());
            productService.addProduct(Product.builder().brand("carfour")
                .ingredients(List.of("potato", "beet")).build());

            var criteria1 = ProductCriteria.builder()
                .ingredientsNamesToExclude(Set.of("potato", "beet")).build();
            var criteria2 = ProductCriteria.builder()
                .ingredientsNamesToExclude(Set.of("potato")).build();
            var criteria3 = ProductCriteria.builder()
                .ingredientsNamesToExclude(Set.of("carrot", "tomato")).build();

            // When
            ProductListResponseDto potatoBeet = productService
                .getProductsMatchingCriteria(criteria1, PageRequest.of(0, 16));

            ProductListResponseDto potato = productService.getProductsMatchingCriteria(criteria2,
                PageRequest.of(0, 16));

            ProductListResponseDto carrotTomato = productService
                .getProductsMatchingCriteria(criteria3, PageRequest.of(0, 16));

            // Then
            assertThat(potatoBeet.products().size()).isEqualTo(0);

            assertThat(potato.products().size()).isEqualTo(2);
            for (ProductDto productDto : potato.products()) {
                Optional<FullProductDto> product = productService.getProductById(productDto.id());

                assertThat(product).isPresent();
                assertThat(product.get().ingredients()).doesNotContain("potato");
            }

            assertThat(carrotTomato.products().size()).isEqualTo(1);
            for (ProductDto productDto : carrotTomato.products()) {
                Optional<FullProductDto> product = productService.getProductById(productDto.id());

                assertThat(product).isPresent();
                assertThat(product.get().ingredients()).doesNotContain("carrot", "tomato");
            }
        }

        @Test
        public void givenRatingIngredientsCriteria_whenFilter_thenReturnCorrectProducts() {
            // Given
            productService.addProduct(Product.builder().brand("carfour")
                .ingredients(List.of("potato", "carrot", "beet")).build());
            productService.addProduct(Product.builder().brand("carfour")
                .ingredients(List.of("beet", "tomato")).build());
            productService.addProduct(Product.builder().brand("carfour")
                .ingredients(List.of("potato", "carrot")).build());
            productService.addProduct(Product.builder().brand("carfour")
                .ingredients(List.of("carrot", "beet")).build());
            productService.addProduct(Product.builder().brand("carfour")
                .ingredients(List.of("potato")).rating(6).build());
            productService.addProduct(Product.builder().brand("carfour")
                .ingredients(List.of("potato", "beet")).rating(7).build());
            productService.addProduct(Product.builder().brand("carfour")
                .ingredients(List.of("beet")).rating(10).build());
            productService.addProduct(Product.builder().brand("carfour")
                .ingredients(List.of("potato", "beet")).rating(10).build());

            var criteria = ProductCriteria.builder()
                .ingredientsNamesToInclude(Set.of("potato"))
                .ingredientsNamesToExclude(Set.of("carrot", "tomato"))
                .minRating(7)
                .build();

            // When
            ProductListResponseDto result = productService.getProductsMatchingCriteria(criteria,
                PageRequest.of(0, 16));

            assertThat(result.products().size()).isEqualTo(2);
            for (ProductDto productDto : result.products()) {
                Optional<FullProductDto> product = productService.getProductById(productDto.id());

                assertThat(product).isPresent();
                assertThat(product.get().ingredients()).doesNotContain("carrot", "tomato");
                assertThat(product.get().ingredients()).contains("potato");
                assertThat(product.get().rating()).isGreaterThanOrEqualTo(7);
            }
        }

        @Test
        public void givenMultiCriteria_whenFilter_thenReturnCorrectProducts() {
            // Given

            // Proper
            productService.addProduct(
                Product.builder()
                    .name("pasta do zębów")
                    .brand("karfur")
                    .provider("żapka")
                    .ingredients(List.of("polietylen", "guma guar", "metanol"))
                    .build());
            productService.addProduct(
                Product.builder()
                    .name("poper")
                    .brand("karfur")
                    .provider("żapka")
                    .ingredients(List.of("rak", "guma", "metanol"))
                    .build());

            // Red herrings
            productService.addProduct(
                Product.builder().name("ziemniak").brand("karfur").provider("żapka")
                    .build());
            productService.addProduct(
                Product.builder().name("obrazek").brand("karfur").provider("żapka")
                    .build());
            productService.addProduct(
                Product.builder().name("szampą").brand("żapka").provider("karfur")
                    .build());
            productService.addProduct(
                Product.builder().name("szamka").brand("grycan").provider("żapka").build());
            productService.addProduct(
                Product.builder().name("baton").brand("sniker").provider("żapka").build());
            productService.addProduct(
                Product.builder().name("marchew").brand("ogródek").provider("rosman")
                    .build());
            productService.addProduct(
                Product.builder().name("pianka do golenia").brand("golibroda").provider("romsan")
                    .build());
            productService.addProduct(
                Product.builder().name("pasta do zębów").brand("kolgat").provider("romsan")
                    .build());
            productService.addProduct(
                Product.builder().name("pasta do zębów").brand("sęsodę").provider("romsan")
                    .build());
            productService.addProduct(
                Product.builder().name("pasta do zębów").brand("elmech").provider("romsan")
                    .build());
            productService.addProduct(
                Product.builder().name("pasta do zębów").brand("akuafresz").provider("romsan")
                    .build());
            productService.addProduct(
                Product.builder().name("pasta do butów").brand("kiwi").provider("romsan")
                    .build());

            var criteria = ProductCriteria.builder()
                .brandsNamesToInclude(Set.of("karfur"))
                .providersNames(Set.of("żapka"))
                .ingredientsNamesToInclude(Set.of("metanol"))
                .build();

            // When
            var kerfurZabkaPage = productService.getProductsMatchingCriteria(
                criteria, PageRequest.of(0, 30));
            List<ProductDto> karfurZapkaProducts = kerfurZabkaPage.products();

            // Then
            assertThat(karfurZapkaProducts.size()).isEqualTo(2);

            for (ProductDto productDto : karfurZapkaProducts) {
                Optional<FullProductDto> product = productService.getProductById(productDto.id());

                assertThat(product).isPresent();
                assertThat(product.get().provider()).isEqualTo("żapka");
                assertThat(product.get().brand()).isEqualTo("karfur");
                assertThat(product.get().ingredients()).contains("metanol");
            }
        }
    }

    @Nested
    @Transactional
    class PhraseAndSortingTests {
        @Test
        public void givenProducts_whenMatchSort_thenProductWithGreaterMatchScoreIsFirst() {
            // Given
            productService.addProduct(Product.builder().name("almette")
                .shortDescription("krem kremik kremiasty").build());
            productService.addProduct(Product.builder().name("parmezan")
                .shortDescription("krem kremik").build());
            productService.addProduct(Product.builder().name("serek")
                .shortDescription("krem do stóp").build());

            var sortingCriteria =
                new ProductsSortingCriteria(Sort.Direction.DESC, "matchScore");
            var criteria = ProductCriteria.builder()
                .phraseKeywords(Set.of("krem"))
                .hasMatchScoreSortCriteria(true)
                .sortingCriteria(List.of(sortingCriteria))
                .build();

            // When
            var result = productService.getProductsMatchingCriteria(
                criteria, PageRequest.of(0, 30));

            // Then
            assertThat(result.products().get(0).name()).isEqualTo("almette");
            assertThat(result.products().get(1).name()).isEqualTo("parmezan");
            assertThat(result.products().get(2).name()).isEqualTo("serek");
        }

        @Test
        public void givenMultiCaseFields_whenSearch_thenResultIsCaseInsensitive() {
            // Given
            productService.addProduct(Product.builder().name("aLmeTTe")
                .shortDescription("MaśĆ").build());
            productService.addProduct(Product.builder().name("ParMez")
                .shortDescription("SZamPoNik").build());
            productService.addProduct(Product.builder().name("SErEk")
                .shortDescription("kRem do stóp").build());

            var criteria = ProductCriteria.builder()
                .phraseKeywords(Set.of("krem", "serek"))
                .build();

            // When
            var result = productService.getProductsMatchingCriteria(
                criteria, PageRequest.of(0, 30));

            // Then
            assertThat(result.products().size()).isEqualTo(1);
            assertThat(result.products().getFirst().name()).isEqualTo("SErEk");
        }
    }

    @Nested
    @Transactional
    class ProductsLikingTests {
        @Test
        @WithMockUser(username = "user", password = "user", roles = {})
        public void givenProduct_whenLikeProduct_thenSuccess() {
            // Given
            Product product = Product.builder().name("likedProduct").build();
            Product savedProduct = productService.addProduct(product);
            Long id = savedProduct.getId();

            // When
            boolean result = productService.likeProduct(id);
            Optional<FullProductDto> updatedProduct = productService.getProductById(id);

            // Then
            assertTrue(result);
            assertTrue(updatedProduct.isPresent());
            assertTrue(updatedProduct.get().isLiked());
        }

        @Test
        @WithMockUser(username = "user", password = "user", roles = {})
        public void givenProduct_whenLikeNonExistingProduct_thenFailure() {
            // Given

            // When
            boolean result = productService.likeProduct(1000L);

            // Then
            assertFalse(result);
        }

        @Test
        @WithMockUser(username = "user", password = "user", roles = {})
        public void givenProduct_whenUnLikeProduct_thenSuccess() {
            // Given
            Product product = Product.builder().name("likedProduct").build();
            Product savedProduct = productService.addProduct(product);
            Long id = savedProduct.getId();

            // When
            productService.likeProduct(id);
            boolean result = productService.unlikeProduct(id);
            Optional<FullProductDto> updatedProduct = productService.getProductById(id);

            // Then
            assertTrue(result);
            assertTrue(updatedProduct.isPresent());
            assertFalse(updatedProduct.get().isLiked());
        }

        @Test
        @WithMockUser(username = "user", password = "user", roles = {})
        public void givenProduct_whenUnLikeNonExistingProduct_thenFailure() {
            // Given

            // When
            boolean result = productService.unlikeProduct(1000L);

            // Then
            assertFalse(result);
        }

        @Test
        @WithMockUser(username = "user", password = "user", roles = {})
        public void givenProduct_whenLikeAndGetProductsList_thenProductsAreLiked() {
            // Given
            Product product1 = productService
                .addProduct(Product.builder().name("likedProduct1").build());
            Product product2 = productService
                .addProduct(Product.builder().name("likedProduct2").build());
            productService.addProduct(Product.builder().name("likedProduct3").build());

            // When
            productService.likeProduct(product1.getId());
            productService.likeProduct(product2.getId());

            ProductListResponseDto page = productService.getProductsMatchingCriteria(
                ProductCriteria.builder().build(), PageRequest.of(0, 30));
            List<ProductDto> products = page.products();

            // Then
            assertThat(products.getFirst().isLiked()).isTrue();
            assertThat(products.get(1).isLiked()).isTrue();
            assertThat(products.get(2).isLiked()).isFalse();
        }

        @Test
        @WithMockUser(username = "user", password = "user", roles = {})
        public void givenProduct_whenLikeAndGetProductDetails_thenProductIsLiked() {
            // Given
            Product product = productService
                .addProduct(Product.builder().name("likedProduct").build());

            // When
            productService.likeProduct(product.getId());
            Optional<FullProductDto> result = productService.getProductById(product.getId());

            // Then
            assertTrue(result.isPresent());
            assertThat(result.get().isLiked()).isTrue();
        }
    }

    @Nested
    @Transactional
    class EditAndDeleteTest {
        @Test
        public void givenProductId_whenEditProduct_thenProductIsDeleted() {
            Product productToEdit = productService.addProduct(Product.builder()
                .name("productToEdit")
                .smallImageUrl("oldSmallImageUrl")
                .largeImageUrl("oldLargeImageUrl")
                .provider("oldProvider")
                .brand("oldBrand")
                .shortDescription("oldShortDescription")
                .longDescription("oldLongDescription")
                .volume("oldVolume")
                .ingredients(List.of("oldIngredient1", "oldIngredient2"))
                .build());

            Product productEdited = Product.builder()
                .name("productEdited")
                .smallImageUrl("newSmallImageUrl")
                .largeImageUrl("newLargeImageUrl")
                .provider("newProvider")
                .brand("newBrand")
                .shortDescription("newShortDescription")
                .longDescription("newLongDescription")
                .volume("newVolume")
                .ingredients(List.of("newIngredient1", "newIngredient2"))
                .build();

            // When
            Optional<Product> editedProduct = productService
                .editProduct(productToEdit.getId(), productEdited);

            // Then
            assertThat(editedProduct.isPresent()).isTrue();
            assertThat(editedProduct.get().getId()).isEqualTo(productToEdit.getId());
            assertThat(editedProduct.get().getName()).isEqualTo("productEdited");
            assertThat(editedProduct.get().getSmallImageUrl())
                .isEqualTo("newSmallImageUrl");
            assertThat(editedProduct.get().getLargeImageUrl())
                .isEqualTo("newLargeImageUrl");
            assertThat(editedProduct.get().getProvider()).isEqualTo("newProvider");
            assertThat(editedProduct.get().getBrand()).isEqualTo("newBrand");
            assertThat(editedProduct.get().getShortDescription())
                .isEqualTo("newShortDescription");
            assertThat(editedProduct.get().getLongDescription())
                .isEqualTo("newLongDescription");
            assertThat(editedProduct.get().getVolume()).isEqualTo("newVolume");
            assertThat(editedProduct.get().getIngredients())
                .containsExactly("newIngredient1", "newIngredient2");
        }

        @Test
        public void givenProductId_whenEditNonExistingProduct_thenReturnFalse() {
            // Given
            Product editedProduct = productService.addProduct(Product.builder()
                .name("edited").build());

            // When
            Optional<Product> edited = productService.editProduct(1000L, editedProduct);
            Optional<FullProductDto> result = productService.getProductById(1000L);

            // Then
            assertThat(edited.isPresent()).isFalse();
            assertThat(result.isPresent()).isFalse();
        }

        @Test
        public void givenProductId_whenDeleteProduct_thenProductIsDeleted() {
            // Given
            Product product = productService
                .addProduct(Product.builder().name("productToDelete").build());

            // When
            boolean deleted = productService.deleteProduct(product.getId());
            Optional<FullProductDto> result = productService.getProductById(product.getId());

            // Then
            assertThat(deleted).isTrue();
            assertThat(result.isPresent()).isFalse();
        }

        @Test
        public void givenProductId_whenDeleteNonExistingProduct_thenReturnFalse() {
            // Given

            // When
            boolean deleted = productService.deleteProduct(1000L);

            // Then
            assertThat(deleted).isFalse();
        }
    }
}
