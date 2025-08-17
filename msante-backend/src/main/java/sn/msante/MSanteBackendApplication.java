package sn.msante;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * M-Santé Backend Application
 * 
 * Healthcare platform backend for Senegal and West Africa
 * providing appointment booking, payment processing, and secure messaging
 * for patients and healthcare professionals.
 * 
 * Features:
 * - Multi-tenant SaaS architecture
 * - JWT-based authentication with OTP
 * - Role-based access control (patient, practitioner, admin, superadmin)
 * - Mobile Money payment integration
 * - GDPR/CDP compliance
 */
@SpringBootApplication
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true, securedEnabled = true, prePostEnabled = true)
@EnableJpaRepositories
@EnableJpaAuditing
@EnableTransactionManagement
@EnableCaching
@EnableAsync
public class MSanteBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MSanteBackendApplication.class, args);
    }
}