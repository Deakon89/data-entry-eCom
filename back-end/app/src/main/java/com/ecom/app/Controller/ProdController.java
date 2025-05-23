package com.ecom.app.Controller;

import com.ecom.app.Repo.ProdRepo;
import com.ecom.app.model.Product;
import com.ecom.app.service.ProdService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProdController {

    private final ProdService service;
    private final ProdRepo repository;
    

    public ProdController(ProdService service, ProdRepo repository) {
        this.repository = repository;
        this.service = service;
    }

    @PostMapping
    public Product create(@RequestBody Product product) {
        return service.save(product);
    }

    @GetMapping
    public List<Product> getAll() {
        return service.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
    
    @PutMapping("/{id}")
public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product updatedProduct) {
    return repository.findById(id)
        .map(existing -> {
            updatedProduct.setId(id);
            return ResponseEntity.ok(repository.save(updatedProduct));
        })
        .orElse(ResponseEntity.notFound().build());
}

    @PostMapping("/upload")
public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
    try {
        String uploadDir = "uploads/";
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filepath = Paths.get(uploadDir, filename);

        Files.createDirectories(filepath.getParent());
        Files.copy(file.getInputStream(), filepath, StandardCopyOption.REPLACE_EXISTING);

        String fileUrl = "http://localhost:8080/" + uploadDir + filename;
        return ResponseEntity.ok(fileUrl);
    } catch (IOException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore upload");
    }
}

@PostMapping("/upload-json")
public ResponseEntity<?> uploadProductList(@RequestParam("file") MultipartFile file) {
    try {
        ObjectMapper mapper = new ObjectMapper();
        List<Product> products = Arrays.asList(mapper.readValue(file.getInputStream(), Product[].class));
        repository.saveAll(products);
        return ResponseEntity.ok("Prodotti caricati con successo");
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Errore nel file");
    }
}


}

