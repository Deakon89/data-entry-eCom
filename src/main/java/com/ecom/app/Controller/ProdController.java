package com.ecom.app.Controller;

import com.ecom.app.Repo.ProdRepo;
import com.ecom.app.model.Product;
import com.ecom.app.service.ProdService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/products")

public class ProdController {

    private final ProdService service;
    private final ProdRepo repository;

    public ProdController(ProdService service, ProdRepo repository) {
        this.service = service;
        this.repository = repository;
    }

    // —————————————— CRUD STANDARD ——————————————

    @GetMapping
    public List<Product> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return repository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(
        @PathVariable Long id,
        @RequestBody Product updated
    ) {
        return repository.findById(id)
            .map(existing -> {
                updated.setId(id);
                Product saved = service.save(updated);
                return ResponseEntity.ok(saved);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    // —————————————— UPLOAD + CREA PRODOTTO ——————————————
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> uploadAndCreate(
        @RequestParam(name = "file", required = false) MultipartFile file,
        @RequestParam String name,
        @RequestParam(required = false) String description,
        @RequestParam BigDecimal price,
        @RequestParam(required = false) String paymentLink,
        @RequestParam(required = false) List<String> tags 
    ) throws IOException {
        String imageUrl = service.storeImage(file);
        // Usa il costruttore di default + setter
        Product prod = new Product();
        prod.setName(name);
        prod.setDescription(description);
        prod.setPrice(price);
        prod.setPaymentLink(paymentLink);
        prod.setTags(tags != null ? tags : new ArrayList<>());
        if (imageUrl != null) {
            prod.setImageUrl(imageUrl);
        }
        System.out.println(">>> TAGS RECEIVED: " + tags);
        Product saved = service.save(prod);
        return ResponseEntity.ok(saved);
    }

    // —————————————— IMPORT JSON DI PRODOTTI ——————————————
 @PostMapping(value = "/upload-json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadJsonList(@RequestParam("file") MultipartFile file) {
        try {
            service.saveAllFromJson(file.getInputStream());
            return ResponseEntity.ok("Prodotti caricati con successo");
        } catch (Exception e) {
            return ResponseEntity
              .status(HttpStatus.BAD_REQUEST)
              .body("Errore parsing JSON: " + e.getMessage());
        }
    }
  
}

