# Ecommerce Backend (Spring Boot)

A REST API backend for an ecommerce application with JWT authentication, product/category catalog, shopping cart, and order management.

## Tech Stack
- Java 17
- Spring Boot 3.2.5 (Web, Data JPA, Security, Validation)
- MySQL (default) / H2 (dev profile, no setup needed)
- JWT (jjwt 0.11.5)
- Lombok
- Maven

## Project Structure
```
src/main/java/com/ecommerce/backend/
├── config/          -> SecurityConfig (JWT + role-based rules, CORS)
├── security/        -> JwtUtil, JwtAuthFilter, UserDetailsServiceImpl
├── entity/          -> User, Category, Product, Cart, CartItem, Order, OrderItem
├── repository/      -> Spring Data JPA repositories
├── dto/             -> Request/response DTOs
├── service/         -> Business logic (Auth, Category, Product, Cart, Order)
├── controller/      -> REST controllers
└── exception/       -> Custom exceptions + global exception handler
```

## Running the project

### Option 1 — Quick start with H2 (no DB setup required)
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```
H2 console available at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:ecommerce_db`).

### Option 2 — MySQL (production-like)
1. Create a MySQL database (or let it auto-create): update credentials in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce_db?createDatabaseIfNotExist=true
   spring.datasource.username=root
   spring.datasource.password=root
   ```
2. Run:
   ```bash
   mvn spring-boot:run
   ```

The API starts on `http://localhost:8080`.

### Building a jar
```bash
mvn clean package
java -jar target/ecommerce-backend-1.0.0.jar
```

## Authentication
JWT-based. Register or login to get a token, then send it as:
```
Authorization: Bearer <token>
```
New users are created with role `CUSTOMER`. To create an `ADMIN` user, register normally then manually update the `role` column to `ADMIN` in the database (or add a seeding mechanism of your choice).

## API Endpoints

### Auth (public)
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a new user `{name, email, password}` |
| POST | `/api/auth/login` | Login `{email, password}` -> returns JWT |

### Categories
| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/api/categories` | Public | List all categories |
| GET | `/api/categories/{id}` | Public | Get category by id |
| POST | `/api/categories` | ADMIN | Create category `{name, description}` |
| PUT | `/api/categories/{id}` | ADMIN | Update category |
| DELETE | `/api/categories/{id}` | ADMIN | Delete category |

### Products
| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/api/products?page=0&size=10` | Public | Paginated product list |
| GET | `/api/products?categoryId=1` | Public | Filter by category |
| GET | `/api/products?search=shirt` | Public | Search by name |
| GET | `/api/products/{id}` | Public | Get product by id |
| POST | `/api/products` | ADMIN | Create product |
| PUT | `/api/products/{id}` | ADMIN | Update product |
| DELETE | `/api/products/{id}` | ADMIN | Delete product |

Product create/update body:
```json
{
  "name": "Wireless Mouse",
  "description": "Ergonomic wireless mouse",
  "price": 19.99,
  "stockQuantity": 100,
  "imageUrl": "https://...",
  "categoryId": 1
}
```

### Cart (requires authentication)
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/cart` | Get current user's cart |
| POST | `/api/cart/items` | Add item `{productId, quantity}` |
| PUT | `/api/cart/items/{itemId}` | Update quantity `{quantity}` (0 removes item) |
| DELETE | `/api/cart/items/{itemId}` | Remove item |

### Orders (requires authentication)
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/orders` | Place order from current cart `{shippingAddress}` |
| GET | `/api/orders` | List current user's orders |
| GET | `/api/orders/{id}` | Get order by id (owner or admin) |
| POST | `/api/orders/{id}/cancel` | Cancel an order (restocks items) |

### Admin (requires ADMIN role)
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/admin/orders` | List all orders |
| PUT | `/api/admin/orders/{id}/status` | Update order status `{status: "SHIPPED"}` |

## Example flow (curl)
```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com","password":"secret123"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","password":"secret123"}'
# -> copy the "token" field from the response

TOKEN="paste-token-here"

# Add product to cart
curl -X POST http://localhost:8080/api/cart/items \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"productId":1,"quantity":2}'

# Place order
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"shippingAddress":"123 Main St, Springfield"}'
```

## Notes / Next steps you may want to add
- Email verification / password reset flow
- Payment gateway integration (Stripe/Razorpay)
- Product image upload (S3 or local storage) instead of `imageUrl` string
- Reviews & ratings
- Wishlist
- Swagger/OpenAPI docs (add `springdoc-openapi-starter-webmvc-ui` dependency)
- Refresh tokens (currently a single JWT with fixed expiry)
- Global rate limiting
