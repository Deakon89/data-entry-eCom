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
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.nio.file.Paths;
// import java.nio.file.StandardCopyOption;
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

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Product> createJson(@RequestBody Product prod) {
    // salva tutto (inclusi tags e imageUrl se presente)
    Product saved = service.save(prod);
    return ResponseEntity.status(HttpStatus.CREATED).body(saved);
}

    // —————————————— UPLOAD + CREA PRODOTTO ——————————————
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> uploadAndCreate(
        @RequestParam(name = "file", required = false) MultipartFile file,
        @RequestParam String name,
        @RequestParam(required = false) String description,
        @RequestParam BigDecimal priceSmall,
        @RequestParam BigDecimal priceMedium,
        @RequestParam BigDecimal priceLarge,
        @RequestParam(required = false) List<String> tags 
    ) throws IOException {
        try {
            String imageUrl = service.storeImage(file);
            Product prod = new Product();
            prod.setName(name);
            prod.setDescription(description);
            prod.setPriceSmall(priceSmall);
            prod.setPriceMedium(priceMedium);
            prod.setPriceLarge(priceLarge);
            prod.setTags(tags != null ? tags : new ArrayList<>());
            if (imageUrl != null) {
                prod.setImageUrl(imageUrl);
            }
            System.out.println(">>> TAGS RECEIVED: " + tags);
            Product saved = service.save(prod);
            System.out.println(">>> PRODUCT SAVED SUCCESSFULLY: " + saved);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            System.err.println(">>> ERROR CREATING PRODUCT: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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

