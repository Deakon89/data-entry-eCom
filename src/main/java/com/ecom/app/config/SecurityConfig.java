package com.ecom.app.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;



@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
          // 1) abilita CORS
          .cors().and()

          // 2) disabilita CSRF perché usi Basic
          .csrf(csrf -> csrf.disable())

          // 3) regole di accesso
          .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
            .requestMatchers("/api/products/**").authenticated()
            .anyRequest().permitAll()
          )
          .httpBasic();

        return http.build();
    }

    // *** questo bean ri-introduce gli header CORS ***
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of("https://fenchinamanidicarta.netlify.app"));  // l’origin di Angular
        cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cfg);
        return src;
    }

       @Bean
    public UserDetailsService userDetailsService(
        @Value("${spring.security.user.name}") String username,
        @Value("${spring.security.user.password}") String password
    ) {
        // In-memory single user, senza bisogno di entità o tabelle
        UserDetails user = User.withDefaultPasswordEncoder()
            .username(username)
            .password(password)
            .roles("ADMIN")
            .build();
        return new InMemoryUserDetailsManager(user);
    }
}



