package com.ecom.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length = 1000)
    private String description;

    private BigDecimal price;

    private String imageUrl;

     private String paymentLink;

  @ElementCollection
  @CollectionTable(
    name = "product_tags", 
    joinColumns = @JoinColumn(name = "product_id")
  )
  @OrderColumn(name = "tag_index")              // crea una colonna per l’ordine
  @Column(name = "tag")                         // nome della colonna che contiene il valore
  private List<String> tags = new ArrayList<>();
}
