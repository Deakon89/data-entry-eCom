package com.ecom.app.service;

import com.ecom.app.model.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ecom.app.Repo.ProdRepo;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Service
public class ProdService {

    private final ProdRepo repository;
    private final ObjectMapper mapper;

    public ProdService(ProdRepo repository, ObjectMapper mapper) {
        this.mapper = mapper;
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

     /**  
     * Legge dal JSON e salva tutti i prodotti.  
     * @param input stream del file JSON  
     * @throws IOException se il parsing fallisce  
     */
    public void saveAllFromJson(InputStream input) throws IOException {
        // Deserializza in array di Product
        Product[] productsArray = mapper.readValue(input, Product[].class);
        // Salva tutti
        repository.saveAll(Arrays.asList(productsArray));
    }
}
