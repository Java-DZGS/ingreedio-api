package pl.edu.pw.mini.ingreedio.api.service;

import static org.assertj.core.api.Assertions.assertThat;

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
    public void givenCriteria_whenFilter_thenReturnCorrectProducts() {
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
    public void givenMultiCriteria_whenFilter_thenReturnCorrectProducts() {
        // Given
        productService.addProduct(
            Product.builder().name("szampą").brand("żapka").provider("karfur").volume(200)
                .build());
        productService.addProduct(
            Product.builder().name("marchew").brand("ogródek").provider("rosman").volume(1)
                .build());
        productService.addProduct(
            Product.builder().name("poper").brand("karfur").provider("żapka").volume(12).build());
        productService.addProduct(
            Product.builder().name("szamka").brand("grycan").provider("żapka").volume(13).build());
        productService.addProduct(
            Product.builder().name("obrazek").brand("karfur").provider("żapka").volume(12)
                .build());
        productService.addProduct(
            Product.builder().name("baton").brand("sniker").provider("żapka").volume(12).build());
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
            Product.builder().name("pasta do zębów").brand("karfur").provider("żapka").volume(12)
                .build());
        productService.addProduct(
            Product.builder().name("pasta do butów").brand("kiwi").provider("romsan").volume(12)
                .build());

        // When
        List<ProductDto> karfurZapkaProducts = productService.getProductsMatching(
            ProductFilterCriteria.builder().brand("karfur").provider("żapka").build());

        // Then
        assertThat(karfurZapkaProducts.size()).isEqualTo(3);

        for (ProductDto productDto : karfurZapkaProducts) {
            Optional<FullProductDto> product = productService.getProductById(productDto.id());

            assertThat(product).isPresent();
            assertThat(product.get().provider()).isEqualTo("żapka");
            assertThat(product.get().brand()).isEqualTo("karfur");
        }
    }
}
