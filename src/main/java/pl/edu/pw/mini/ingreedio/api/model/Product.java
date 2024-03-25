package pl.edu.pw.mini.ingreedio.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Table(name = "products")
@Data
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private String name;
    @Column
    private String smallImageUrl;
    @Column
    private String largeImageUrl;
    @Column
    private String provider;
    @Column
    private String brand;
    @Column
    private String shortDescription;
    @Column
    private String longDescription;
    @Column
    private Integer volume;

    public Product(String name,
                   String smallImageUrl,
                   String largeImageUrl,
                   String provider,
                   String brand,
                   String shortDescription,
                   String longDescription,
                   Integer volume) {
        this.name = name;
        this.smallImageUrl = smallImageUrl;
        this.largeImageUrl = largeImageUrl;
        this.provider = provider;
        this.brand = brand;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.volume = volume;
    }

    public Product() {

    }

}
