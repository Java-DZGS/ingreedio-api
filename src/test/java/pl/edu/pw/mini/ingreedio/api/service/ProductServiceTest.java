package pl.edu.pw.mini.ingreedio.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import pl.edu.pw.mini.ingreedio.api.IntegrationTest;
import pl.edu.pw.mini.ingreedio.api.criteria.ProductFilterCriteria;
import pl.edu.pw.mini.ingreedio.api.dto.FullProductDto;
import pl.edu.pw.mini.ingreedio.api.dto.ProductDto;
import pl.edu.pw.mini.ingreedio.api.mapper.ProductDtoMapper;
import pl.edu.pw.mini.ingreedio.api.model.Product;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductServiceTest extends IntegrationTest {

    @Autowired
    private ProductService productService;

    private PageRequest pageRequest = PageRequest.of(0, 30);

    @Test
    @Order(1)
    public void givenProductObject_whenSaveProduct_thenReturnProductObject() {
        // Given
        Product product = Product.builder().name("testProduct").build();

        // When
        Product savedProduct = productService.addProduct(product);

        // Then
        assertThat(savedProduct).isNotNull();
    }

    @Test
    @Order(2)
    public void givenProductsList_whenGetAllProducts_thenReturnProductsList() {
        // Given
        productService.addProduct(Product.builder().name("testProduct1").build());
        productService.addProduct(Product.builder().name("testProduct2").build());
        productService.addProduct(Product.builder().name("testProduct3").build());

        // When
        List<ProductDto> productsList = productService.getAllProducts();

        // Then
        assertThat(productsList).isNotNull();
        assertThat(productsList.size()).isEqualTo(4);
    }

    @Test
    @Order(3)
    public void givenProductId_whenGetProductById_thenReturnFullProductDtoObject() {
        // When
        FullProductDto fullProductDto = productService.getProductById(1L).orElseThrow();

        // Then
        assertThat(fullProductDto).isNotNull();
        assertThat(fullProductDto.id()).isEqualTo(1L);
        assertThat(fullProductDto.name()).isEqualTo("testProduct");
    }

    @Test
    @Order(4)
    public void givenNoCriteria_whenFilter_thenReturnAllProducts() {
        // Given

        // When
        Page<ProductDto> page = productService.getProductsMatching(
            ProductFilterCriteria.builder().build(), pageRequest);
        List<ProductDto> res = page.getContent();

        // Then
        assertThat(res).isNotNull();
        assertThat(res.size()).isEqualTo(4);
    }

    @Test
    @Order(5)
    public void givenProviderCriteria_whenFilter_thenReturnCorrectProducts() {
        // Given
        productService.addProduct(Product.builder().provider("daglas").build());
        productService.addProduct(Product.builder().provider("daglas").build());
        productService.addProduct(Product.builder().name("daglas").build());
        productService.addProduct(Product.builder().provider("daglas & co.").build());

        // When
        Page<ProductDto> page = productService.getProductsMatching(
            ProductFilterCriteria.builder().provider("daglas").build(), pageRequest);
        List<ProductDto> daglasProducts = page.getContent();

        // Then
        assertThat(daglasProducts.size()).isEqualTo(2);

        for (ProductDto productDto : daglasProducts) {
            assertThat(productDto.provider()).isEqualTo("daglas");
        }
    }

    @Test
    @Order(6)
    public void givenBrandCriteria_whenFilter_thenReturnCorrectProducts() {
        // Given
        productService.addProduct(Product.builder().brand("daglas").build());
        productService.addProduct(Product.builder().brand("daglas").build());
        productService.addProduct(Product.builder().name("daglas").build());
        productService.addProduct(Product.builder().provider("daglas & co.").build());

        // When
        Page<ProductDto> page = productService.getProductsMatching(
            ProductFilterCriteria.builder().brand("daglas").build(), pageRequest);
        List<ProductDto> daglasProducts = page.getContent();

        // Then
        assertThat(daglasProducts.size()).isEqualTo(2);

        for (ProductDto productDto : daglasProducts) {
            Optional<FullProductDto> product = productService.getProductById(productDto.id());

            assertThat(product).isPresent();
            assertThat(product.get().brand()).isEqualTo("daglas");
        }
    }

    @Test
    @Order(7)
    public void givenIngredientsCriteria_whenFilter_thenReturnCorrectProducts() {
        // Given
        productService.addProduct(Product.builder().brand("karfur")
            .ingredients(Arrays.asList("ziemniak", "marchewka", "burak")).build());
        productService.addProduct(Product.builder().brand("karfur")
            .ingredients(List.of("burak")).build());
        productService.addProduct(Product.builder().brand("karfur")
            .ingredients(Arrays.asList("ziemniak", "marchewka")).build());
        productService.addProduct(Product.builder().brand("karfur")
            .ingredients(Arrays.asList("marchewka", "burak")).build());
        productService.addProduct(Product.builder().brand("karfur")
            .ingredients(Arrays.asList("ziemniak", "burak")).build());

        // When
        Page<ProductDto> ziemniakBurakPage = productService.getProductsMatching(
            ProductFilterCriteria.builder()
                .ingredients(new String[] {"ziemniak", "burak"})
                .build(), pageRequest);
        List<ProductDto> ziemniakBurakProducts = ziemniakBurakPage.getContent();

        Page<ProductDto> ziemniakPage = productService.getProductsMatching(
            ProductFilterCriteria.builder()
                .ingredients(new String[] {"ziemniak"})
                .build(), pageRequest);
        List<ProductDto> ziemniakProducts = ziemniakPage.getContent();

        // Then
        assertThat(ziemniakBurakProducts.size()).isEqualTo(2);
        for (ProductDto productDto : ziemniakBurakProducts) {
            Optional<FullProductDto> product = productService.getProductById(productDto.id());

            assertThat(product).isPresent();
            assertThat(product.get().ingredients()).contains("ziemniak");
            assertThat(product.get().ingredients()).contains("burak");
        }

        assertThat(ziemniakProducts.size()).isEqualTo(3);
        for (ProductDto productDto : ziemniakProducts) {
            Optional<FullProductDto> product = productService.getProductById(productDto.id());

            assertThat(product).isPresent();
            assertThat(product.get().ingredients()).contains("ziemniak");
        }
    }

    @Test
    @Order(11)
    public void givenMultiCriteria_whenFilter_thenReturnCorrectProducts() {
        // Given

        // Proper
        productService.addProduct(
            Product.builder()
                .name("pasta do zębów")
                .brand("karfur")
                .provider("żapka")
                .volume("12 ml")
                .ingredients(Arrays.asList("polietylen", "guma guar", "metanol"))
                .build());
        productService.addProduct(
            Product.builder()
                .name("poper")
                .brand("karfur")
                .provider("żapka")
                .volume("14 ml")
                .ingredients(Arrays.asList("rak", "guma", "metanol"))
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

        // When
        Page<ProductDto> kerfurZabkaPage = productService.getProductsMatching(
            ProductFilterCriteria.builder()
                .brand("karfur")
                .provider("żapka")
                .ingredients(new String[] {"metanol"})
                .build(), pageRequest);
        List<ProductDto> karfurZapkaProducts = kerfurZabkaPage.getContent();

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

    @Test
    @Order(12)
    public void givenNameCriteria_whenFilter_thenReturnCorrectProducts() {
        // Given
        ProductDtoMapper productDtoMapper = new ProductDtoMapper();
        PageRequest pageRequest = PageRequest.of(0, 10);
        productService.addProduct(Product.builder()
            .name("Matte Blush")
            .brand("Maybelline")
            .shortDescription("Matte blush for a natural finish.")
            .longDescription("Controls shine and blurs pores for a natural, matte finish.")
            .build());
        productService.addProduct(Product.builder()
            .name("Pressed Powder")
            .brand("Rimmel London")
            .shortDescription("Pressed powder for smooth skin.")
            .longDescription(
                "Helps minimize the appearance of pores and leaves a smooth, matte finish.")
            .build());
        productService.addProduct(Product.builder()
            .name("Amazonian Clay 12-Hour Blush")
            .brand("Tarte")
            .shortDescription("Long-lasting blush with Amazonian clay.")
            .longDescription("Infused with Amazonian clay for 12 hours of fade-free wear.")
            .build());
        productService.addProduct(Product.builder()
            .name("Stay Matte Pressed Powder")
            .brand("Rimmel London")
            .shortDescription("Matte pressed powder for a smooth finish.")
            .longDescription(
                "Helps minimize the appearance of pores and leaves a smooth, matte finish.")
            .build());
        productService.addProduct(Product.builder()
            .name("Stay Matte Powder")
            .brand("Lovely")
            .shortDescription("Matte powder for smooth skin.")
            .longDescription(
                "Helps minimize the appearance of pores and leaves a smooth, matte finish.")
            .build());
        String searchTerm = "Matte Powder Rimmel";

        // When
        Page<ProductDto> page = productService.getProductsMatching(
            ProductFilterCriteria.builder().name(searchTerm).build(), pageRequest);
        List<ProductDto> liquidProducts = page.getContent();

        // Then
        assertThat(liquidProducts.size()).isEqualTo(2);

        for (ProductDto productDto : liquidProducts) {
            Optional<FullProductDto> product = productService.getProductById(productDto.id());

            assertThat(product).isPresent();

            String brand = product.get().brand().toLowerCase();
            String name = product.get().name().toLowerCase();
            String longDescription = product.get().longDescription().toLowerCase();
            String shortDescription = productDto.shortDescription().toLowerCase();

            String[] searchTermsArray = searchTerm.split("\\s+");
            for (String term : searchTermsArray) {
                assertThat(brand.contains(term.toLowerCase())
                    || name.contains(term.toLowerCase())
                    || longDescription.contains(term.toLowerCase())
                    || shortDescription.contains(term.toLowerCase())).isTrue();
            }
        }
    }

    @Test
    @WithMockUser(username = "user", password = "user", roles = {})
    @Order(13)
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
        assertTrue(updatedProduct.get().isLiked());
    }

    @Test
    @WithMockUser(username = "user", password = "user", roles = {})
    @Order(14)
    public void givenProduct_whenLikeNonExistingProduct_thenFailure() {
        // Given

        // When
        boolean result = productService.likeProduct(1000L);

        // Then
        assertFalse(result);
    }

    @Test
    @WithMockUser(username = "user", password = "user", roles = {})
    @Order(15)
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
        assertFalse(updatedProduct.get().isLiked());
    }

    @Test
    @WithMockUser(username = "user", password = "user", roles = {})
    @Order(16)
    public void givenProduct_whenUnLikeNonExistingProduct_thenFailure() {
        // Given

        // When
        boolean result = productService.unlikeProduct(1000L);

        // Then
        assertFalse(result);
    }

    @Test
    @WithMockUser(username = "user", password = "user", roles = {})
    @Order(17)
    public void givenProduct_whenLikeAndGetProductsList_thenProductsAreLiked() {
        // Given

        // When
        productService.likeProduct(1L);
        productService.likeProduct(2L);

        Page<ProductDto> page = productService.getProductsMatching(
            ProductFilterCriteria.builder().build(), pageRequest);
        List<ProductDto> products = page.getContent();

        // Then
        assertThat(products.getFirst().isLiked()).isTrue();
        assertThat(products.get(1).isLiked()).isTrue();
        assertThat(products.get(2).isLiked()).isFalse();
    }

    @Test
    @WithMockUser(username = "user", password = "user", roles = {})
    @Order(17)
    public void givenProduct_whenLikeAndGetProductDetails_thenProductIsLiked() {
        // Given

        // When
        productService.likeProduct(4L);
        Optional<FullProductDto> product = productService.getProductById(4L);

        // Then
        assertThat(product.get().isLiked()).isTrue();
    }
}
