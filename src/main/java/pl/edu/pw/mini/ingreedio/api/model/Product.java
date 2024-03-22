package pl.edu.pw.mini.ingreedio.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name="products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
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
