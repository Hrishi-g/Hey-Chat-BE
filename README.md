# 🚀 HeyChat Engine — Backend API & Microservices

A high-performance, real-time backend engine built with **Java 21**, **Spring Boot 3**, **Spring Security (JWT + HttpOnly Cookies)**, **Apache Kafka**, **Redis**, **WebSockets**, and **Cloudflare R2**.

---

## ✨ System Architecture & Key Features

- 🔐 **Secure Authentication**: Mobile number & password login with OTP email verification. Uses **JWT** tokens transmitted via secure `HttpOnly` cookies (with `SameSite` & `Partitioned` support) and `Authorization: Bearer` headers.
- ⚡ **Real-Time WebSockets**: Custom WebSocket handler supporting ping/pong heartbeats, live online/offline user status, read receipts, and one-time ticket security.
- 📩 **Kafka Event Pipeline**: Asynchronous OTP distribution, partitioned chat delivery, and Dead Letter Queue (DLQ) error recovery.
- ⚡ **Redis High-Speed Caching**: Low-latency Redis cache for user profiles (`@Cacheable`), active OTP session TTLs, and transient connection states.
- ☁️ **Cloudflare R2 Media Storage**: S3-compatible cloud storage for user avatar uploads and profile image hosting.
- 🛠️ **RESTful API**: Structured endpoints for authentication, profile updates, contact lookup, and chat history retrieval.

---

## 🛠️ Tech Stack

- **Language & Runtime**: Java 21 (JDK 21)
- **Framework**: Spring Boot 3.x
- **Security**: Spring Security 6 (Stateless JWT Filter, CORS, HttpOnly Cookie)
- **Database & ORM**: Spring Data JPA / Hibernate (PostgreSQL / MySQL)
- **Caching**: Spring Data Redis (Lettuce Pool)
- **Event Streaming**: Apache Kafka (Aiven SASL_SSL / SSL Certificate support)
- **Cloud Storage**: Cloudflare R2 / AWS S3 SDK
- **Build Tool**: Apache Maven (`./mvnw`)

---

## 📦 Project Structure

```text
chatApp_Be/
├── src/main/java/com/app/chatApp/
│   ├── config/              # Security, CORS, Cookie, Redis, WebSocket, R2 configs
│   ├── controller/          # REST Endpoints (Auth, User, Image, OneTimeTicket)
│   ├── dto/                 # Data Transfer Objects & Payloads
│   ├── exception/           # Global exception handling
│   ├── handlers/            # WebSocket Chat & Utility Handlers
│   ├── kafka/               # Kafka Producers, Consumers & Topic configs
│   ├── repository/          # JPA Repositories
│   ├── security/            # JwtFilter, JwtUtil & Authentication mechanisms
│   ├── service/             # Business Logic (Auth, User, Email, OneTimeTicket)
│   └── vo/                  # JPA Entities & Enums
├── src/main/resources/
│   ├── application.yaml     # Application configuration & profiles
│   └── certs/               # SSL Truststore Certificates (for Aiven Kafka)
├── Dockerfile               # Production Docker container image definition
├── docker-compose.yml       # Local development stack (Redis, Postgres, Kafka)
├── pom.xml                  # Maven dependencies & build plugins
└── mvnw                     # Maven Wrapper
```

---

## 🚀 Getting Started

### Prerequisites

- **Java Development Kit (JDK)**: Version 21
- **Maven**: Version 3.8+ (or use included `./mvnw`)
- **Redis & Database**: Redis and PostgreSQL/MySQL server (or Docker)

### Installation & Local Run

1. **Clone the repository:**
   ```bash
   git clone https://github.com/your-username/chatApp_Be.git
   cd chatApp_Be
   ```

2. **Configure Environment Variables:**
   Create a `.env` file (or set variables in your IDE):
   ```env
   DATASOURCE_URL=jdbc:postgresql://localhost:5432/chatapp
   DATASOURCE_USERNAME=postgres
   DATASOURCE_PASS=yourpassword
   DATASOURCE_DRIVER_CLASSNAME=org.postgresql.Driver
   JPA_HIBERNATE_SHOW_SQL=false
   JPA_HIBERNATE_GENERATE_DDL=true
   JPA_HIBERNATE_DDL_AUTO=update
   JPA_HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect

   EMAIL_HOST=smtp.gmail.com
   EMAIL_PORT=587
   EMAIL_USERNAME=your-email@gmail.com
   EMAIL_PASSWORD=your-app-password
   EMAIL_FROM_USERNAME=your-email@gmail.com

   SPRING_DATA_REDIS_URL=redis://localhost:6379
   SPRING_DATA_REDIS_SSL=false

   SPRING_KAFKA_AIVEN_SERVICE_URI=localhost:9092
   SPRING_KAFKA_USER=avnadmin
   SPRING_KAFKA_PASSWORD=yourkafkapassword

   JWT_SECRET=your_super_secret_jwt_key_here_32_chars_long
   MSG_ENCRYPTION_SECRET=your_msg_secret

   ALLOWED_CORS_ORIGINS=http://localhost:5173,http://localhost:4000
   COOKIE_SECURE=false
   ```

3. **Build the project:**
   ```bash
   ./mvnw clean compile -DskipTests
   ```

4. **Run the application:**
   ```bash
   ./mvnw spring-boot:run
   ```
   The backend server will start at `http://localhost:8080`.

---

## 🔌 API Endpoints Summary

### Authentication (`/auth`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/auth/signup` | Send OTP for signup verification |
| `POST` | `/auth/verify-signup` | Verify signup OTP & register user |
| `POST` | `/auth/login` | Send OTP for login authentication |
| `POST` | `/auth/verify-otp` | Verify login OTP & issue `jwt` HttpOnly cookie |
| `POST` | `/auth/logout` | Clear `jwt` cookie & destroy session |

### User & Messaging (`/user`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/user/profile` | Retrieve current authenticated user profile |
| `POST` | `/user/profile/update` | Update profile information |
| `GET` | `/user/allHomeChats` | Fetch recent home screen conversations |
| `POST` | `/user/chats` | Retrieve chat message history between users |
| `POST` | `/user/getNewUser` | Search contact by mobile number |

### WebSockets & Security (`/secure`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/secure/ws-ticket` | Generate a one-time WebSocket access ticket |
| `WS` | `/chat?ticket={ticket}` | Establish WebSocket connection for real-time messaging |

---

## 🌐 Production Deployment Guide

When deploying to production platforms (**Render**, **AWS**, **Railway**, **Heroku**, **Docker**):

1. Set `COOKIE_SECURE=true` (enables `SameSite=None; Secure; Partitioned` cookies over HTTPS).
2. Set `ALLOWED_CORS_ORIGINS=https://your-frontend-domain.com`.
3. Configure `SPRING_KAFKA_AIVEN_SERVICE_URI`, Redis URL, and PostgreSQL credentials.

---

## 📄 License

This project is licensed under the MIT License.
