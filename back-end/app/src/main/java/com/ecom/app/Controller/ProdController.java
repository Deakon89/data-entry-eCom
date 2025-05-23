package com.ecom.app.Controller;

import com.ecom.app.model.Product;
import com.ecom.app.service.ProdService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProdController {

    private final ProdService service;

    public ProdController(ProdService service) {
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

}

