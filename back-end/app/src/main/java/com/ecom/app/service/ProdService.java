package com.ecom.app.service;

import com.ecom.app.model.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ecom.app.Repo.ProdRepo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

    public String storeImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;
        // stessa logica del tuo uploadImage
        String uploadDir = "uploads/";
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filepath = Paths.get(uploadDir, filename);
        Files.createDirectories(filepath.getParent());
        Files.copy(file.getInputStream(), filepath, StandardCopyOption.REPLACE_EXISTING);
        return "http://localhost:8080/" + uploadDir + filename;
    }
}
