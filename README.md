Perfect 👍
Below is a **fully polished, clean, professional README**.
You can **directly copy and paste this into `README.md`** on GitHub.

---

# 🚀 Zest Product Backend API

A production-ready RESTful API built using **Java 17 + Spring Boot** that implements full CRUD operations on Products with JWT authentication, refresh token rotation, pagination, validation, testing, and Docker support.

---

## 📌 Overview

This project demonstrates:

* Clean layered architecture
* RESTful API design best practices
* JWT-based stateless authentication
* Refresh token mechanism
* Role-based authorization
* Pagination & sorting
* Global exception handling
* Unit & integration testing
* Dockerized deployment

---

## 🛠 Tech Stack

* Java 17+
* Spring Boot
* Spring Data JPA (Hibernate)
* MySQL / PostgreSQL
* Spring Security (JWT + Refresh Token)
* BCrypt Password Encoder
* Swagger / OpenAPI
* JUnit 5 & Mockito
* H2 Database (Testing)
* Docker & Docker Compose

---

## 🏗 Architecture

The project follows a clean layered architecture:

```
Controller → Service → Repository → Database
```

### Layers

* **Controller** → REST endpoints
* **Service** → Business logic
* **Repository** → Data access layer
* **Security** → JWT authentication & authorization
* **DTO** → Request/Response separation
* **Global Exception Handler** → Standardized API responses

---

## 🔐 Security Implementation

* Stateless JWT authentication
* Access Token (short-lived)
* Refresh Token (long-lived)
* Refresh token rotation
* Role-based access control (`ROLE_USER`, `ROLE_ADMIN`)
* BCrypt password hashing
* CORS configuration
* Input validation

### 🔑 Authentication Flow

1. User registers or logs in
2. Server returns:

   * Access Token
   * Refresh Token
3. Client sends:

```
Authorization: Bearer <access_token>
```

4. When access token expires, client uses refresh token to get new access token.

---

## 📡 API Endpoints

### 🔓 Authentication (Public)

| Method | Endpoint                |
| ------ | ----------------------- |
| POST   | `/api/v1/auth/register` |
| POST   | `/api/v1/auth/login`    |
| POST   | `/api/v1/auth/refresh`  |

---

### 🔐 Products (Protected)

| Method | Endpoint                      |
| ------ | ----------------------------- |
| GET    | `/api/v1/products`            |
| GET    | `/api/v1/products/{id}`       |
| POST   | `/api/v1/products`            |
| PUT    | `/api/v1/products/{id}`       |
| DELETE | `/api/v1/products/{id}`       |
| GET    | `/api/v1/products/{id}/items` |

### Features

* Pagination support
* Sorting support
* Input validation
* Role-based protection

---

## 🗄 Database Schema

### Product Table

```sql
CREATE TABLE product (
 id INT PRIMARY KEY AUTO_INCREMENT,
 product_name VARCHAR(255) NOT NULL,
 created_by VARCHAR(100) NOT NULL,
 created_on TIMESTAMP NOT NULL,
 modified_by VARCHAR(100),
 modified_on TIMESTAMP
);
```

### Item Table

```sql
CREATE TABLE item (
 id INT PRIMARY KEY AUTO_INCREMENT,
 product_id INT NOT NULL,
 quantity INT NOT NULL,
 FOREIGN KEY (product_id) REFERENCES product(id)
);
```

Indexes:

* product_name
* product_id

---

## 📑 Swagger API Documentation

Access Swagger UI at:

```
http://localhost:8080/swagger-ui.html
```

---

## 🧪 Testing

### Unit Testing

* JUnit 5
* Mockito
* Service layer testing

### Integration Testing

* Spring Boot Test
* H2 in-memory database

Run tests:

```
mvn test
```

---

## 🐳 Docker Setup

### Build and Run

```
docker-compose up --build
```

Services:

* Spring Boot Application
* MySQL Database
* Persistent Docker volumes

---

## ⚙️ Run Locally (Without Docker)

### 1️⃣ Clone Repository

```
git clone https://github.com/Deepanshu-72/Zest-Product_Backend.git
cd Zest-Product_Backend
```

### 2️⃣ Configure Database

Update `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/productdb
    username: root
    password: password
```

### 3️⃣ Run Application

```
mvn spring-boot:run
```

Application runs at:

```
http://localhost:8080
```

---

## ✅ Features Implemented

✔ Full CRUD operations
✔ JWT authentication
✔ Refresh token rotation
✔ Role-based authorization
✔ Global exception handling
✔ Input validation
✔ Pagination & sorting
✔ Swagger documentation
✔ Docker support
✔ Unit & integration tests

---

## 📊 Project Highlights

* Clean architecture
* Production-ready security implementation
* REST best practices
* Scalable design
* Assignment requirement fully covered

---

## 👨‍💻 Author

Deepanshu Bariya

---


