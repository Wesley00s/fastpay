# FastPay - Core Banking and Pix Emulation API

FastPay is a robust, high-performance REST API built to simulate a digital wallet ecosystem and the Brazilian instant payment system (Pix). The project is engineered with Clean Architecture principles, ensuring a decoupled domain, high testability, and scalability. It leverages asynchronous messaging to process settlements reliably without blocking the main HTTP threads.



[Image of Clean Architecture diagram]


## Technologies Used

* **Java 21**: Latest LTS features and optimizations.
* **Spring Boot 3**: Framework for the REST API, Security, and Dependency Injection.
* **Spring Security & JWT**: Stateless authentication secured with asymmetric RSA cryptography.
* **Apache Kafka**: Event-driven architecture for asynchronous transaction settlement.
* **PostgreSQL**: Relational database for transactional integrity.
* **Flyway**: Database schema migration and version control.
* **SpringDoc OpenAPI**: Interactive API documentation (Swagger UI).
* **JUnit 5 & Mockito**: Comprehensive unit testing for the application layer.
* **Docker & Docker Compose**: Containerization for seamless infrastructure setup.
* **GitHub Actions**: Continuous Integration (CI) pipeline for automated testing and builds.

## Key Features

* **User & Account Provisioning**: Secure sign-up process that automatically provisions a default banking account for the new user.
* **Stateless Authentication**: Login endpoint that issues an RSA-signed JWT. The API protects against IDOR (Insecure Direct Object Reference) by extracting the user identity directly from the token context.
* **Cash-In Operations**: Endpoints to deposit funds into the user's account.
* **Pix Key Management**: Registration and resolution of Pix alias keys (Email, Phone, CPF, Random).
* **Asynchronous Transfers**: Pix transfers are initiated synchronously but settled asynchronously via Kafka topics, preventing race conditions and improving throughput.
* **Account Statement**: Paginated retrieval of the user's transaction history.
* **Standardized Error Handling**: Global exception handling compliant with RFC 7807 (Problem Details for HTTP APIs).

## Architecture Overview

The application follows the Ports and Adapters (Hexagonal) architecture. The codebase is divided into:

1. **Domain**: Contains the core business models (`Account`, `User`, `Transaction`, `PixKey`) and custom exceptions. It has no dependencies on external frameworks.
2. **Application**: Contains the Use Cases (`SendPixService`, `AccountOperationService`, etc.) that orchestrate the business logic using interfaces (Ports).
3. **Infrastructure (Adapters)**:
    * **Persistence**: PostgreSQL adapters utilizing Spring Data JPA.
    * **Messaging**: Kafka producers and consumers for settlement events.
    * **Security**: JWT generation and validation filters using RSA.
4. **Presentation**: REST Controllers that expose the application's capabilities, mapped with DTOs.

## How to Run

### Prerequisites
* Java 21 installed.
* Docker and Docker Compose installed.
* Gradle (Wrapper included in the project).

### 1. Environment Configuration
The application relies on environment variables for database and messaging configurations. Copy the example file and adjust the values if necessary:

```bash
cp .env.example .env
```

### 2. Start the Infrastructure
Navigate to the project root directory and start the PostgreSQL database and Apache Kafka broker:

```bash
docker-compose up -d
```

### 3. Configure RSA Keys (Development)
The application requires an RSA key pair for signing and verifying JWTs. For local development, generate them using OpenSSL:

```bash
mkdir -p src/main/resources/certs
openssl genrsa -out src/main/resources/certs/private.pem 2048
openssl rsa -in src/main/resources/certs/private.pem -pubout -out src/main/resources/certs/public.pem
```

### 4. Run the Application
Execute the Spring Boot application using the Gradle wrapper:

```bash
./gradlew bootRun
```

## API Documentation

Once the application is running, you can interact with the endpoints through the Swagger UI:

* **Swagger UI**: `http://localhost:8080/swagger-ui.html`
* **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

## Continuous Integration and Testing

This project uses GitHub Actions to enforce code quality. Every push or pull request to the `main` branch triggers a workflow that compiles the code and runs the unit test suite.

To run the unit tests locally:

```bash
./gradlew test
```