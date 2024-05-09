package pl.edu.pw.mini.ingreedio.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import pl.edu.pw.mini.ingreedio.api.IntegrationTest;
import pl.edu.pw.mini.ingreedio.api.criteria.ProductsCriteria;
import pl.edu.pw.mini.ingreedio.api.dto.FullProductDto;
import pl.edu.pw.mini.ingreedio.api.dto.ProductDto;
import pl.edu.pw.mini.ingreedio.api.dto.ProductListResponseDto;
import pl.edu.pw.mini.ingreedio.api.model.Product;
import pl.edu.pw.mini.ingreedio.api.repository.ProductRepository;

@SpringBootTest
public class ProductServiceTest extends IntegrationTest {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;
    @AfterEach
    void clearProducts() {
        productRepository.deleteAll();
    }

    @Nested
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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
    class ProductsMatchingCriteriaTests {
        @Test
        public void givenNoCriteria_whenMatch_thenReturnAllProducts() {
            // Given
            productService.addProduct(Product.builder().name("testProduct1").build());
            productService.addProduct(Product.builder().name("testProduct2").build());
            productService.addProduct(Product.builder().name("testProduct3").build());

            var criteria = ProductsCriteria.builder().build();

            // When
            ProductListResponseDto page = productService.getProductsMatchingCriteria(
                criteria, PageRequest.of(0, 10));

            // Then
            assertThat(page).isNotNull();
            assertThat(page.products().size()).isEqualTo(3);
        }

        @Test
        public void givenBrandCriteria_whenFilter_thenReturnCorrectProducts() {
            // Given
            productService.addProduct(Product.builder().brand("daglas").build());
            productService.addProduct(Product.builder().brand("daglas").build());
            productService.addProduct(Product.builder().name("daglas").brand("ddaglas").build());
            productService.addProduct(Product.builder().provider("daglas & co.").build());

            var criteria = ProductsCriteria.builder().brandsNamesToInclude(Set.of("daglas", "gowno")).build();

            // When
            ProductListResponseDto page = productService.getProductsMatchingCriteria(
                criteria, PageRequest.of(0, 10));

            // Then
            assertThat(page.products().size()).isEqualTo(2);

            for (ProductDto productDto : page.products()) {
                Optional<FullProductDto> product = productService.getProductById(productDto.id());

                assertThat(product).isPresent();
                assertThat(product.get().brand()).isEqualTo("daglas");
            }
        }
    }


//
//    @Test
//    public void givenIngredientsCriteria_whenFilter_thenReturnCorrectProducts() {
//        // Given
//        productService.addProduct(Product.builder().brand("karfur")
//            .ingredients(Arrays.asList("ziemniak", "marchewka", "burak")).build());
//        productService.addProduct(Product.builder().brand("karfur")
//            .ingredients(List.of("burak")).build());
//        productService.addProduct(Product.builder().brand("karfur")
//            .ingredients(Arrays.asList("ziemniak", "marchewka")).build());
//        productService.addProduct(Product.builder().brand("karfur")
//            .ingredients(Arrays.asList("marchewka", "burak")).build());
//        productService.addProduct(Product.builder().brand("karfur")
//            .ingredients(Arrays.asList("ziemniak", "burak")).build());
//
//        // When
//        Page<ProductDto> ziemniakBurakPage = productService.getProductsMatching(
//            ProductFilterCriteria.builder()
//                .ingredients(new String[] {"ziemniak", "burak"})
//                .build(), pageRequest);
//        List<ProductDto> ziemniakBurakProducts = ziemniakBurakPage.getContent();
//
//        Page<ProductDto> ziemniakPage = productService.getProductsMatching(
//            ProductFilterCriteria.builder()
//                .ingredients(new String[] {"ziemniak"})
//                .build(), pageRequest);
//        List<ProductDto> ziemniakProducts = ziemniakPage.getContent();
//
//        // Then
//        assertThat(ziemniakBurakProducts.size()).isEqualTo(2);
//        for (ProductDto productDto : ziemniakBurakProducts) {
//            Optional<FullProductDto> product = productService.getProductById(productDto.id());
//
//            assertThat(product).isPresent();
//            assertThat(product.get().ingredients()).contains("ziemniak");
//            assertThat(product.get().ingredients()).contains("burak");
//        }
//
//        assertThat(ziemniakProducts.size()).isEqualTo(3);
//        for (ProductDto productDto : ziemniakProducts) {
//            Optional<FullProductDto> product = productService.getProductById(productDto.id());
//
//            assertThat(product).isPresent();
//            assertThat(product.get().ingredients()).contains("ziemniak");
//        }
//    }
//
//    @Test
//    public void givenVolumeCriteria_whenFilter_thenReturnCorrectProducts() {
//        // Given
//        productService.addProduct(Product.builder().name("woda").brand("cisowiana")
//            .volume(3).build());
//        productService.addProduct(Product.builder().name("woda").brand("contigo")
//            .volume(4).build());
//        productService.addProduct(Product.builder().name("woda").brand("skarb życia muszyna")
//            .volume(5).build());
//        productService.addProduct(Product.builder().name("woda").brand("nałęczowiana")
//            .volume(6).build());
//        productService.addProduct(Product.builder().name("woda").brand("pepsi")
//            .volume(7).build());
//
//        // When
//        Page<ProductDto> lessPage = productService.getProductsMatching(
//            ProductFilterCriteria.builder().volumeTo(6).build(), pageRequest);
//        List<ProductDto> lessProducts = lessPage.getContent();
//
//        // Then
//        assertThat(lessProducts.size()).isEqualTo(4);
//        for (ProductDto productDto : lessProducts) {
//            Optional<FullProductDto> product = productService.getProductById(productDto.id());
//
//            assertThat(product).isPresent();
//            assertThat(product.get().volume()).isLessThanOrEqualTo(6);
//        }
//    }
//
//    @Test
//    public void givenVolumeFromCriteria_whenFilter_thenReturnCorrectProducts() {
//        // Given
//
//        // When
//        Page<ProductDto> greaterPage = productService.getProductsMatching(
//            ProductFilterCriteria.builder().volumeFrom(4).build(), pageRequest);
//        List<ProductDto> greaterProducts = greaterPage.getContent();
//
//        // Then
//        assertThat(greaterProducts.size()).isEqualTo(4);
//        for (ProductDto productDto : greaterProducts) {
//            Optional<FullProductDto> product = productService.getProductById(productDto.id());
//
//            assertThat(product).isPresent();
//            assertThat(product.get().volume()).isGreaterThanOrEqualTo(4);
//        }
//    }
//
//    @Test
//    public void givenVolumeBetweenCriteria_whenFilter_thenReturnCorrectProducts() {
//        // Given
//
//        // When
//        Page<ProductDto> betweenPage = productService.getProductsMatching(
//            ProductFilterCriteria.builder().volumeFrom(3).volumeTo(6).build(), pageRequest);
//        List<ProductDto> betweenProducts = betweenPage.getContent();
//
//        // Then
//        assertThat(betweenProducts.size()).isEqualTo(4);
//        for (ProductDto productDto : betweenProducts) {
//            Optional<FullProductDto> product = productService.getProductById(productDto.id());
//
//            assertThat(product).isPresent();
//            assertThat(product.get().volume()).isLessThanOrEqualTo(6);
//            assertThat(product.get().volume()).isGreaterThanOrEqualTo(3);
//        }
//    }
//
//    @Test
//    public void givenMultiCriteria_whenFilter_thenReturnCorrectProducts() {
//        // Given
//
//        // Proper
//        productService.addProduct(
//            Product.builder()
//                .name("pasta do zębów")
//                .brand("karfur")
//                .provider("żapka")
//                .volume(12)
//                .ingredients(Arrays.asList("polietylen", "guma guar", "metanol"))
//                .build());
//        productService.addProduct(
//            Product.builder()
//                .name("poper")
//                .brand("karfur")
//                .provider("żapka")
//                .volume(14)
//                .ingredients(Arrays.asList("rak", "guma", "metanol"))
//                .build());
//
//        // Red herrings
//        productService.addProduct(
//            Product.builder().name("ziemniak").brand("karfur").provider("żapka").volume(13)
//                .build());
//        productService.addProduct(
//            Product.builder().name("obrazek").brand("karfur").provider("żapka").volume(15)
//                .build());
//        productService.addProduct(
//            Product.builder().name("szampą").brand("żapka").provider("karfur").volume(200)
//                .build());
//        productService.addProduct(
//            Product.builder().name("szamka").brand("grycan").provider("żapka").volume(13).build());
//        productService.addProduct(
//            Product.builder().name("baton").brand("sniker").provider("żapka").volume(12).build());
//        productService.addProduct(
//            Product.builder().name("marchew").brand("ogródek").provider("rosman").volume(1)
//                .build());
//        productService.addProduct(
//            Product.builder().name("pianka do golenia").brand("golibroda").provider("romsan")
//                .volume(12).build());
//        productService.addProduct(
//            Product.builder().name("pasta do zębów").brand("kolgat").provider("romsan").volume(12)
//                .build());
//        productService.addProduct(
//            Product.builder().name("pasta do zębów").brand("sęsodę").provider("romsan").volume(12)
//                .build());
//        productService.addProduct(
//            Product.builder().name("pasta do zębów").brand("elmech").provider("romsan").volume(12)
//                .build());
//        productService.addProduct(
//            Product.builder().name("pasta do zębów").brand("akuafresz").provider("romsan")
//                .volume(12).build());
//        productService.addProduct(
//            Product.builder().name("pasta do butów").brand("kiwi").provider("romsan").volume(12)
//                .build());
//
//        // When
//        Page<ProductDto> kerfurZabkaPage = productService.getProductsMatching(
//            ProductFilterCriteria.builder()
//                .brand("karfur")
//                .volumeFrom(12)
//                .volumeTo(14)
//                .provider("żapka")
//                .ingredients(new String[] {"metanol"})
//                .build(), pageRequest);
//        List<ProductDto> karfurZapkaProducts = kerfurZabkaPage.getContent();
//
//        // Then
//        assertThat(karfurZapkaProducts.size()).isEqualTo(2);
//
//        for (ProductDto productDto : karfurZapkaProducts) {
//            Optional<FullProductDto> product = productService.getProductById(productDto.id());
//
//            assertThat(product).isPresent();
//            assertThat(product.get().provider()).isEqualTo("żapka");
//            assertThat(product.get().brand()).isEqualTo("karfur");
//            assertThat(product.get().volume()).isBetween(12, 14);
//            assertThat(product.get().ingredients()).contains("metanol");
//        }
//    }
//
//    @Test
//    public void givenNameCriteria_whenFilter_thenReturnCorrectProducts() {
//        // Given
//        ProductDtoMapper productDtoMapper = new ProductDtoMapper();
//        PageRequest pageRequest = PageRequest.of(0, 10);
//        productService.addProduct(Product.builder()
//            .name("Matte Blush")
//            .brand("Maybelline")
//            .shortDescription("Matte blush for a natural finish.")
//            .longDescription("Controls shine and blurs pores for a natural, matte finish.")
//            .build());
//        productService.addProduct(Product.builder()
//            .name("Pressed Powder")
//            .brand("Rimmel London")
//            .shortDescription("Pressed powder for smooth skin.")
//            .longDescription(
//                "Helps minimize the appearance of pores and leaves a smooth, matte finish.")
//            .build());
//        productService.addProduct(Product.builder()
//            .name("Amazonian Clay 12-Hour Blush")
//            .brand("Tarte")
//            .shortDescription("Long-lasting blush with Amazonian clay.")
//            .longDescription("Infused with Amazonian clay for 12 hours of fade-free wear.")
//            .build());
//        productService.addProduct(Product.builder()
//            .name("Stay Matte Pressed Powder")
//            .brand("Rimmel London")
//            .shortDescription("Matte pressed powder for a smooth finish.")
//            .longDescription(
//                "Helps minimize the appearance of pores and leaves a smooth, matte finish.")
//            .build());
//        productService.addProduct(Product.builder()
//            .name("Stay Matte Powder")
//            .brand("Lovely")
//            .shortDescription("Matte powder for smooth skin.")
//            .longDescription(
//                "Helps minimize the appearance of pores and leaves a smooth, matte finish.")
//            .build());
//        String searchTerm = "Matte Powder Rimmel";
//
//        // When
//        Page<ProductDto> page = productService.getProductsMatching(
//            ProductFilterCriteria.builder().name(searchTerm).build(), pageRequest);
//        List<ProductDto> liquidProducts = page.getContent();
//
//        // Then
//        assertThat(liquidProducts.size()).isEqualTo(2);
//
//        for (ProductDto productDto : liquidProducts) {
//            Optional<FullProductDto> product = productService.getProductById(productDto.id());
//
//            assertThat(product).isPresent();
//
//            String brand = product.get().brand().toLowerCase();
//            String name = product.get().name().toLowerCase();
//            String longDescription = product.get().longDescription().toLowerCase();
//            String shortDescription = productDto.shortDescription().toLowerCase();
//
//            String[] searchTermsArray = searchTerm.split("\\s+");
//            for (String term : searchTermsArray) {
//                assertThat(brand.contains(term.toLowerCase())
//                    || name.contains(term.toLowerCase())
//                    || longDescription.contains(term.toLowerCase())
//                    || shortDescription.contains(term.toLowerCase())).isTrue();
//            }
//        }
//    }
}
