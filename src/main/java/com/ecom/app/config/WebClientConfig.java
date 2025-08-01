package com.ecom.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Bean
    public WebClient supabaseWebClient() {
        return WebClient.builder()
            .baseUrl(supabaseUrl)
            .defaultHeader("apikey", supabaseKey)
            .defaultHeader("Authorization", "Bearer " + supabaseKey)
            .build();
    }
}
