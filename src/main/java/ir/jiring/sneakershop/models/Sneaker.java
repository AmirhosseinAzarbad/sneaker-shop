package ir.jiring.sneakershop.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document(indexName = "sneakers")
@Data
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class  Sneaker {

    @Id
    @Field(type = FieldType.Keyword)
    @Column(nullable = false, updatable = false)
    private UUID id = UUID.randomUUID();

    @Column(name = "name1",nullable = false)
    private String name;

    @Column(name = "brand1",nullable = false)
    private String brand;

    @Column(name = "price1",nullable = false)
    private BigDecimal price;

    @OneToMany(mappedBy = "sneaker", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<SneakerVariant> variants = new ArrayList<>();

}
