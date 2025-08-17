package sn.msante.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import sn.msante.security.JwtAuthenticationEntryPoint;
import sn.msante.security.JwtAuthenticationFilter;
import sn.msante.security.MSanteAuthenticationProvider;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;

/**
 * Security configuration for development environment
 */
@Configuration
@EnableWebSecurity
@Profile("!prod")
public class SecurityConfig {
    
    @Value("${msante.security.cors.allowed-origins:http://localhost:3000,http://localhost:4200}")
    private String allowedOrigins;
    
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    public SecurityConfig(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                         JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/health", "/actuator/health").permitAll()
                .requestMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/otp", "/api/auth/verify").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/practitioners/search").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/availability/search").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/bookings").permitAll() // Guest booking
                
                // Patient endpoints
                .requestMatchers("/api/patient/**").hasRole("PATIENT")
                
                // Practitioner endpoints  
                .requestMatchers("/api/practitioner/**").hasRole("PRACTITIONER")
                
                // Admin endpoints
                .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "SUPERADMIN")
                
                // Authenticated endpoints
                .requestMatchers("/api/bookings/my").authenticated()
                .requestMatchers("/api/profile/**").authenticated()
                .requestMatchers("/api/messages/**").authenticated()
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .addFilterBefore(jwtAuthenticationFilter, BasicAuthenticationFilter.class);
            
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
                config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                config.setAllowCredentials(true);
                config.setAllowedHeaders(Collections.singletonList("*"));
                config.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
                config.setMaxAge(3600L);
                return config;
            }
        };
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(MSanteAuthenticationProvider authProvider) {
        return new ProviderManager(authProvider);
    }
}