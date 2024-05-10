package pl.edu.pw.mini.ingreedio.api.criteria;

import org.springframework.data.domain.Sort;

public record ProductsSortingCriteria(Sort.Direction order, String byField) { }