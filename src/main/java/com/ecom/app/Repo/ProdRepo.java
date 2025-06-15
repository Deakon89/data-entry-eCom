package com.ecom.app.Repo;

import com.ecom.app.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdRepo extends JpaRepository<Product, Long> {
}

