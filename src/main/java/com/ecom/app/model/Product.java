package com.ecom.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.ecom.app.config.JsonListConverter;

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

    private BigDecimal priceSmall;
    private BigDecimal priceMedium;
    private BigDecimal priceLarge;

    private String imageUrl;
    
    @Convert(converter = JsonListConverter.class)
    @Column(columnDefinition = "TEXT", nullable = false)
    private List<String> tags;
    
}
