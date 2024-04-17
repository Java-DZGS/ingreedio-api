package pl.edu.pw.mini.ingreedio.api.repository;

import java.util.List;
import pl.edu.pw.mini.ingreedio.api.criteria.ProductFilterCriteria;
import pl.edu.pw.mini.ingreedio.api.dto.FullProductDto;

public interface CustomizedProductRepository {
    List<FullProductDto> filterProducts(ProductFilterCriteria criteria);
}
