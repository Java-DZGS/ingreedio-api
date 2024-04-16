package pl.edu.pw.mini.ingreedio.api.service;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.edu.pw.mini.ingreedio.api.IntegrationTest;
import pl.edu.pw.mini.ingreedio.api.dto.FullProductDto;
import pl.edu.pw.mini.ingreedio.api.dto.ProductDto;
import pl.edu.pw.mini.ingreedio.api.model.Product;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductServiceTest extends IntegrationTest {
    @Autowired
    private ProductService productService;

    @Test
    @Order(1)
    public void givenProductObject_whenSaveProduct_thenReturnProductObject() {
        // Given
        Product product = Product.builder().name("test").build();

        // When
        Product savedProduct = productService.addProduct(product);

        // Then
        assertThat(savedProduct).isNotNull();
    }

    @Test
    @Order(2)
    public void givenProductsList_whenGetAllProducts_thenReturnProductsList() {
        // Given
        productService.addProduct(Product.builder().name("test1").build());
        productService.addProduct(Product.builder().name("test2").build());
        productService.addProduct(Product.builder().name("test3").build());

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
        FullProductDto fullProductDto = productService.getProductById(1L);

        // Then
        assertThat(fullProductDto).isNotNull();
        assertThat(fullProductDto.id()).isEqualTo(1L);
        assertThat(fullProductDto.name()).isEqualTo("testProduct");
    }

    @Test
    @Order(4)
    public void givenNoCriteria_whenFilter_thenReturnAllProducts() {
        // When
        List<FullProductDto> res = productService.filterProducts(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());

        // Then
        assertThat(res).isNotNull();
        assertThat(res.size()).isEqualTo(4);
    }

    @Test
    @Order(5)
    public void givenCriteria_whenFilter_thenReturnCorrectProducts() {
        // Given
        productService.addProduct(Product.builder().brand("daglas").build());
        productService.addProduct(Product.builder().brand("daglas").build());
        productService.addProduct(Product.builder().provider("daglas").build());
        productService.addProduct(Product.builder().brand("daglas & co.").build());

        // When
        List<FullProductDto> daglasProducts = productService.filterProducts(
                Optional.empty(),
                Optional.empty(),
                Optional.of("daglas"),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());

        // Then
        assertThat(daglasProducts.size()).isEqualTo(2);
        for (FullProductDto productDto : daglasProducts) {
            assertThat(productDto.brand()).isEqualTo("daglas");
        }
    }

    @Test
    @Order(6)
    public void givenMultiCriteria_whenFilter_thenReturnCorrectProducts() {
        // Given
        productService.addProduct(Product.builder().name("szampą").brand("karfur").provider("karfur").volume(200).build());
        productService.addProduct(Product.builder().name("marchew").brand("ogródek").provider("rosman").volume(1).build());
        productService.addProduct(Product.builder().name("poper").brand("karfur").provider("żapka").volume(12).build());
        productService.addProduct(Product.builder().name("szamka").brand("grycan").provider("żapka").volume(13).build());
        productService.addProduct(Product.builder().name("obrazek").brand("reksio").provider("karfur").volume(12).build());
        productService.addProduct(Product.builder().name("baton").brand("sniker").provider("żapka").volume(12).build());
        productService.addProduct(Product.builder().name("pianka do golenia").brand("golibroda").provider("romsan").volume(12).build());
        productService.addProduct(Product.builder().name("pasta do zębów").brand("kolgat").provider("romsan").volume(12).build());
        productService.addProduct(Product.builder().name("pasta do zębów").brand("sęsodę").provider("romsan").volume(12).build());
        productService.addProduct(Product.builder().name("pasta do zębów").brand("elmech").provider("romsan").volume(12).build());
        productService.addProduct(Product.builder().name("pasta do zębów").brand("akuafresz").provider("romsan").volume(12).build());
        productService.addProduct(Product.builder().name("pasta do zębów").brand("karfur").provider("karfur").volume(12).build());
        productService.addProduct(Product.builder().name("pasta do butów").brand("kiwi").provider("romsan").volume(12).build());

        // When
        List<FullProductDto> daglasProducts = productService.filterProducts(
                Optional.of("pasta"),
                Optional.of("romsan"),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());

        // Then
        for (FullProductDto productDto : daglasProducts) {
            assertThat(productDto.brand()).isEqualTo("rosman");
            assertThat(productDto.name()).contains("pasta");
        }
    }
}
