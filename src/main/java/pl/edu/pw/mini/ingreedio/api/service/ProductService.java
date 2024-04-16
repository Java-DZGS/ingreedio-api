package pl.edu.pw.mini.ingreedio.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pw.mini.ingreedio.api.criteria.ProductFilterCriteria;
import pl.edu.pw.mini.ingreedio.api.dto.FullProductDto;
import pl.edu.pw.mini.ingreedio.api.dto.ProductDto;
import pl.edu.pw.mini.ingreedio.api.mapper.FullProductDtoMapper;
import pl.edu.pw.mini.ingreedio.api.mapper.ProductDtoMapper;
import pl.edu.pw.mini.ingreedio.api.model.Product;
import pl.edu.pw.mini.ingreedio.api.repository.ProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductDtoMapper productDtoMapper;
    private final FullProductDtoMapper fullProductDtoMapper;
    private final SequenceGeneratorService sequenceGenerator;

    public List<ProductDto> getAllProducts() {
        return productRepository
                .findAll()
                .stream()
                .map(productDtoMapper).collect(Collectors.toList());
    }

    public FullProductDto getProductById(Long id) {
        return productRepository.findById(id).map(fullProductDtoMapper).orElse(null);
    }

    public Product addProduct(Product product) {
        product.setId(sequenceGenerator.generateSequence(Product.SEQUENCE_NAME));
        return productRepository.save(product);
    }

    //private boolean fetchFilteredData(Function<> boolean fetchedInitialData)

    public List<FullProductDto> filterProducts(Optional<String> name,
                                               Optional<String> provider,
                                               Optional<String> brand,
                                               Optional<Integer> volumeFrom, Optional<Integer> volumeTo,
                                               String[] ingredients) {
        return productRepository.filterProducts(
                ProductFilterCriteria.builder()
                        .name(name.orElse(null))
                        .provider(provider.orElse(null))
                        .brand(brand.orElse(null))
                        .volumeFrom(volumeFrom.orElse(null))
                        .volumeTo(volumeTo.orElse(null))
                        .ingredients(ingredients)
                        .build());
    }

}