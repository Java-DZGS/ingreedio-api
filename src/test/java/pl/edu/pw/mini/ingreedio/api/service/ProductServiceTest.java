package pl.edu.pw.mini.ingreedio.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
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

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductServiceTest extends IntegrationTest {
    @Autowired
    private ProductService productService;

    @Test
    @Order(1)
    public void givenProductObject_whenSaveProduct_thenReturnProductObject() {
        // Given
        Product product = new Product();
        product.setName("testProduct");

        // When
        Product savedProduct = productService.addProduct(product);

        // Then
        assertThat(savedProduct).isNotNull();
    }

    @Test
    @Order(2)
    public void givenProductsList_whenGetAllProducts_thenReturnProductsList() {

        // Given
        Product product1 = new Product();
        product1.setName("testProduct1");
        Product product2 = new Product();
        product2.setName("testProduct2");
        Product product3 = new Product();
        product3.setName("testProduct3");

        productService.addProduct(product1);
        productService.addProduct(product2);
        productService.addProduct(product3);

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
}
