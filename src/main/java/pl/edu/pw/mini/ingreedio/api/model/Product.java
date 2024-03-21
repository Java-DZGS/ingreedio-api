package pl.edu.pw.mini.ingreedio.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.List;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String url;
    private String provider;
    private String brand;
    private String shortDescription;
    private String longDescription;
    private Double volume;

    public Product(String name,
                   String url,
                   String provider,
                   String brand,
                   String shortDescription,
                   String longDescription,
                   Double volume) {
        this.name = name;
        this.url = url;
        this.provider = provider;
        this.brand = brand;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.volume = volume;
    }
  
    public Product() {

    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getProvider() {
        return provider;
    }

    public String getBrand() {
        return brand;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public Double getVolume() {
        return volume;
    }
}
