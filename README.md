
# 📦 Online Marketplace API

This is a **Spring Boot** project for an online marketplace, providing a REST API to manage:

- Users (Admin, Seller, Shopper)
- Stores
- Products
- Orders
- Categories
- Reviews

---

## 🚀 Features

- **JWT Authentication** and **Authorization**
- **Role-Based Access Control** (`ADMIN`, `SELLER`, `SHOPPER`)
- **Email Verification** on registration
- **Swagger UI** for API documentation
- **Asynchronous Order Processing** using **ActiveMQ** (or RabbitMQ, configurable)
- **Secure Password Hashing** with BCrypt
- **Order Reviews** (only after purchase)
- **Swagger/OpenAPI 3.0 Specification**
- **Clean layered architecture** (Controller ➔ Service ➔ Repository ➔ Model)

---

## Tech Stack

| Tech | Description |
|:----|:------------|
| Java 23 | Core backend language |
| Spring Boot 3.x | Backend framework |
| Spring Security 6.x | Authentication and authorization |
| Spring Data JPA | ORM and database access |
| PostgreSQL | Database |
| ActiveMQ | Asynchronous order queue |
| Swagger / OpenAPI | API documentation |
| JavaMailSenger | Email sending for verification |
| Maven | Build tool |

---

## 🛠 Setup Instructions

### 1. Clone the repository

```bash
git clone https://github.com/your-username/online-marketplace-api.git
cd online-marketplace-api
```

---

### 2. Configure `application.properties`

Update the following settings inside `src/main/resources/application.properties`:

```properties
# Database
spring.datasource.url=jdbc:postgresql://dpg-cvs3nmmuk2gs739lfj00-a/oma_db
spring.datasource.username=oma_db_user
spring.datasource.password=my_pwd
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=update

# Mail
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# ActiveMQ
spring.activemq.broker-url=tcp://localhost:61616
spring.activemq.user=admin
spring.activemq.password=admin
```

---

### 3. Build and Run

```bash
./mvnw clean install
./mvnw spring-boot:run
```

Backend will be available at:

```
http://localhost:8080
```

Swagger UI:

```
http://localhost:8080/swagger-ui/index.html
```

---

## API Overview

| Method | URL | Description |
|:------|:----|:------------|
| POST | `/auth` | User login |
| GET  | `/auth` | Get current user |
| POST | `/users` | User registration |
| GET  | `/products` | Browse products |
| POST | `/products` | Create new product (seller only) |
| POST | `/orders` | Create new order (shopper only) |
| POST | `/reviews/{productId}` | Submit a review |
| ... | ... | Full list available in Swagger UI |

---

## Docker (optional)

Create a `Dockerfile`:

```dockerfile
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim
COPY --from=build /target/OnlineMarketplaceAPI-0.0.1-SNAPSHOT.jar OnlineMarketplaceAPI.jar
COPY --from=build .env .
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "OnlineMarketplaceAPI.jar"]
```

Build and run:

```bash
docker build -t online-marketplace-api .
docker run -p 8080:8080 online-marketplace-api
```

---

## Author

- Neo Joram (Me!)
- Backend Engineer
