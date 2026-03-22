<div align="center">
  <h1>🏦 SecureBank API</h1>
  <p><i>A production-quality Spring Boot Banking REST API built with Java 17 and Spring Boot 3.2.3.</i></p>
  
  [![Java Support](https://img.shields.io/badge/Java-17+-ED8B00?logo=openjdk&logoColor=white)](#)
  [![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.3-6DB33F?style=flat&logo=spring&logoColor=white)](#)
  [![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-336791?style=flat&logo=postgresql&logoColor=white)](#)
  [![Docker Ready](https://img.shields.io/badge/Docker-Ready-2496ED?style=flat&logo=docker&logoColor=white)](#)
  [![CI Status](https://img.shields.io/github/actions/workflow/status/owesomejus17/student-performance-app/maven.yml?branch=main&label=Build%20Status&logo=githubactions)](#)
</div>

<hr />

## 🎯 Overview

SecureBank API is a robust backend service designed to handle core banking operations. Built with enterprise-grade standards, it incorporates strong security, efficient database handling, and clear architectural boundaries to deliver reliable financial transactions.

## ✨ Key Features

- **🔐 Robust Security:** User Authentication & Authorization mechanisms powered by Spring Security and JWT.
- **👤 Account Management:** Comprehensive secure endpoints for generating and managing bank accounts.
- **💸 Transaction Processing:** Reliable real-time transaction processing rules and services.
- **🗄️ Scalable Data Layer:** Seamless integration with PostgreSQL and Spring Data JPA.
- **📚 Rich API Documentation:** Auto-generated interactive API docs using OpenAPI (Swagger UI).
- **🐳 Containerized Delivery:** Docker-ready multi-stage builds.
- **🚀 Automated CI/CD:** GitHub Actions workflows configured for continuous integration.

---

## 🛠️ Tech Stack

| Category         | Technology                           |
|------------------|--------------------------------------|
| **Core**         | Java 17, Spring Boot 3.2.3           |
| **Database**     | PostgreSQL, Hibernate, Spring Data   |
| **Security**     | Spring Security, JWT (io.jsonwebtoken)|
| **Documentation**| Springdoc OpenAPI v2.3.0             |
| **Build & CI**   | Maven, Docker, GitHub Actions        |

---

## 🚦 Getting Started

### 📋 Prerequisites

Before you begin, ensure you have the following installed:
- [Java 17](https://adoptium.net/) or higher
- [Maven 3.8+](https://maven.apache.org/)
- [PostgreSQL 15+](https://www.postgresql.org/)

### 🗄️ 1. Database Configuration

Make sure PostgreSQL is running locally. You must create a local database named `securebankdb` before running the application. The schema and tables will be generated or updated automatically on startup.

Update your local credentials in `src/main/resources/application.properties` if they differ:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/securebankdb
spring.datasource.username=postgres
spring.datasource.password=12345678
```

### 💻 2. Build and Run locally

Open a terminal and navigate to the project directory:

**Build the application:**
```bash
mvn clean package
```

**Run the application:**
```bash
java -jar target/securebank-api-1.0.0.jar
```
*(Alternatively, you can run it directly with Maven: `mvn spring-boot:run`)*

🚀 The API will start and listen on `http://localhost:8080`.

---

## 📖 API Documentation

Once the server is running, you can explore, test, and interact with all the API endpoints directly from your browser via Swagger UI!

**👉 [Interactive API Docs (Swagger UI)](http://localhost:8080/swagger-ui.html)**

---

## 🐳 Docker Deployment

The application is fully containerized using a multi-stage `Dockerfile` to optimize build performance and image size.

**1. Build the Docker Image:**
```bash
docker build -t securebank-api .
```

**2. Run the Container:**
```bash
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://<YOUR_DB_HOST>:5432/securebankdb \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=12345678 \
  securebank-api
```
*(Note: Replace `<YOUR_DB_HOST>` with `host.docker.internal` if connecting to a PostgreSQL database on your native host machine.)*

---

## ⚙️ CI/CD Pipeline

This project includes an automated GitHub Actions CI pipeline (`.github/workflows/maven.yml`). 
- **Triggered on:** Push and Pull Requests to `main` and `master`.
- **Flow:** It automatically checks out the code, sets up JDK 17, launches a PostgreSQL service container, and executes `mvn package` to ensure the build stays green and all tests pass.

---

<div align="center">
  <sub>Built with ❤️ by the open-source community.</sub>
</div>
"# banking-backend-api" 
