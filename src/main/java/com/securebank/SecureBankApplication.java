package com.securebank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the SecureBank API application.
 *
 * Features:
 *  - JWT-based authentication (Spring Security 6)
 *  - RESTful banking operations: accounts, deposits, withdrawals, transfers
 *  - PostgreSQL persistence via Spring Data JPA
 *  - Swagger UI available at: http://localhost:8080/swagger-ui.html
 */
@SpringBootApplication
public class SecureBankApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecureBankApplication.class, args);
    }
}
