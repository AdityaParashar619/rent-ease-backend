# RentEase Enterprise Backend

> **Production-Grade Java Full Stack Rental Platform Backend**  
> Built with **Java 17**, **Spring Boot 3.3.3**, **Spring Security 6 (Stateless JWT)**, **Spring Data JPA / Hibernate**, **Flyway**, and **PostgreSQL 16**.

---

## 🏛️ Architecture Overview

RentEase provides a multi-category rental engine supporting:
- 🏡 **Residential Homes & PGs** (BHKs, furnishing, gated society status, pet friendly, maintenance)
- 🚗 **Self-Drive Vehicles** (Cars, bikes, transmission, fuel type, daily km limits)
- 🏢 **Commercial Workspaces** (Managed offices, retail shops, coworking hot-desks)
- ✨ **Event Venues** (Marriage gardens, banquet pavilions, capacity, curfews)

### Package Structure
```
com.rentease
├── RentEaseApplication.java
├── config/              # CORS, Security, OpenAPI
├── controller/          # REST Controllers (/api/v1/*)
├── dto/                 # Request & Response Contracts, ApiResponse<T>
├── entity/              # JPA Relational Entities
├── enums/               # Domain Enums (Roles, Statuses, OwnerTypes)
├── exception/           # Global Exception Handler & Custom Errors
├── repository/          # Spring Data JPA Repositories
├── security/            # JWT Token Provider, Filter, UserDetailsService
├── service/             # Business Logic & PaymentGateway abstraction
└── specification/       # JPA Criteria Builder for dynamic multi-filter search
```

---

## 🚀 Getting Started

### Prerequisites
- **Java 17+ (Temurin / OpenJDK)**
- **Maven 3.8+**
- **Docker & Docker Compose** (Optional, for instant PostgreSQL container)

### Step 1: Start PostgreSQL
You can quickly boot PostgreSQL via the project root's docker-compose:
```bash
docker compose up -d postgres
```
The development profile connects to `jdbc:postgresql://localhost:5433/rentease_db`. Set `DB_PASSWORD` before starting Postgres; the container password and application connection use that same variable.

### Step 2: Run the Spring Boot Application
From the `backend` directory:
```bash
cd backend
mvn clean spring-boot:run
```
Flyway automatically applies the schema and reference-data migrations, including supported city and locality records.

---

## 📖 Interactive OpenAPI & Swagger UI

Once started, interactive API documentation is available at:
- **Swagger UI**: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:8081/api-docs](http://localhost:8081/api-docs)

---

## Account setup

The migrations seed roles and reference data, but do not create demo users. Register a customer/provider account using `/api/v1/auth/register`. Administrative accounts must be provisioned through a trusted operator process; public registration cannot grant admin privileges.

---

## 📡 REST API Summary (`/api/v1/*`)

### Authentication
- `POST /api/v1/auth/login`: Authenticate and obtain JWT Bearer token
- `POST /api/v1/auth/register`: Register as Customer or Provider

### Inventory & Search
- `GET /api/v1/listings`: Search & filter across categories (params: `category`, `locality`, `minPrice`, `maxPrice`, `ownerType`, `verifiedOnly`, `keyword`)
- `GET /api/v1/listings/{id}`: Detailed specifications of a rental
- `GET /api/v1/listings/featured`: Active, verified featured listings
- `POST /api/v1/listings`: Publish a new rental (Provider/Broker only)
- `GET /api/v1/listings/my`: Current provider's listings
- `GET /api/v1/admin/listings/pending`: Listings waiting for admin review
- `POST /api/v1/admin/listings/{id}/review`: Approve or reject a listing

### Bookings & Concurrency Slot Locking
- `POST /api/v1/bookings`: Reserve rental with conflict validation (409 Conflict if dates overlap)
- `GET /api/v1/bookings/my`: Customer's booking history
- `POST /api/v1/bookings/{id}/cancel`: Cancel booking reservation

### Administration & Compliance
- `GET /api/v1/admin/dashboard`: Platform KPIs (active inventory, pending verifications, total users)
- `POST /api/v1/admin/verifications/{id}/review`: Approve or reject owner identity or property title deeds

---

## 🐳 Docker database
The Compose file starts PostgreSQL only. Run the Spring Boot application separately:
```bash
docker compose up -d postgres
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```
The API listens at `http://localhost:8081`. Configure a strong `JWT_SECRET` and `DB_PASSWORD` in the environment before starting the application.
