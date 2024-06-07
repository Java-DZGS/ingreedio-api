package pl.edu.pw.mini.ingreedio.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pw.mini.ingreedio.api.IntegrationTest;
import pl.edu.pw.mini.ingreedio.api.product.criteria.ProductCriteria;
import pl.edu.pw.mini.ingreedio.api.product.exception.ProductNotFoundException;
import pl.edu.pw.mini.ingreedio.api.product.model.BrandDocument;
import pl.edu.pw.mini.ingreedio.api.product.model.CategoryDocument;
import pl.edu.pw.mini.ingreedio.api.product.model.IngredientDocument;
import pl.edu.pw.mini.ingreedio.api.product.model.ProductDocument;
import pl.edu.pw.mini.ingreedio.api.product.model.ProviderDocument;
import pl.edu.pw.mini.ingreedio.api.product.repository.ProductRepository;
import pl.edu.pw.mini.ingreedio.api.product.service.ProductCriteriaService;
import pl.edu.pw.mini.ingreedio.api.product.service.ProductService;
import pl.edu.pw.mini.ingreedio.api.review.dto.ReviewDto;
import pl.edu.pw.mini.ingreedio.api.review.model.Review;
import pl.edu.pw.mini.ingreedio.api.user.model.User;
import pl.edu.pw.mini.ingreedio.api.user.service.UserService;

public class ProductServiceTest extends IntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductCriteriaService productCriteriaService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserService userService;

    private User user;

    @BeforeEach
    public void setupData() {
        user = userService.getUserByUsername("user");
    }

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
            ProductDocument product = ProductDocument.builder().name("testProduct").build();

            // When
            ProductDocument savedProduct = productService.addProduct(product);

            // Then
            assertThat(savedProduct).isNotNull();
        }

        @Test
        public void givenProductsList_whenGetAllProducts_thenReturnProductsList() {
            // Given
            productService.addProduct(ProductDocument.builder().name("testProduct1").build());
            productService.addProduct(ProductDocument.builder().name("testProduct2").build());
            productService.addProduct(ProductDocument.builder().name("testProduct3").build());

            // When
            List<ProductDocument> productsList = productService.getAllProducts();

            // Then
            assertThat(productsList).isNotNull();
            assertThat(productsList.size()).isEqualTo(3);
        }

        @Test
        public void givenProductId_whenGetProductById_thenReturnFullProductDtoObject() {
            // Given
            ProductDocument product = productService
                .addProduct(ProductDocument.builder().name("testProduct1").build());

            // When
            ProductDocument productResult = productService.getProductById(product.getId());

            // Then
            assertThat(productResult).isNotNull();
            assertThat(productResult.getId()).isEqualTo(product.getId());
            assertThat(productResult.getName()).isEqualTo("testProduct1");
        }
    }

    @Nested
    @Transactional
    class FilteringTests {
        @Test
        public void givenNoCriteria_whenMatch_thenReturnAllProducts() {
            // Given
            productService.addProduct(ProductDocument.builder().name("testProduct1").build());
            productService.addProduct(ProductDocument.builder().name("testProduct2").build());
            productService.addProduct(ProductDocument.builder().name("testProduct3").build());

            var criteria = ProductCriteria.builder().build();

            // When
            Page<ProductDocument> page = productService.getProductsMatchingCriteria(
                criteria, PageRequest.of(0, 10));

            // Then
            assertThat(page).isNotNull();
            assertThat(page.getContent().size()).isEqualTo(3);
        }

        @Test
        public void givenBrandsIncludeCriteria_whenFilter_thenReturnCorrectProducts() {
            // Given
            BrandDocument daglas = BrandDocument.builder().id(1L).name("daglas").build();
            BrandDocument hit = BrandDocument.builder().id(1L).name("hit").build();
            BrandDocument nivea = BrandDocument.builder().id(2L).name("nivea").build();
            ProviderDocument daglasco =
                ProviderDocument.builder().id(1L).name("daglas & co.").build();

            productService.addProduct(ProductDocument.builder().brand(daglas).build()); // +1
            productService.addProduct(ProductDocument.builder().brand(nivea).build()); // +1
            productService.addProduct(ProductDocument.builder().name("daglas").brand(hit).build());
            productService.addProduct(ProductDocument.builder().provider(daglasco).build());

            var criteria = ProductCriteria.builder()
                .brandsNamesToInclude(Set.of("daglas", "nivea")).build();

            // When
            Page<ProductDocument> page = productService.getProductsMatchingCriteria(
                criteria, PageRequest.of(0, 10));

            // Then
            assertThat(page.getContent().size()).isEqualTo(2);

            for (ProductDocument product : page.getContent()) {
                assertThat(product.getBrand().getName()).isIn("daglas", "nivea");
            }
        }

        @Test
        public void givenBrandsExcludeCriteria_whenFilter_thenReturnCorrectProducts() {
            // Given
            BrandDocument daglas = BrandDocument.builder().id(1L).name("daglas").build();
            BrandDocument nivea = BrandDocument.builder().id(2L).name("nivea").build();
            BrandDocument adidas = BrandDocument.builder().id(3L).name("adidas").build();
            ProviderDocument daglasco =
                ProviderDocument.builder().id(1L).name("daglas & co.").build();

            productService.addProduct(ProductDocument.builder()
                .brand(daglas).build()); // +1
            productService.addProduct(ProductDocument.builder()
                .brand(nivea).build()); // +1
            productService.addProduct(ProductDocument.builder()
                .name("perfume").brand(adidas).build());
            productService.addProduct(ProductDocument.builder()
                .provider(daglasco).brand(daglas).build());

            var criteria = ProductCriteria.builder()
                .brandsNamesToExclude(Set.of("adidas", "nivea")).build();

            // When
            Page<ProductDocument> page = productService.getProductsMatchingCriteria(
                criteria, PageRequest.of(0, 10));

            // Then
            assertThat(page.getContent().size()).isEqualTo(2);

            for (ProductDocument product : page.getContent()) {
                assertThat(product.getBrand().getName()).isNotIn("daglas & co.", "nivea");
            }
        }

        @Test
        public void givenIngredientsIncludeCriteria_whenFilter_thenReturnCorrectProducts() {
            // Given
            BrandDocument carfour = BrandDocument.builder().id(3L).name("carfur").build();

            IngredientDocument potato = IngredientDocument.builder().id(1L).name("potato").build();
            IngredientDocument carrot = IngredientDocument.builder().id(2L).name("carrot").build();
            IngredientDocument beet = IngredientDocument.builder().id(3L).name("beet").build();

            productService.addProduct(ProductDocument.builder().brand(carfour)
                .ingredients(Set.of(potato, carrot, beet)).build());
            productService.addProduct(ProductDocument.builder().brand(carfour)
                .ingredients(Set.of(beet)).build());
            productService.addProduct(ProductDocument.builder().brand(carfour)
                .ingredients(Set.of(potato, carrot)).build());
            productService.addProduct(ProductDocument.builder().brand(carfour)
                .ingredients(Set.of(carrot, beet)).build());
            productService.addProduct(ProductDocument.builder().brand(carfour)
                .ingredients(Set.of(potato, beet)).build());

            var criteria1 = ProductCriteria.builder()
                .ingredientsNamesToInclude(Set.of("potato", "beet")).build();
            var criteria2 = ProductCriteria.builder()
                .ingredientsNamesToInclude(Set.of("potato")).build();

            // When
            Page<ProductDocument> potatoBeetPage =
                productService.getProductsMatchingCriteria(criteria1,
                    PageRequest.of(0, 16));

            Page<ProductDocument> potatoPage = productService.getProductsMatchingCriteria(criteria2,
                PageRequest.of(0, 16));

            // Then
            assertThat(potatoBeetPage.getContent().size()).isEqualTo(2);
            for (ProductDocument product : potatoBeetPage.getContent()) {
                assertThat(product.getIngredients()
                    .stream()
                    .map(IngredientDocument::getName).toList()).contains("potato", "beet");
            }

            assertThat(potatoPage.getContent().size()).isEqualTo(3);
            for (ProductDocument product : potatoPage.getContent()) {
                assertThat(product.getIngredients()
                    .stream()
                    .map(IngredientDocument::getName).toList()).contains("potato");
            }
        }

        @Test
        @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
        public void givenIngredientsExcludeCriteria_whenFilter_thenReturnCorrectProducts() {
            // Given
            BrandDocument carfour = BrandDocument.builder().id(3L).name("carfur").build();

            IngredientDocument potato = IngredientDocument.builder().id(1L).name("potato").build();
            IngredientDocument carrot = IngredientDocument.builder().id(2L).name("carrot").build();
            IngredientDocument beet = IngredientDocument.builder().id(3L).name("beet").build();
            IngredientDocument tomato = IngredientDocument.builder().id(4L).name("tomato").build();

            productService.addProduct(ProductDocument.builder().brand(carfour)
                .ingredients(Set.of(potato, carrot, beet)).build());
            productService.addProduct(ProductDocument.builder().brand(carfour)
                .ingredients(Set.of(beet, tomato)).build());
            productService.addProduct(ProductDocument.builder().brand(carfour)
                .ingredients(Set.of(potato, carrot)).build());
            productService.addProduct(ProductDocument.builder().brand(carfour)
                .ingredients(Set.of(carrot, beet)).build());
            productService.addProduct(ProductDocument.builder().brand(carfour)
                .ingredients(Set.of(potato, beet)).build());

            var criteria1 = ProductCriteria.builder()
                .ingredientsNamesToExclude(Set.of("potato", "beet")).build();
            var criteria2 = ProductCriteria.builder()
                .ingredientsNamesToExclude(Set.of("potato")).build();
            var criteria3 = ProductCriteria.builder()
                .ingredientsNamesToExclude(Set.of("carrot", "tomato")).build();

            // When
            Page<ProductDocument> potatoBeetPage = productService
                .getProductsMatchingCriteria(criteria1, PageRequest.of(0, 16));

            Page<ProductDocument> potatoPage = productService.getProductsMatchingCriteria(
                criteria2, PageRequest.of(0, 16));

            Page<ProductDocument> carrotTomatoPage = productService
                .getProductsMatchingCriteria(criteria3, PageRequest.of(0, 16));

            // Then
            assertThat(potatoBeetPage.getContent().size()).isEqualTo(0);
            assertThat(potatoPage.getContent().size()).isEqualTo(2);
            assertThat(carrotTomatoPage.getContent().size()).isEqualTo(1);

            for (ProductDocument product : potatoPage.getContent()) {
                assertThat(product.getIngredients()
                    .stream()
                    .map(IngredientDocument::getName).toList()).doesNotContain("potato");
            }

            for (ProductDocument product : carrotTomatoPage.getContent()) {
                assertThat(product.getIngredients()
                    .stream()
                    .map(IngredientDocument::getName).toList())
                    .doesNotContain("carrot", "tomato");
            }
        }

        @Test
        public void givenRatingIngredientsCriteria_whenFilter_thenReturnCorrectProducts() {
            // Given
            BrandDocument carfour = BrandDocument.builder().id(3L).name("carfur").build();

            IngredientDocument potato = IngredientDocument.builder().id(1L).name("potato").build();
            IngredientDocument carrot = IngredientDocument.builder().id(2L).name("carrot").build();
            IngredientDocument beet = IngredientDocument.builder().id(3L).name("beet").build();
            IngredientDocument tomato = IngredientDocument.builder().id(4L).name("tomato").build();

            productService.addProduct(ProductDocument.builder().brand(carfour)
                .ingredients(Set.of(potato, carrot, beet)).build());
            productService.addProduct(ProductDocument.builder().brand(carfour)
                .ingredients(Set.of(beet, tomato)).build());
            productService.addProduct(ProductDocument.builder().brand(carfour)
                .ingredients(Set.of(potato, carrot)).build());
            productService.addProduct(ProductDocument.builder().brand(carfour)
                .ingredients(Set.of(carrot, beet)).build());
            productService.addProduct(ProductDocument.builder().brand(carfour)
                .ingredients(Set.of(potato)).rating(6).build());
            productService.addProduct(ProductDocument.builder().brand(carfour)
                .ingredients(Set.of(potato, beet)).rating(7).build());
            productService.addProduct(ProductDocument.builder().brand(carfour)
                .ingredients(Set.of(beet)).rating(10).build());
            productService.addProduct(ProductDocument.builder().brand(carfour)
                .ingredients(Set.of(potato, beet)).rating(10).build());

            var criteria = ProductCriteria.builder()
                .ingredientsNamesToInclude(Set.of("potato"))
                .ingredientsNamesToExclude(Set.of("carrot", "tomato"))
                .minRating(7)
                .build();

            // When
            Page<ProductDocument> result = productService.getProductsMatchingCriteria(criteria,
                PageRequest.of(0, 16));

            assertThat(result.getContent().size()).isEqualTo(2);

            for (ProductDocument product : result.getContent()) {
                assertThat(product.getIngredients()
                    .stream()
                    .map(IngredientDocument::getName).toList())
                    .doesNotContain("carrot", "tomato");
                assertThat(product.getIngredients()
                    .stream()
                    .map(IngredientDocument::getName).toList())
                    .contains("potato");
                assertThat(product.getRating()).isGreaterThanOrEqualTo(7);
            }
        }

        @Test
        public void givenMultiCriteria_whenFilter_thenReturnCorrectProducts() {
            // Given
            BrandDocument nivea = BrandDocument.builder().id(1L).name("nivea").build();
            BrandDocument grycan = BrandDocument.builder().id(2L).name("grycan").build();
            BrandDocument sniker = BrandDocument.builder().id(3L).name("sniker").build();
            BrandDocument ogrodek = BrandDocument.builder().id(4L).name("ogrodek").build();
            BrandDocument golibroda = BrandDocument.builder().id(5L).name("golibroda").build();
            BrandDocument kolgat = BrandDocument.builder().id(6L).name("kolgat").build();
            BrandDocument elmech = BrandDocument.builder().id(7L).name("elmech").build();
            BrandDocument akufresz = BrandDocument.builder().id(8L).name("elmech").build();

            IngredientDocument guma = IngredientDocument
                .builder().id(2L).name("guma").build();
            IngredientDocument rak = IngredientDocument
                .builder().id(3L).name("rak").build();
            IngredientDocument polietylen = IngredientDocument
                .builder().id(4L).name("polietylen").build();
            IngredientDocument gumaGuar = IngredientDocument
                .builder().id(5L).name("guma guar").build();
            IngredientDocument metanol = IngredientDocument
                .builder().id(6L).name("metanol").build();


            ProviderDocument zapka = ProviderDocument.builder().id(1L).name("żapka").build();
            ProviderDocument rosman = ProviderDocument.builder().id(2L).name("rosman").build();
            ProviderDocument karfur = ProviderDocument.builder().id(3L).name("karfur")
                .build();

            // Proper
            productService.addProduct(
                ProductDocument.builder()
                    .name("pasta do zębów")
                    .brand(nivea)
                    .provider(zapka)
                    .ingredients(Set.of(polietylen, gumaGuar, metanol))
                    .build());

            productService.addProduct(
                ProductDocument.builder()
                    .name("poper")
                    .brand(nivea)
                    .provider(zapka)
                    .ingredients(Set.of(rak, guma, metanol))
                    .build());

            // Red herrings
            productService.addProduct(
                ProductDocument.builder().name("ziemniak").brand(nivea).provider(zapka)
                    .build());
            productService.addProduct(
                ProductDocument.builder().name("obrazek").brand(nivea).provider(zapka)
                    .build());
            productService.addProduct(
                ProductDocument.builder().name("szampą").brand(nivea).provider(karfur)
                    .build());
            productService.addProduct(
                ProductDocument.builder().name("szamka").brand(grycan).provider(zapka)
                    .build());
            productService.addProduct(
                ProductDocument.builder().name("baton").brand(sniker).provider(zapka).build());
            productService.addProduct(
                ProductDocument.builder().name("marchew").brand(ogrodek).provider(karfur)
                    .build());
            productService.addProduct(
                ProductDocument.builder().name("pianka do golenia").brand(golibroda)
                    .provider(rosman)
                    .build());
            productService.addProduct(
                ProductDocument.builder().name("pasta do zębów").brand(kolgat)
                    .provider(rosman)
                    .build());
            productService.addProduct(
                ProductDocument.builder().name("pasta do zębów").brand(elmech)
                    .provider(rosman)
                    .build());
            productService.addProduct(
                ProductDocument.builder().name("pasta do zębów").brand(akufresz)
                    .provider(rosman)
                    .build());

            var criteria = ProductCriteria.builder()
                .brandsNamesToInclude(Set.of("nivea"))
                .providersNames(Set.of("żapka"))
                .ingredientsNamesToInclude(Set.of("metanol"))
                .build();

            // When
            Page<ProductDocument> kerfurZabkaPage = productService.getProductsMatchingCriteria(
                criteria, PageRequest.of(0, 30));

            List<ProductDocument> karfurZapkaProducts = kerfurZabkaPage.getContent();

            // Then
            assertThat(karfurZapkaProducts.size()).isEqualTo(2);

            for (ProductDocument product : karfurZapkaProducts) {
                assertThat(product.getProvider().getName()).isEqualTo("żapka");
                assertThat(product.getBrand().getName()).isEqualTo("nivea");
                assertThat(product.getIngredients()
                    .stream()
                    .map(IngredientDocument::getName).toList())
                    .contains("metanol");
            }
        }
    }

    @Nested
    @Transactional
    class PhraseAndSortingTests {
        @Test
        public void givenProducts_whenMatchSort_thenProductWithGreaterMatchScoreIsFirst() {
            // Given
            productService.addProduct(ProductDocument.builder().name("almette")
                .shortDescription("krem kremik kremiasty").build());
            productService.addProduct(ProductDocument.builder().name("parmezan")
                .shortDescription("krem kremik").build());
            productService.addProduct(ProductDocument.builder().name("serek")
                .shortDescription("krem do stóp").build());

            var sortingCriteria = productCriteriaService
                .getProductsSortingCriteria("d-match-score");

            var criteria = ProductCriteria.builder()
                .phraseKeywords(Set.of("krem"))
                .hasMatchScoreSortCriteria(true)
                .sortingCriteria(List.of(sortingCriteria))
                .build();

            // When
            Page<ProductDocument> result = productService.getProductsMatchingCriteria(
                criteria, PageRequest.of(0, 30));

            // Then
            assertThat(result.getContent().get(0).getName()).isEqualTo("almette");
            assertThat(result.getContent().get(1).getName()).isEqualTo("parmezan");
            assertThat(result.getContent().get(2).getName()).isEqualTo("serek");
        }

        @Test
        public void givenMultiCaseFields_whenSearch_thenResultIsCaseInsensitive() {
            // Given
            productService.addProduct(ProductDocument.builder().name("aLmeTTe")
                .shortDescription("MaśĆ").build());
            productService.addProduct(ProductDocument.builder().name("ParMez")
                .shortDescription("SZamPoNik").build());
            productService.addProduct(ProductDocument.builder().name("SErEk")
                .shortDescription("kRem do stóp").build());

            var criteria = ProductCriteria.builder()
                .phraseKeywords(Set.of("krem", "serek"))
                .build();

            // When
            Page<ProductDocument> result = productService.getProductsMatchingCriteria(
                criteria, PageRequest.of(0, 30));

            // Then
            assertThat(result.getContent().size()).isEqualTo(1);
            assertThat(result.getContent().getFirst().getName()).isEqualTo("SErEk");
        }
    }

    @Nested
    @Transactional
    class ProductsLikingTests {
        @Test
        public void givenProduct_whenLikeProduct_thenSuccess() {
            // Given
            ProductDocument product = ProductDocument.builder().name("likedProduct").build();
            ProductDocument savedProduct = productService.addProduct(product);
            Long id = savedProduct.getId();

            // When
            User user = ProductServiceTest.this.user;

            productService.likeProduct(id, user);
            ProductDocument updatedProduct = productService.getProductById(id);

            // Then
            assertTrue(productService.isProductLikedByUser(updatedProduct, user));
        }

        @Test
        public void givenProduct_whenLikeNonExistingProduct_thenFailure() {
            // Given
            User user = ProductServiceTest.this.user;

            // When

            // Then
            assertThrows(
                ProductNotFoundException.class,
                () -> productService.likeProduct(1000L, user));
        }

        @Test
        public void givenProduct_whenUnLikeProduct_thenSuccess() {
            // Given
            ProductDocument product = ProductDocument.builder().name("likedProduct").build();
            ProductDocument savedProduct = productService.addProduct(product);
            Long id = savedProduct.getId();
            User user = ProductServiceTest.this.user;

            // When
            productService.likeProduct(id, user);
            productService.unlikeProduct(id, user);
            ProductDocument updatedProduct = productService.getProductById(id);

            // Then
            assertFalse(productService.isProductLikedByUser(updatedProduct, user));
        }

        @Test
        public void givenProduct_whenUnLikeNonExistingProduct_thenFailure() {
            // Given
            User user = ProductServiceTest.this.user;

            // When

            // Then
            assertThrows(
                ProductNotFoundException.class,
                () -> productService.unlikeProduct(1000L, user));
        }

        @Test
        public void givenProduct_whenLikeAndGetProductsList_thenProductsAreLiked() {
            // Given
            ProductDocument product1 = productService
                .addProduct(ProductDocument.builder().name("likedProduct1").build());
            ProductDocument product2 = productService
                .addProduct(ProductDocument.builder().name("likedProduct2").build());
            User user = ProductServiceTest.this.user;

            productService.addProduct(ProductDocument.builder().name("likedProduct3").build());

            // When
            productService.likeProduct(product1.getId(), user);
            productService.likeProduct(product2.getId(), user);

            Page<ProductDocument> page = productService.getProductsMatchingCriteria(
                ProductCriteria.builder().build(), PageRequest.of(0, 30));
            List<ProductDocument> products = page.getContent();

            // Then
            assertThat(productService.isProductLikedByUser(products.getFirst(), user)).isTrue();
            assertThat(productService.isProductLikedByUser(products.get(1), user)).isTrue();
            assertThat(productService.isProductLikedByUser(products.get(2), user)).isFalse();
        }

        @Test
        public void givenProduct_whenLikeAndGetProductDetails_thenProductIsLiked() {
            // Given
            ProductDocument product = productService
                .addProduct(ProductDocument.builder().name("likedProduct").build());
            User user = ProductServiceTest.this.user;

            // When
            productService.likeProduct(product.getId(), user);
            ProductDocument p = productService.getProductById(product.getId());

            // Then
            assertThat(productService.isProductLikedByUser(p, user)).isTrue();
        }
    }

    @Nested
    @Transactional
    class EditAndDeleteTest {
        @Test
        public void givenProductId_whenUpdateEntireProduct_thenProductIsUpdated() {
            Set<IngredientDocument> oldIngredients = Set.of(
                IngredientDocument.builder().name("oldIngredient1").build(),
                IngredientDocument.builder().name("oldIngredient2").build()
            );
            ProviderDocument oldProvider = ProviderDocument.builder().name("oldProvider").build();
            BrandDocument oldBrand = BrandDocument.builder().name("oldBrand").build();
            Set<CategoryDocument> oldCategories = Set.of(
                CategoryDocument.builder().name("oldCategory1").build(),
                CategoryDocument.builder().name("oldCategory2").build()
            );

            ProductDocument productToEdit = productService.addProduct(ProductDocument.builder()
                .name("productToEdit")
                .smallImageUrl("oldSmallImageUrl")
                .largeImageUrl("oldLargeImageUrl")
                .provider(oldProvider)
                .brand(oldBrand)
                .shortDescription("oldShortDescription")
                .longDescription("oldLongDescription")
                .volume("oldVolume")
                .categories(oldCategories)
                .ingredients(oldIngredients)
                .build());

            Set<IngredientDocument> newIngredients = Set.of(
                IngredientDocument.builder().name("newIngredient1").build(),
                IngredientDocument.builder().name("newIngredient2").build()
            );
            ProviderDocument newProvider = ProviderDocument.builder().name("newProvider").build();
            BrandDocument newBrand = BrandDocument.builder().name("newBrand").build();
            Set<CategoryDocument> newCategories = Set.of(
                CategoryDocument.builder().name("newCategory1").build(),
                CategoryDocument.builder().name("newCategory2").build()
            );

            ProductDocument productEdited = ProductDocument.builder()
                .id(productToEdit.getId())
                .name("productEdited")
                .smallImageUrl("newSmallImageUrl")
                .largeImageUrl("newLargeImageUrl")
                .provider(newProvider)
                .brand(newBrand)
                .shortDescription("newShortDescription")
                .longDescription("newLongDescription")
                .volume("newVolume")
                .ingredients(newIngredients)
                .categories(newCategories)
                .build();

            // When
            ProductDocument editedProduct = productService.updateProduct(productEdited);

            // Then
            assertThat(editedProduct.getId()).isEqualTo(productToEdit.getId());
            assertThat(editedProduct.getName()).isEqualTo("productEdited");
            assertThat(editedProduct.getSmallImageUrl())
                .isEqualTo("newSmallImageUrl");
            assertThat(editedProduct.getLargeImageUrl())
                .isEqualTo("newLargeImageUrl");
            assertThat(editedProduct.getProvider()).isEqualTo(newProvider);
            assertThat(editedProduct.getBrand()).isEqualTo(newBrand);
            assertThat(editedProduct.getShortDescription())
                .isEqualTo("newShortDescription");
            assertThat(editedProduct.getLongDescription())
                .isEqualTo("newLongDescription");
            assertThat(editedProduct.getVolume()).isEqualTo("newVolume");
            assertThat(editedProduct.getIngredients()).isEqualTo(newIngredients);
            assertThat(editedProduct.getCategories()).isEqualTo(newCategories);
        }

        @Test
        public void givenProductId_whenEditNonExistingProduct_thenReturnFalse() {
            // Given
            productService.addProduct(ProductDocument.builder().name("edited").build());

            ProductDocument productPatch = ProductDocument.builder()
                .id(1000L)
                .name("newName")
                .build();

            // When

            // Then
            assertThrows(
                ProductNotFoundException.class,
                () -> productService.updateProduct(productPatch));
        }

        @Test
        public void givenProductId_whenDeleteProduct_thenProductIsDeleted() {
            // Given
            ProductDocument product = productService
                .addProduct(ProductDocument.builder().name("productToDelete").build());

            // When
            productService.deleteProductById(product.getId());

            // Then
            assertThrows(
                ProductNotFoundException.class,
                () -> productService.getProductById(product.getId()));
        }

        @Test
        public void givenProductId_whenDeleteNonExistingProduct_thenReturnFalse() {
            // Given

            // When

            // Then
            assertThrows(
                ProductNotFoundException.class,
                () -> productService.getProductById(1000L));
        }
    }

    @Nested
    class ReviewTest {
        @Test
        public void givenProductId_whenAddReview_reviewIsAdded() {
            // Given
            ProductDocument product = productService
                .addProduct(ProductDocument.builder().name("testProduct").build());
            Review review = Review.builder()
                .productId(product.getId())
                .content("testContent")
                .rating(5)
                .build();

            // When
            Optional<ReviewDto> reviewOptional = productService.addReview(review);
            ProductDocument reviewedProduct = productService
                .getProductById(product.getId());

            // Then
            assertThat(reviewOptional.isPresent()).isTrue();
            assertThat(reviewOptional.get().displayName()).isEqualTo("User");
            assertThat(reviewOptional.get().content()).isEqualTo("testContent");
            assertThat(reviewOptional.get().rating()).isEqualTo(5);

            assertThat(reviewedProduct.getRating()).isEqualTo(5);
        }

        @Test
        public void givenNonExistingProductId_whenAddReview_reviewIsNotAdded() {
            // Given
            Review review = Review.builder()
                .productId(1000L)
                .content("testContent")
                .rating(5)
                .build();

            // When

            // Then
            assertThrows(
                ProductNotFoundException.class,
                () -> productService.addReview(review)
            );
        }

        @Test
        public void givenProductId_whenGetReviews_thenGetProductReviews() {
            // Given
            ProductDocument product = productService
                .addProduct(ProductDocument.builder().name("testProduct").build());
            Review review = Review.builder()
                .productId(product.getId())
                .content("testContent")
                .rating(1)
                .build();

            // When
            productService.addReview(review);
            Optional<List<ReviewDto>> reviews = productService
                .getProductReviews(product.getId());

            // Then
            assertThat(reviews.isPresent()).isTrue();
            assertThat(reviews.get().size()).isEqualTo(1);
            assertThat(reviews.get().getFirst().rating()).isEqualTo(1);
            assertThat(reviews.get().getFirst().content()).isEqualTo("testContent");
        }

        @Test
        public void givenNonExistingProductId_whenGetReviews_thenGetEmptyResponse() {
            // Given

            // When

            // Then
            assertThrows(
                ProductNotFoundException.class,
                () -> productService.getProductReviews(1000L)
            );
        }

        @Test
        public void givenProductId_whenEditReview_reviewIsEdited() {
            ProductDocument product = productService
                .addProduct(ProductDocument.builder().name("testProduct").build());
            Review review = Review.builder()
                .productId(product.getId())
                .content("testContent")
                .rating(1)
                .build();

            Review editedReview = Review.builder()
                .productId(product.getId())
                .content("edited")
                .rating(5)
                .build();

            // When
            productService.addReview(review);
            Optional<ReviewDto> reviewOptional = productService.editReview(editedReview);
            Optional<List<ReviewDto>> reviews = productService
                .getProductReviews(product.getId());
            ProductDocument reviewedProduct = productService
                .getProductById(product.getId());

            // Then
            assertThat(reviewOptional.isPresent()).isTrue();
            assertThat(reviewOptional.get().displayName()).isEqualTo("User");
            assertThat(reviewOptional.get().content()).isEqualTo("edited");
            assertThat(reviewOptional.get().rating()).isEqualTo(5);
            assertThat(reviews.isPresent()).isTrue();
            assertThat(reviews.get().size()).isEqualTo(1);
            assertThat(reviews.get().getFirst().rating()).isEqualTo(5);
            assertThat(reviews.get().getFirst().content()).isEqualTo("edited");

            assertThat(reviewedProduct.getRating()).isEqualTo(5);
        }

        @Test
        public void givenNonExistingProductId_whenEditReview_reviewIsNotEdited() {
            ProductDocument product = productService
                .addProduct(ProductDocument.builder().name("testProduct").build());
            Review editedReview = Review.builder()
                .productId(product.getId())
                .content("edited")
                .rating(5)
                .build();

            // When
            Optional<ReviewDto> reviewOptional = productService.editReview(editedReview);

            // Then
            assertThat(reviewOptional.isEmpty()).isTrue();
        }

        @Test
        public void givenProductId_whenEditNonExistingReview_reviewIsNotEdited() {
            ProductDocument product = productService
                .addProduct(ProductDocument.builder().name("testProduct").build());
            Review editedReview = Review.builder()
                .productId(product.getId())
                .content("edited")
                .rating(5)
                .build();

            // When
            Optional<ReviewDto> reviewOptional = productService.editReview(editedReview);

            // Then
            assertThat(reviewOptional.isEmpty()).isTrue();
        }

        @Test
        public void givenProductId_whenDeleteReview_reviewIsDeleted() {
            ProductDocument product = productService
                .addProduct(ProductDocument.builder().name("testProduct").build());
            Review review = Review.builder()
                .productId(product.getId())
                .content("review")
                .rating(5)
                .build();

            // When
            productService.addReview(review);
            Optional<List<ReviewDto>> reviews = productService
                .getProductReviews(product.getId());

            productService.deleteReview(product.getId());
            Optional<List<ReviewDto>> reviewsDeleted = productService
                .getProductReviews(product.getId());

            // Then
            assertThat(reviews.isPresent()).isTrue();
            assertThat(reviews.get().size()).isEqualTo(1);
            assertThat(reviewsDeleted.isPresent()).isTrue();
            assertThat(reviewsDeleted.get().size()).isEqualTo(0);
        }

        @Test
        public void givenProductId_whenGetProductUserReview_getProductUserReview() {
            ProductDocument product = productService
                .addProduct(ProductDocument.builder().name("testProduct").build());
            Review review = Review.builder()
                .productId(product.getId())
                .content("review")
                .rating(5)
                .build();

            // When
            productService.addReview(review);
            Optional<ReviewDto> productUserReview = productService
                .getProductUserReview(product.getId());


            // Then
            assertThat(productUserReview.isPresent()).isTrue();
            assertThat(productUserReview.get().productId()).isEqualTo(product.getId());
            assertThat(productUserReview.get().displayName()).isEqualTo("User");
            assertThat(productUserReview.get().rating()).isEqualTo(5);
            assertThat(productUserReview.get().content()).isEqualTo("review");
        }

        @Test
        public void givenProductId_whenGetNonExistingProductUserReview_getEmpty() {
            ProductDocument product = productService
                .addProduct(ProductDocument.builder().name("testProduct").build());

            // When
            Optional<ReviewDto> productUserReview = productService
                .getProductUserReview(product.getId());

            // Then
            assertThat(productUserReview.isPresent()).isFalse();
        }
    }
}
