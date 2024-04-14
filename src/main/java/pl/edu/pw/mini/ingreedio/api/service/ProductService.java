package pl.edu.pw.mini.ingreedio.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pw.mini.ingreedio.api.dto.FullProductDto;
import pl.edu.pw.mini.ingreedio.api.dto.ProductDto;
import pl.edu.pw.mini.ingreedio.api.mapper.FullProductDtoMapper;
import pl.edu.pw.mini.ingreedio.api.mapper.ProductDtoMapper;
import pl.edu.pw.mini.ingreedio.api.model.Product;
import pl.edu.pw.mini.ingreedio.api.repository.ProductRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        return productRepository.findById(id).map(fullProductDtoMapper::apply).orElse(null);
    }

    public Product addProduct(Product product) {
        product.setId(sequenceGenerator.generateSequence(Product.SEQUENCE_NAME));
        return productRepository.save(product);
    }

    //private boolean fetchFilteredData(Function<> boolean fetchedInitialData)

    public List<FullProductDto> filterProducts(String name,
                                               String provider,
                                               String brand,
                                               Integer volumeFrom, Integer volumeTo,
                                               String ingredient) {
        boolean fetchedInitialData = false;

        Set<FullProductDto> result = new HashSet<>();

        if (name != null)
            fetchedInitialData = fetchData(result,
                    productRepository.findByNameContaining(name),
                    false);

        if (provider != null)
            fetchedInitialData = fetchData(result,
                    productRepository.findByProvider(provider),
                    fetchedInitialData);

        if (brand != null)
            fetchedInitialData = fetchData(result,
                    productRepository.findByBrand(brand),
                    fetchedInitialData);

        if (volumeFrom != null || volumeTo != null)
            fetchedInitialData = fetchData(result,
                    productRepository.findByVolumeBetween(volumeFrom, volumeTo),
                    fetchedInitialData);

        if (ingredient != null)
            fetchedInitialData = fetchData(result,
                    productRepository.findByIngredientsContaining(ingredient),
                    fetchedInitialData);

        if (!fetchedInitialData)
            return productRepository.findAll().stream().map(fullProductDtoMapper).toList();

        return result.stream().toList();
    }

    private boolean fetchData(Set<FullProductDto> set, Stream<Product> data, boolean fetchedInitialData) {
        var mappedData = data.map(fullProductDtoMapper).collect(Collectors.toSet());

        if (fetchedInitialData) {
            set.retainAll(mappedData);
        } else {
            set.addAll(mappedData);
        }

        return true;
    }
}