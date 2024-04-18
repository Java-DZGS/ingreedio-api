package pl.edu.pw.mini.ingreedio.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.edu.pw.mini.ingreedio.api.IntegrationTest;
import pl.edu.pw.mini.ingreedio.api.criteria.ProductFilterCriteria;
import pl.edu.pw.mini.ingreedio.api.dto.FullProductDto;
import pl.edu.pw.mini.ingreedio.api.dto.ProductDto;
import pl.edu.pw.mini.ingreedio.api.model.Product;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductServiceTest extends IntegrationTest {
    @Autowired
    private ProductService productService;

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
        List<ProductDto> res =
            productService.getProductsMatching(ProductFilterCriteria.builder().build());

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
        List<ProductDto> daglasProducts = productService.getProductsMatching(
            ProductFilterCriteria.builder().provider("daglas").build());

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
        List<ProductDto> daglasProducts = productService.getProductsMatching(
            ProductFilterCriteria.builder().brand("daglas").build());

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
        List<ProductDto> ziemniakBurakProducts = productService.getProductsMatching(
            ProductFilterCriteria.builder()
                .ingredients(new String[] {"ziemniak", "burak"})
                .build());
        List<ProductDto> ziemniakProducts = productService.getProductsMatching(
            ProductFilterCriteria.builder()
                .ingredients(new String[] {"ziemniak"})
                .build());

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
    @Order(8)
    public void givenVolumeCriteria_whenFilter_thenReturnCorrectProducts() {
        // Given
        productService.addProduct(Product.builder().name("woda").brand("cisowiana")
            .volume(3).build());
        productService.addProduct(Product.builder().name("woda").brand("contigo")
            .volume(4).build());
        productService.addProduct(Product.builder().name("woda").brand("skarb życia muszyna")
            .volume(5).build());
        productService.addProduct(Product.builder().name("woda").brand("nałęczowiana")
            .volume(6).build());
        productService.addProduct(Product.builder().name("woda").brand("pepsi")
            .volume(7).build());

        // When
        List<ProductDto> lessProducts = productService.getProductsMatching(
            ProductFilterCriteria.builder().volumeTo(6).build()
        );

        // Then
        assertThat(lessProducts.size()).isEqualTo(4);
        for (ProductDto productDto : lessProducts) {
            Optional<FullProductDto> product = productService.getProductById(productDto.id());

            assertThat(product).isPresent();
            assertThat(product.get().volume()).isLessThanOrEqualTo(6);
        }
    }

    @Test
    @Order(9)
    public void givenVolumeFromCriteria_whenFilter_thenReturnCorrectProducts() {
        // Given

        // When
        List<ProductDto> greaterProducts = productService.getProductsMatching(
            ProductFilterCriteria.builder().volumeFrom(4).build()
        );

        // Then
        assertThat(greaterProducts.size()).isEqualTo(4);
        for (ProductDto productDto : greaterProducts) {
            Optional<FullProductDto> product = productService.getProductById(productDto.id());

            assertThat(product).isPresent();
            assertThat(product.get().volume()).isGreaterThanOrEqualTo(4);
        }
    }

    @Test
    @Order(10)
    public void givenVolumeBetweenCriteria_whenFilter_thenReturnCorrectProducts() {
        // Given

        // When
        List<ProductDto> betweenProducts = productService.getProductsMatching(
            ProductFilterCriteria.builder().volumeTo(6).volumeFrom(3).build()
        );

        // Then
        assertThat(betweenProducts.size()).isEqualTo(4);
        for (ProductDto productDto : betweenProducts) {
            Optional<FullProductDto> product = productService.getProductById(productDto.id());

            assertThat(product).isPresent();
            assertThat(product.get().volume()).isLessThanOrEqualTo(6);
            assertThat(product.get().volume()).isGreaterThanOrEqualTo(3);
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
                .volume(12)
                .ingredients(Arrays.asList("polietylen", "guma guar", "metanol"))
                .build());
        productService.addProduct(
            Product.builder()
                .name("poper")
                .brand("karfur")
                .provider("żapka")
                .volume(14)
                .ingredients(Arrays.asList("rak", "guma", "metanol"))
                .build());

        // Red herrings
        productService.addProduct(
            Product.builder().name("ziemniak").brand("karfur").provider("żapka").volume(13)
                .build());
        productService.addProduct(
            Product.builder().name("obrazek").brand("karfur").provider("żapka").volume(15)
                .build());
        productService.addProduct(
            Product.builder().name("szampą").brand("żapka").provider("karfur").volume(200)
                .build());
        productService.addProduct(
            Product.builder().name("szamka").brand("grycan").provider("żapka").volume(13).build());
        productService.addProduct(
            Product.builder().name("baton").brand("sniker").provider("żapka").volume(12).build());
        productService.addProduct(
            Product.builder().name("marchew").brand("ogródek").provider("rosman").volume(1)
                .build());
        productService.addProduct(
            Product.builder().name("pianka do golenia").brand("golibroda").provider("romsan")
                .volume(12).build());
        productService.addProduct(
            Product.builder().name("pasta do zębów").brand("kolgat").provider("romsan").volume(12)
                .build());
        productService.addProduct(
            Product.builder().name("pasta do zębów").brand("sęsodę").provider("romsan").volume(12)
                .build());
        productService.addProduct(
            Product.builder().name("pasta do zębów").brand("elmech").provider("romsan").volume(12)
                .build());
        productService.addProduct(
            Product.builder().name("pasta do zębów").brand("akuafresz").provider("romsan")
                .volume(12).build());
        productService.addProduct(
            Product.builder().name("pasta do butów").brand("kiwi").provider("romsan").volume(12)
                .build());

        // When
        List<ProductDto> karfurZapkaProducts = productService.getProductsMatching(
            ProductFilterCriteria.builder()
                .brand("karfur")
                .volumeFrom(12)
                .volumeTo(14)
                .provider("żapka")
                .ingredients(new String[] {"metanol"})
                .build());

        // Then
        assertThat(karfurZapkaProducts.size()).isEqualTo(2);

        for (ProductDto productDto : karfurZapkaProducts) {
            Optional<FullProductDto> product = productService.getProductById(productDto.id());

            assertThat(product).isPresent();
            assertThat(product.get().provider()).isEqualTo("żapka");
            assertThat(product.get().brand()).isEqualTo("karfur");
            assertThat(product.get().volume()).isBetween(12, 14);
            assertThat(product.get().ingredients()).contains("metanol");
        }
    }

    @Test
    @Order(12)
    public void givenNameCriteria_whenFilter_thenReturnCorrectProducts() {
        // Given
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
        List<ProductDto> liquidProducts = productService.getProductsMatching(
            ProductFilterCriteria.builder().name(searchTerm).build());

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
}
