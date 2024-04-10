package pl.edu.pw.mini.ingreedio.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.pw.mini.ingreedio.api.dto.FullProductDto;
import pl.edu.pw.mini.ingreedio.api.dto.ProductDto;
import pl.edu.pw.mini.ingreedio.api.mapper.FullProductDtoMapper;
import pl.edu.pw.mini.ingreedio.api.mapper.ProductDtoMapper;
import pl.edu.pw.mini.ingreedio.api.model.Product;
import pl.edu.pw.mini.ingreedio.api.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductDtoMapper productDtoMapper;

    @Mock
    private FullProductDtoMapper fullProductDtoMapper;

    @Mock
    private SequenceGeneratorService sequenceGenerator;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    public void setUp() {
        lenient().when(fullProductDtoMapper.apply(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            return new FullProductDto(
                product.getId(),
                product.getName(),
                product.getLargeImageUrl(),
                product.getProvider(),
                product.getBrand(),
                product.getLongDescription(),
                product.getVolume(),
                product.getIngredients()
            );
        });
        productService = new ProductService(
            productRepository,
            productDtoMapper,
            fullProductDtoMapper,
            sequenceGenerator
        );
    }

    @Test
    public void givenProductObject_whenSaveProduct_thenReturnProductObject() {
        // Given
        Product product = new Product();
        product.setName("testProduct");
        given(productRepository.save(product)).willReturn(product);

        // When
        Product savedProduct = productService.addProduct(product);

        // Then
        assertThat(savedProduct).isNotNull();
    }

    @Test
    public void givenProductsList_whenGetAllProducts_thenReturnProductsList() {

        // Given
        Product product1 = new Product();
        product1.setName("testProduct1");
        Product product2 = new Product();
        product1.setName("testProduct2");
        Product product3 = new Product();
        product1.setName("testProduct3");

        given(productRepository.findAll()).willReturn(List.of(product1, product2, product3));

        // When
        List<ProductDto> productsList = productService.getAllProducts();

        // Then
        assertThat(productsList).isNotNull();
        assertThat(productsList.size()).isEqualTo(3);
    }

    @Test
    public void givenProductId_whenGetProductById_thenReturnFullProductDtoObject() {
        // Given
        Product product = new Product();
        product.setId(1L);
        product.setName("testProduct");

        // Mockowanie zachowania repozytorium
        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        // When
        FullProductDto fullProductDto = productService.getProductById(1L);

        // Then
        assertThat(fullProductDto).isNotNull();
        assertThat(fullProductDto.id()).isEqualTo(1L);
        assertThat(fullProductDto.name()).isEqualTo("testProduct");
    }
}
