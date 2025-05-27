// package com.ecom.app.Controller;

// import com.ecom.app.Repo.ProdRepo;
// import com.ecom.app.model.Product;
// import com.ecom.app.service.ProdService;
// import com.fasterxml.jackson.databind.ObjectMapper;

// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.web.multipart.MultipartFile;

// import java.io.IOException;
// import java.nio.file.Path;
// import java.nio.file.Files;
// import java.nio.file.Paths;
// import java.nio.file.StandardCopyOption;
// import java.util.Arrays;
// import java.util.List;

// @RestController
// @RequestMapping("/api/products")
// @CrossOrigin(origins = "*")
// public class ProdController {

//     private final ProdService service;
//     private final ProdRepo repository;
    

//     public ProdController(ProdService service, ProdRepo repository) {
//         this.repository = repository;
//         this.service = service;
//     }

//     @PostMapping
//     public Product create(@RequestBody Product product) {
//         return service.save(product);
//     }

//     @GetMapping
//     public List<Product> getAll() {
//         return service.getAll();
//     }

//     @DeleteMapping("/{id}")
//     public void delete(@PathVariable Long id) {
//         service.deleteById(id);
//     }
    
//     @PutMapping("/{id}")
// public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product updatedProduct) {
//     return repository.findById(id)
//         .map(existing -> {
//             updatedProduct.setId(id);
//             return ResponseEntity.ok(repository.save(updatedProduct));
//         })
//         .orElse(ResponseEntity.notFound().build());
// }

//     @PostMapping("/upload")
// public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
//     try {
//         String uploadDir = "uploads/";
//         String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
//         Path filepath = Paths.get(uploadDir, filename);

//         Files.createDirectories(filepath.getParent());
//         Files.copy(file.getInputStream(), filepath, StandardCopyOption.REPLACE_EXISTING);

//         String fileUrl = "http://localhost:8080/" + uploadDir + filename;
//         return ResponseEntity.ok(fileUrl);
//     } catch (IOException e) {
//         e.printStackTrace();
//         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore upload");
//     }
// }

// @PostMapping("/upload-json")
// public ResponseEntity<?> uploadProductList(@RequestParam("file") MultipartFile file) {
//     try {
//         ObjectMapper mapper = new ObjectMapper();
//         List<Product> products = Arrays.asList(mapper.readValue(file.getInputStream(), Product[].class));
//         repository.saveAll(products);
//         return ResponseEntity.ok("Prodotti caricati con successo");
//     } catch (Exception e) {
//         e.printStackTrace();
//         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Errore nel file");
//     }
// }


// }
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
                return ResponseEntity.ok(service.save(updated));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    // —————————————— UPLOAD + CREA PRODOTTO ——————————————

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAndCreate(
        @RequestParam(name = "file", required = false) MultipartFile file,
        @RequestParam String name,
        @RequestParam(required = false) String description,
        @RequestParam BigDecimal price,
        @RequestParam(required = false) String paymentLink
    ) {
        String imageUrl = null;

        // 1) se è presente un file, valida e salva
        if (file != null && !file.isEmpty()) {
            String contentType = file.getContentType();
            if (contentType == null ||
               !(contentType.equals("image/jpeg") || contentType.equals("image/png"))
            ) {
                return ResponseEntity
                    .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body("Formato immagine non supportato. Usa JPEG/PNG.");
            }
            try {
                String uploadDir = "uploads/";
                String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path filepath = Paths.get(uploadDir, filename);
                Files.createDirectories(filepath.getParent());
                Files.copy(file.getInputStream(), filepath, StandardCopyOption.REPLACE_EXISTING);
                imageUrl = "http://localhost:8080/" + uploadDir + filename;
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante il salvataggio dell’immagine");
            }
        }

        // 2) costruisci ed effettua il save del prodotto
        Product prod = new Product();
        prod.setName(name);
        prod.setDescription(description);
        prod.setPrice(price);
        prod.setPaymentLink(paymentLink);
        if (imageUrl != null) {
            prod.setImageUrl(imageUrl);
        }

        Product saved = service.save(prod);
        return ResponseEntity.ok(saved);
    }

    // —————————————— IMPORT JSON DI PRODOTTI ——————————————

    @PostMapping(value = "/upload-json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadJsonList(@RequestParam("file") MultipartFile file) {
        try {
            service.saveAllFromJson(file.getInputStream());
            return ResponseEntity.ok("Prodotti caricati con successo");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Errore nel parsing del file JSON");
        }
    }
}

