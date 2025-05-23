package com.ecom.app.service;

import com.ecom.app.model.Product;
import com.ecom.app.Repo.ProdRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdService {

    private final ProdRepo repository;

    public ProdService(ProdRepo repository) {
        this.repository = repository;
    }

    public Product save(Product product) {
        return repository.save(product);
    }

    public List<Product> getAll() {
        return repository.findAll();
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
