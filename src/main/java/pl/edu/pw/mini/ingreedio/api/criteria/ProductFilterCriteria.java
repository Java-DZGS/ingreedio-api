package pl.edu.pw.mini.ingreedio.api.criteria;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductFilterCriteria {
    private String provider;
    private String brand;
    private Integer volumeFrom;
    private Integer volumeTo;
    private String[] ingredients;
}
