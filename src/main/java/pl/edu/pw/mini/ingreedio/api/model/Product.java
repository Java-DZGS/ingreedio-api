package pl.edu.pw.mini.ingreedio.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "products")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

}
