# 💊 Supplement Distributor API

A REST API for managing a gym supplement distribution company, including products, stock, orders, shipments and reports.

---

## 🛠️ Tech Stack

- **Java 17**
- **Spring Boot 3.4.1**
- **Spring Security + JWT**
- **Spring Data JPA + Hibernate**
- **PostgreSQL 16**
- **Redis 7**
- **Lombok**
- **Swagger / OpenAPI**
- **Docker + Docker Compose**
- **Maven**

---

## ⚙️ Configuration

### application.properties

```properties
# App
spring.application.name=SupplementDistributor
server.port=8080

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/supplements_db?TimeZone=UTC
spring.datasource.username=supplements_user
spring.datasource.password=supplements_pass
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.jdbc.time_zone=UTC

# JWT
jwt.secret=your_secret_key
jwt.expiration=86400000

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379

# Swagger
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/v3/api-docs
```

---

## 🚀 Running the Project

### Prerequisites
- Java 17
- Maven
- Docker Desktop

### Steps

```bash
# 1. Clone the repository
git clone https://github.com/alejoalbornoz/SupplementDistributor.git

# 2. Start the containers (PostgreSQL, pgAdmin, Redis)
docker-compose up -d

# 3. Run the application
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`

### Stop containers

```bash
# Stop but keep data
docker-compose stop

# Stop and remove containers
docker-compose down

# Stop and remove everything including data
docker-compose down -v
```

---

## 🗄️ Database Setup

After running the app, insert the first admin user directly in the database. The password must be encrypted with BCrypt (use [bcrypt-generator.com](https://bcrypt-generator.com) with 10 rounds):

```sql
INSERT INTO users (first_name, last_name, email, password, phone, role, active, created_at, updated_at)
VALUES (
    'Admin',
    'User',
    'admin@supplements.com',
    '$2a$10$your_bcrypt_hashed_password',
    '1234567890',
    'ADMIN',
    true,
    NOW(),
    NOW()
);
```

Access pgAdmin at `http://localhost:5050`
Email:    admin@supplements.com
Password: admin123

Connect to the server with:
Host:     postgres
Port:     5432
Database: supplements_db
Username: supplements_user
Password: supplements_pass

---

## 🔐 Authentication

The API uses **JWT (JSON Web Tokens)** for authentication. Tokens expire after **24 hours**.

On logout, the token is stored in **Redis** with a TTL equal to its remaining expiration time and automatically removed when it expires. Any request using a blacklisted token will receive a `401 Unauthorized` response.

### Roles

| Role | Description |
|------|-------------|
| `ADMIN` | Full access: manage users, products, stock, orders, shipments and reports |
| `CLIENT` | Limited access: browse products, create and view own orders |

### Login
POST /api/auth/login
Content-Type: application/json
{
"email": "admin@supplements.com",
"password": "yourpassword"
}

Response:

```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "type": "Bearer",
    "user": {
        "id": 1,
        "firstName": "Admin",
        "lastName": "User",
        "email": "admin@supplements.com",
        "phone": "1234567890",
        "role": "ADMIN"
    }
}
```

Use the token in every subsequent request:
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

### Logout
POST /api/auth/logout
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

---

## 📋 API Endpoints

### Auth

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| `POST` | `/api/auth/login` | Public | Login and get JWT token |
| `POST` | `/api/auth/logout` | Authenticated | Invalidate JWT token |

### Users (Admin only)

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/users` | Get all users |
| `GET` | `/api/users/{id}` | Get user by ID |
| `POST` | `/api/users` | Create user |

#### Create User Request Body

```json
{
    "firstName": "Juan",
    "lastName": "Pérez",
    "email": "juan@gmail.com",
    "password": "securepassword",
    "phone": "1234567890",
    "role": "CLIENT"
}
```

### Categories

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| `GET` | `/api/categories` | Public | Get all categories |
| `GET` | `/api/categories/{id}` | Public | Get category by ID |
| `POST` | `/api/categories` | Admin | Create category |

#### Create Category Request Body

```json
{
    "name": "Proteína",
    "description": "Suplementos proteicos"
}
```

### Products

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| `GET` | `/api/products` | Public | Get all active products |
| `GET` | `/api/products/{id}` | Public | Get product by ID |
| `POST` | `/api/products` | Admin | Create product |
| `PATCH` | `/api/products/{id}` | Admin | Update product |
| `DELETE` | `/api/products/{id}` | Admin | Soft delete product |

#### Create Product Request Body

```json
{
    "name": "Whey Protein",
    "brand": "Optimum Nutrition",
    "description": "100% Whey Gold Standard",
    "price": 5000.00,
    "stock": 50,
    "categoryId": 1
}
```

> **Note:** Deleting a product performs a **soft delete** — the product is marked as `active = false` and never permanently removed. This preserves historical order data integrity.

### Stock (Admin only)

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/stock/in` | Register stock entry (purchase from supplier) |
| `POST` | `/api/stock/out` | Register stock exit (manual adjustment) |
| `GET` | `/api/stock/history` | Get all stock movements |

#### Stock Movement Request Body

```json
{
    "productId": 1,
    "quantity": 100,
    "reason": "Purchase from supplier"
}
```

### Orders

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| `GET` | `/api/orders` | Admin/Client | Get orders (Admin sees all, Client sees own) |
| `POST` | `/api/orders` | Client | Create order |
| `PATCH` | `/api/orders/{id}/status` | Admin | Update order status |

#### Create Order Request Body

```json
{
    "items": [
        {
            "productId": 1,
            "quantity": 2
        },
        {
            "productId": 3,
            "quantity": 1
        }
    ],
    "notes": "Leave at the door"
}
```

#### Order Status Values

| Status | Description |
|--------|-------------|
| `PENDING` | Order placed, awaiting processing |
| `SHIPPED` | Order shipped |
| `DELIVERED` | Order delivered |
| `CANCELLED` | Order cancelled |

### Shipments

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| `POST` | `/api/shipments` | Admin | Create shipment |
| `GET` | `/api/shipments/order/{orderId}` | Authenticated | Get shipment by order |
| `GET` | `/api/shipments/tracking/{code}` | Authenticated | Track shipment by code |
| `PATCH` | `/api/shipments/{id}/status` | Admin | Update shipment status |

#### Create Shipment Request Body

```json
{
    "orderId": 1,
    "address": "Av. Corrientes 1234, Buenos Aires",
    "trackingCode": "SUPP-2024-00123"
}
```

#### Shipment Status Values

| Status | Description |
|--------|-------------|
| `IN_WAREHOUSE` | Shipment prepared, not yet dispatched |
| `IN_TRANSIT` | Shipment on the way |
| `DELIVERED` | Shipment delivered to customer |

### Reports (Admin only)

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/reports/top-products` | Most sold products |
| `GET` | `/api/reports/billing?start=&end=` | Billing by period |
| `GET` | `/api/reports/low-stock?threshold=10` | Products with low stock |

#### Billing Report Example
GET /api/reports/billing?start=2024-01-01T00:00:00&end=2024-01-31T23:59:59
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

---

## 📖 API Documentation

Swagger UI is available at:
http://localhost:8080/swagger-ui.html

---

## 📬 Postman Setup

To test the API with Postman efficiently:

1. Create an environment called `SupplementAPI`
2. Add a **Post-response script** to the Login request:

```javascript
const response = pm.response.json();
pm.environment.set("token", response.token);
```

3. Use `Bearer {{token}}` in the Authorization header of all protected requests — the token updates automatically after every login.

---

## 🧠 Design Decisions

**Soft delete on products** — Products are never deleted from the database. Instead they are marked as `active = false`. This preserves historical order data integrity since orders reference products.

**Stock separated from products** — Stock movements are tracked in a dedicated `StockMovement` table with `IN/OUT` types, providing a full audit trail of inventory changes.

**Price snapshot on order items** — `OrderItem` stores the unit price at the time of purchase. This ensures historical orders are not affected by future price changes on products.

**Redis token blacklist** — On logout, JWT tokens are stored in Redis with a TTL equal to its remaining expiration time. Redis automatically removes expired entries keeping the blacklist clean with no manual maintenance required. This approach avoids unnecessary database queries while still preventing token reuse after logout.

**Interface + Implementation pattern on services** — Every service is defined as an interface (`IUserService`) and implemented separately (`UserService`). Controllers always inject the interface, making the implementation swappable without touching the controller layer.

**Centralized mapping** — All entity to DTO conversions are handled by a single `Mapper` class with static methods, avoiding repeated mapping logic across services.

**TimeZone configuration** — The JDBC connection URL includes `?TimeZone=UTC` to avoid compatibility issues between the Windows system timezone and the PostgreSQL Docker container.

---

## 📄 License

This project is for educational purposes.
