package com.ecom.app.service;

import com.ecom.app.model.Product;
import com.ecom.app.Repo.ProdRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Service
public class ProdService {

    private final ProdRepo repository;
    private final ObjectMapper mapper;
    private final WebClient supabaseClient;

    @Value("${supabase.bucket}")
    private String bucket;

    @Value("${supabase.url}")
    private String supabaseUrl;

    public ProdService(ProdRepo repository,
                       ObjectMapper mapper,
                       WebClient supabaseWebClient) {
        this.repository = repository;
        this.mapper = mapper;
        this.supabaseClient = supabaseWebClient;
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

    public void saveAllFromJson(java.io.InputStream input) throws IOException {
        Product[] productsArray = mapper.readValue(input, Product[].class);
        repository.saveAll(Arrays.asList(productsArray));
    }

    /**
     * Carica l'immagine su Supabase Storage e restituisce l'URL pubblico.
     */
    public String storeImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Genera un nome univoco per il file
        String filename = Instant.now().toEpochMilli() + "_" + file.getOriginalFilename();

        // Esegue la PUT sull'endpoint Supabase Storage
        supabaseClient.put()
            .uri(uriBuilder -> uriBuilder
                .path("/storage/v1/object/{bucket}/{file}")
                .build(bucket, filename))
            .header("Content-Type", file.getContentType())
            .bodyValue(file.getBytes())
            .retrieve()
            .bodyToMono(Void.class)
            .block();

        // Costruisce l'URL pubblico usando la variabile supabaseUrl
        return String.format("%s/storage/v1/object/public/%s/%s",
            supabaseUrl, bucket, filename);
    }
}
