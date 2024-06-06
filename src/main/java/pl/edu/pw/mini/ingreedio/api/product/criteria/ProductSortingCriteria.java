package pl.edu.pw.mini.ingreedio.api.product.criteria;

import org.springframework.data.domain.Sort;

public record ProductSortingCriteria(Sort.Direction order, SortingBy byField) { }