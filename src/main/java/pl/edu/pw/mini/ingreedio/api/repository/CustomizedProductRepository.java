package pl.edu.pw.mini.ingreedio.api.repository;

import pl.edu.pw.mini.ingreedio.api.criteria.ProductFilterCriteria;
import pl.edu.pw.mini.ingreedio.api.dto.FullProductDto;

import java.util.List;

public interface CustomizedProductRepository {
    List<FullProductDto> filterProducts(ProductFilterCriteria criteria);
}
