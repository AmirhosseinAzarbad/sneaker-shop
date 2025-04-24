package ir.jiring.sneakershop.models;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
public class  Sneaker {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "name1",nullable = false)
    private String name;

    @Column(name = "brand1",nullable = false)
    private String brand;

    @Column(name = "price1",nullable = false)
    private BigDecimal price;

    @OneToMany(mappedBy = "sneaker", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<SneakerVariant> variants = new ArrayList<>();

}
