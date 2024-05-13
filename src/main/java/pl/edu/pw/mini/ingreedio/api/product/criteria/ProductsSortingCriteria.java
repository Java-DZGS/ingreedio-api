package pl.edu.pw.mini.ingreedio.api.product.criteria;

import org.springframework.data.domain.Sort;

public record ProductsSortingCriteria(Sort.Direction order, String byField) { }