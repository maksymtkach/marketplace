
# Simple Marketplace Web Application
Spring Boot CRUD + Basic Authentication + CSRF Protection + Role-Based Access Control
Tasks №2, №3, №4, and №5

---

## Description
A simple Spring Boot web application that demonstrates:
- CRUD REST API for managing Products and Orders
- Basic Authentication protection with user roles
- **CSRF (Cross-Site Request Forgery) Protection (Lab 4)**
- **Role-Based Access Control - RBAC (Lab 5)**
- Sample data initialization on startup
- No database (data stored in memory)
- Tested via Postman

---

## Technologies
- Java 17  
- Spring Boot 3.5.6  
- Spring Web  
- Spring Security  
- Jakarta Validation  

---

## Users and Roles

| Username | Password | Role | Description |
|-----------|-----------|------|--------------|
| admin | admin123 | ADMIN | Full access to all endpoints |
| manager | manager123 | MANAGER | Can view all orders and products |
| user | user123 | CUSTOMER | Can create and view own orders |

---

## How to Run

1. Open the project in IntelliJ IDEA  
2. Run in terminal:
   ```bash
   mvn spring-boot:run


3. Application starts on:

   ```
   http://localhost:8080
   ```
4. Use Postman or browser to test requests.

---

## Task №2 — Basic Authorization

All endpoints require Basic Authentication.
Unauthorized requests return:

```json
{
  "status": 401,
  "error": "Unauthorized"
}
```

### Steps to test Basic Auth

1. Open Postman → Authorization → Type: Basic Auth
2. Enter one of the following credentials:

    * admin / admin123
    * manager / manager123
    * user / user123
3. Send requests listed below to check access levels.

---

## Task №3 — CRUD HTTP Requests (10 requests total)

### 1. GET all products

```
GET http://localhost:8080/api/products
```

* Admin: allowed
* Manager: allowed
* User: allowed

### 2. POST new product

```
POST http://localhost:8080/api/products
```

Body:

```json
{
  "name": "Test Product",
  "price": 99.9
}
```

* Admin: allowed
* Manager: allowed
* User: forbidden

### 3. DELETE product by ID

```
DELETE http://localhost:8080/api/products/1
```

* Admin: allowed
* Manager: forbidden
* User: forbidden

### 4. POST create order

```
POST http://localhost:8080/api/orders
```

Body:

```json
{
  "productId": 1,
  "quantity": 2
}
```

* User: allowed
* Admin: allowed
* Manager: allowed

### 5. GET all orders

```
GET http://localhost:8080/api/orders
```

* Admin: sees all orders
* Manager: sees all orders
* User: sees only own orders

### 6. GET order by ID

```
GET http://localhost:8080/api/orders/1
```

* Owner: allowed
* Admin / Manager: allowed
* Other users: forbidden

### 7. DELETE order

```
DELETE http://localhost:8080/api/orders/1
```

* Admin: allowed
* Manager: allowed
* User: forbidden

### 8. Unauthorized request (no auth)

```
GET http://localhost:8080/api/products
```

→ 401 Unauthorized

### 9. Wrong credentials

```
GET http://localhost:8080/api/orders
```

→ 401 Unauthorized

### 10. GET after deletion

```
GET http://localhost:8080/api/orders/1
```

→ 404 Not Found (after delete)

---

## Required screenshots for Moodle

| #  | Endpoint                     | User  | Expected Result  |
| -- | ---------------------------- | ----- | ---------------- |
| 1  | GET /api/products            | admin | 200 OK           |
| 2  | GET /api/products            | user  | 200 OK           |
| 3  | POST /api/products           | admin | 201 Created      |
| 4  | POST /api/products           | user  | 403 Forbidden    |
| 5  | GET /api/orders              | admin | 200 OK           |
| 6  | GET /api/orders              | user  | 200 OK           |
| 7  | DELETE /api/orders/1         | admin | 204 No Content   |
| 8  | DELETE /api/orders/1         | user  | 403 Forbidden    |
| 9  | GET /api/products (no auth)  | -     | 401 Unauthorized |
| 10 | GET /api/orders (wrong pass) | -     | 401 Unauthorized |

---

## Task №4 — CSRF Protection

### What is CSRF?

**CSRF (Cross-Site Request Forgery)** is a type of attack where a malicious website tricks a user's browser into making unauthorized requests to a different website where the user is authenticated.

**Example Attack Scenario:**
1. User logs into `bankapp.com` and gets an authentication cookie
2. User visits `malicious.com` while still logged in to `bankapp.com`
3. `malicious.com` contains hidden code that sends a request to `bankapp.com/transfer?to=attacker&amount=1000`
4. Browser automatically includes the authentication cookie with the request
5. `bankapp.com` processes the request because it looks legitimate
6. Money is transferred to the attacker's account

### How Spring Security Protects Against CSRF

Spring Security implements the **Synchronizer Token Pattern**:

1. **Token Generation**: Server generates a unique, random CSRF token for each session
2. **Token Storage**: Token is stored in:
   - **Cookie** (name: `XSRF-TOKEN`) - readable by JavaScript
   - **Server session** - for validation
3. **Token Requirement**: State-changing requests (POST, PUT, DELETE, PATCH) must include the token
4. **Token Validation**: Server validates the token from the request matches the session token
5. **Protection**: Malicious sites cannot read the token due to Same-Origin Policy

**Safe Methods** (GET, HEAD, OPTIONS, TRACE) don't require CSRF tokens because they should not modify state.

### CSRF Configuration in This Project

Located in `SecurityConfig.java:87-92`:

```java
.csrf(csrf -> csrf
    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
)
```

**Configuration Details:**
- `CookieCsrfTokenRepository`: Stores token in browser cookie
- `withHttpOnlyFalse()`: Allows JavaScript to read the cookie (needed for AJAX/SPA)
- Cookie name: `XSRF-TOKEN` (default)
- Header name: `X-XSRF-TOKEN` (default)
- Parameter name: `_csrf` (default)

### New Endpoints for Lab 4

#### 1. CSRF Token Endpoint

```
GET /csrf-token
```

**Purpose**: Retrieve the current CSRF token for testing

**Authentication**: Not required (public endpoint)

**Response Example**:
```json
{
  "token": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "headerName": "X-XSRF-TOKEN",
  "parameterName": "_csrf"
}
```

**File**: `CsrfController.java`

#### 2. Test Endpoints

All located at `/api/v1/test`:

| Endpoint | Method | Auth Required | CSRF Required | Description |
|----------|--------|---------------|---------------|-------------|
| `/api/v1/test/public` | GET | No | No | Public endpoint for testing |
| `/api/v1/test/protected` | GET | Yes | No | Protected GET (safe method) |
| `/api/v1/test/protected` | POST | Yes | Yes | Protected POST (demonstrates CSRF) |
| `/api/v1/test/protected` | PUT | Yes | Yes | Protected PUT (demonstrates CSRF) |
| `/api/v1/test/protected/{id}` | DELETE | Yes | Yes | Protected DELETE (demonstrates CSRF) |
| `/api/v1/test/admin` | POST | Yes (ADMIN) | Yes | Admin-only endpoint |
| `/api/v1/test/info` | GET | Yes | No | CSRF information endpoint |

**File**: `TestController.java`

### Testing CSRF Protection with Postman

#### Test 1: GET Request Without CSRF Token (Should Work)

**Request:**
```
GET http://localhost:8080/api/v1/test/protected
Authorization: Basic auth (admin/admin123)
```

**Expected Result**: `200 OK` (GET requests don't require CSRF tokens)

---

#### Test 2: POST Request Without CSRF Token (Should Fail)

**Request:**
```
POST http://localhost:8080/api/v1/test/protected
Authorization: Basic auth (admin/admin123)
Content-Type: application/json

{
  "data": "test"
}
```

**Expected Result**: `403 Forbidden`

**Response**:
```json
{
  "timestamp": "2025-01-15T10:30:00.000+00:00",
  "status": 403,
  "error": "Forbidden",
  "path": "/api/v1/test/protected"
}
```

**Reason**: CSRF token is missing

---

#### Test 3: POST Request With Valid CSRF Token (Should Work)

**Step 1**: Get the CSRF token
```
GET http://localhost:8080/csrf-token
```

**Response**:
```json
{
  "token": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "headerName": "X-XSRF-TOKEN",
  "parameterName": "_csrf"
}
```

**Step 2**: Copy the token value

**Step 3**: Make POST request with token
```
POST http://localhost:8080/api/v1/test/protected
Authorization: Basic auth (admin/admin123)
X-XSRF-TOKEN: a1b2c3d4-e5f6-7890-abcd-ef1234567890
Content-Type: application/json

{
  "data": "test"
}
```

**Expected Result**: `200 OK`

**Response**:
```json
{
  "message": "POST request successful! CSRF token was valid.",
  "timestamp": "2025-01-15T10:35:00",
  "csrf_required": true,
  "auth_required": true,
  "received_data": {
    "data": "test"
  }
}
```

---

#### Test 4: POST to Admin Endpoint as Non-Admin (With CSRF Token)

**Request:**
```
POST http://localhost:8080/api/v1/test/admin
Authorization: Basic auth (manager/manager123)
X-XSRF-TOKEN: {valid-token}
Content-Type: application/json

{
  "action": "admin operation"
}
```

**Expected Result**: `403 Forbidden` (insufficient role, not CSRF issue)

---

#### Test 5: POST to Admin Endpoint as Admin (With CSRF Token)

**Request:**
```
POST http://localhost:8080/api/v1/test/admin
Authorization: Basic auth (admin/admin123)
X-XSRF-TOKEN: {valid-token}
Content-Type: application/json

{
  "action": "admin operation"
}
```

**Expected Result**: `200 OK`

---

### Complete Postman Testing Workflow

1. **Start the application**: `mvn spring-boot:run`

2. **Test public endpoint** (no auth, no CSRF):
   ```
   GET http://localhost:8080/api/v1/test/public
   → 200 OK
   ```

3. **Get CSRF token**:
   ```
   GET http://localhost:8080/csrf-token
   → Copy the token value
   ```

4. **Test protected GET** (with auth, no CSRF needed):
   ```
   GET http://localhost:8080/api/v1/test/protected
   Authorization: Basic admin/admin123
   → 200 OK
   ```

5. **Test protected POST without CSRF** (should fail):
   ```
   POST http://localhost:8080/api/v1/test/protected
   Authorization: Basic admin/admin123
   → 403 Forbidden
   ```

6. **Test protected POST with CSRF** (should work):
   ```
   POST http://localhost:8080/api/v1/test/protected
   Authorization: Basic admin/admin123
   X-XSRF-TOKEN: {your-token-here}
   → 200 OK
   ```

7. **Test with invalid CSRF token** (should fail):
   ```
   POST http://localhost:8080/api/v1/test/protected
   Authorization: Basic admin/admin123
   X-XSRF-TOKEN: invalid-token-12345
   → 403 Forbidden
   ```

8. **Test other state-changing methods** (PUT, DELETE):
   ```
   PUT http://localhost:8080/api/v1/test/protected
   Authorization: Basic admin/admin123
   X-XSRF-TOKEN: {your-token-here}
   → 200 OK

   DELETE http://localhost:8080/api/v1/test/protected/1
   Authorization: Basic admin/admin123
   X-XSRF-TOKEN: {your-token-here}
   → 200 OK
   ```

### How to Add CSRF Token in Postman

**Method 1: Using Headers (Recommended)**
1. Get token from `/csrf-token` endpoint
2. In your POST/PUT/DELETE request, go to **Headers** tab
3. Add new header:
   - Key: `X-XSRF-TOKEN`
   - Value: `{paste-token-here}`

**Method 2: Using Request Parameter**
1. Get token from `/csrf-token` endpoint
2. In your POST request, add to URL or body:
   - URL: `http://localhost:8080/api/v1/test/protected?_csrf={token}`
   - OR in form-data body: key=`_csrf`, value=`{token}`

**Method 1 (Headers) is recommended** as it's cleaner and works with JSON requests.

### Summary: CSRF vs Regular Authorization

| Aspect | Basic Auth | CSRF Protection |
|--------|-----------|-----------------|
| **Purpose** | Verify user identity | Prevent unauthorized requests from malicious sites |
| **Protects Against** | Unauthorized access | Cross-site request forgery attacks |
| **Required For** | All protected endpoints | State-changing methods (POST/PUT/DELETE/PATCH) |
| **How It Works** | Username + password in header | Unique token per session |
| **Attack Vector** | Credential theft | Malicious website making requests |
| **Spring Security** | HTTP Basic Authentication | Synchronizer Token Pattern |

**Both are required for full security:**
- Basic Auth verifies "Who are you?"
- CSRF Protection verifies "Is this request from you or from a malicious site?"

### Expected Behavior Summary

| Request Type | Auth | CSRF Token | Expected Result |
|-------------|------|------------|----------------|
| GET (any endpoint) | No | - | 401 Unauthorized |
| GET (any endpoint) | Yes | - | 200 OK |
| POST (any endpoint) | No | No | 401 Unauthorized |
| POST (any endpoint) | Yes | No | **403 Forbidden** (CSRF) |
| POST (any endpoint) | Yes | Invalid | **403 Forbidden** (CSRF) |
| POST (any endpoint) | Yes | Valid | 200 OK (or 403 if role insufficient) |

### Key Files Modified for Lab 4

1. **SecurityConfig.java** - CSRF configuration
   - Enabled CSRF protection (line 87-92)
   - Configured CookieCsrfTokenRepository
   - Added public endpoints

2. **CsrfController.java** - New controller
   - Provides `/csrf-token` endpoint
   - Returns token in JSON format

3. **TestController.java** - New test controller
   - Multiple endpoints to demonstrate CSRF
   - Different HTTP methods (GET, POST, PUT, DELETE)
   - Role-based authorization examples

4. **README.md** - This documentation
   - Complete CSRF explanation
   - Testing instructions
   - Postman examples

---

## Task №5 — Role-Based Access Control (RBAC)

### Users and Roles

The application has 3 users with different permission levels:

| Username | Password | Role | Permissions |
|----------|----------|------|-------------|
| **admin** | admin123 | ADMIN | Full access - can create, read, update, delete all resources |
| **manager** | manager123 | MANAGER | Can manage products (create, update, delete), view all orders |
| **alice** | alice123 | CUSTOMER | Can view products, create and view own orders only |

### Access Control Rules

#### Products (`/api/products`)

| Operation | Endpoint | Method | ADMIN | MANAGER | CUSTOMER |
|-----------|----------|--------|-------|---------|----------|
| List all products | `/api/products` | GET | ✅ | ✅ | ✅ |
| Get product by ID | `/api/products/{id}` | GET | ✅ | ✅ | ✅ |
| Create product | `/api/products` | POST | ✅ | ✅ | ❌ 403 |
| Update product | `/api/products/{id}` | PUT | ✅ | ✅ | ❌ 403 |
| Delete product | `/api/products/{id}` | DELETE | ✅ | ✅ | ❌ 403 |

#### Orders (`/api/orders`)

| Operation | Endpoint | Method | ADMIN | MANAGER | CUSTOMER |
|-----------|----------|--------|-------|---------|----------|
| List orders | `/api/orders` | GET | All orders | All orders | Own orders only |
| Create order | `/api/orders` | POST | ✅ | ✅ | ✅ |
| Get order by ID | `/api/orders/{id}` | GET | ✅ | ✅ | Own orders only |
| Delete order | `/api/orders/{id}` | DELETE | ✅ | ✅ | ❌ 403 |

### Sample Data

The application automatically initializes with 5 sample products on startup:

1. **Laptop Dell XPS 15** - SKU: TECH-001, Price: $1299.99, Stock: 10
2. **Wireless Mouse Logitech MX** - SKU: TECH-002, Price: $79.99, Stock: 50
3. **Mechanical Keyboard Keychron K2** - SKU: TECH-003, Price: $89.99, Stock: 30
4. **Monitor LG 27 inch 4K** - SKU: TECH-004, Price: $399.99, Stock: 15
5. **Headphones Sony WH-1000XM5** - SKU: TECH-005, Price: $349.99, Stock: 25

### 10 HTTP Requests for Lab 5 (10 × 0.5 = 5 points)

#### Request 1: GET Products as ADMIN ✅
```
GET http://localhost:8080/api/products
Authorization: Basic admin:admin123
Expected: 200 OK - Returns all 5 products
```

#### Request 2: GET Products as MANAGER ✅
```
GET http://localhost:8080/api/products
Authorization: Basic manager:manager123
Expected: 200 OK - Returns all 5 products
```

#### Request 3: GET Products as CUSTOMER ✅
```
GET http://localhost:8080/api/products
Authorization: Basic alice:alice123
Expected: 200 OK - Returns all 5 products
```

#### Request 4: POST Create Product as ADMIN ✅
```
POST http://localhost:8080/api/products
Authorization: Basic admin:admin123
Content-Type: application/json

{
  "sku": "LAPTOP-001",
  "name": "Gaming Laptop ASUS ROG",
  "price": 1499.99,
  "stock": 5
}

Expected: 201 Created - Product created successfully
```

#### Request 5: POST Create Product as MANAGER ✅
```
POST http://localhost:8080/api/products
Authorization: Basic manager:manager123
Content-Type: application/json

{
  "sku": "MOUSE-001",
  "name": "Gaming Mouse Razer DeathAdder",
  "price": 69.99,
  "stock": 20
}

Expected: 201 Created - Product created successfully
```

#### Request 6: POST Create Product as CUSTOMER ❌
```
POST http://localhost:8080/api/products
Authorization: Basic alice:alice123
Content-Type: application/json

{
  "sku": "KEYBOARD-001",
  "name": "Mechanical Keyboard",
  "price": 129.99,
  "stock": 15
}

Expected: 403 Forbidden - Access Denied (CUSTOMER cannot create products)
```

#### Request 7: DELETE Product as ADMIN ✅
```
DELETE http://localhost:8080/api/products/1
Authorization: Basic admin:admin123

Expected: 204 No Content - Product deleted successfully
```

#### Request 8: DELETE Product as MANAGER ✅
```
DELETE http://localhost:8080/api/products/2
Authorization: Basic manager:manager123

Expected: 204 No Content - Product deleted successfully
```

#### Request 9: DELETE Product as CUSTOMER ❌
```
DELETE http://localhost:8080/api/products/3
Authorization: Basic alice:alice123

Expected: 403 Forbidden - Access Denied (CUSTOMER cannot delete products)
```

#### Request 10: GET Products WITHOUT Authentication ❌
```
GET http://localhost:8080/api/products
Authorization: No Auth

Expected: 401 Unauthorized - Authentication required
```

### Expected Results Summary

| # | Endpoint | Method | User | Expected Status | Demonstrates |
|---|----------|--------|------|----------------|--------------|
| 1 | /api/products | GET | admin | 200 OK | ADMIN can read |
| 2 | /api/products | GET | manager | 200 OK | MANAGER can read |
| 3 | /api/products | GET | alice | 200 OK | CUSTOMER can read |
| 4 | /api/products | POST | admin | 201 Created | ADMIN can create |
| 5 | /api/products | POST | manager | 201 Created | MANAGER can create |
| 6 | /api/products | POST | alice | **403 Forbidden** | CUSTOMER cannot create |
| 7 | /api/products/1 | DELETE | admin | 204 No Content | ADMIN can delete |
| 8 | /api/products/2 | DELETE | manager | 204 No Content | MANAGER can delete |
| 9 | /api/products/3 | DELETE | alice | **403 Forbidden** | CUSTOMER cannot delete |
| 10 | /api/products | GET | (none) | **401 Unauthorized** | Auth required |

### Implementation Details

**Authorization Rules** - Located in `ProductController.java`:
```java
// Any authenticated user can read products
@GetMapping
public List<Product> list() { ... }

// Only ADMIN or MANAGER can create/update/delete
@PostMapping
@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
public Product create(@RequestBody @Valid ProductDTO dto) { ... }

@DeleteMapping("/{id}")
@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
public void delete(@PathVariable Long id) { ... }
```

**Security Configuration** - Located in `SecurityConfig.java:85-121`:
- HTTP Basic Authentication for all endpoints
- Session management enabled for CSRF support
- Method-level security with `@PreAuthorize` annotations
- Role-based authorization using Spring Security

### Key Files for Lab 5

1. **SecurityConfig.java** - User definitions and security configuration
2. **ProductController.java** - `@PreAuthorize` annotations for role-based access
3. **OrderController.java** - Custom authorization logic in service layer
4. **DataInitializer.java** - Populates sample data on startup

---

## Task №6 — Advanced Route Annotations & Authorization

### What's New in Lab 6

Lab 6 enhances Lab 5 by adding **more detailed security annotations** to demonstrate various Spring Security authorization patterns:

1. **hasRole('ROLE')** - Single role check
2. **hasAnyRole('ROLE1', 'ROLE2')** - Multiple roles (OR condition)
3. **Different permission levels** for different operations
4. **Custom authorization logic** in service layer

### Key Changes from Lab 5

| Operation | Lab 5 Authorization | Lab 6 Authorization | Change |
|-----------|---------------------|---------------------|--------|
| DELETE Product | ADMIN **or** MANAGER | **ADMIN only** | ⚠️ More restrictive |
| DELETE Order | No annotation | ADMIN or MANAGER | ✅ Added annotation |
| All other operations | Same | Same | - |

### Security Annotations Used

#### ProductController (`/api/products`)

| Endpoint | Method | Annotation | Allowed Roles |
|----------|--------|------------|---------------|
| `/api/products` | GET | None (authenticated) | ADMIN, MANAGER, CUSTOMER |
| `/api/products/{id}` | GET | None (authenticated) | ADMIN, MANAGER, CUSTOMER |
| `/api/products` | POST | `@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")` | ADMIN, MANAGER |
| `/api/products/{id}` | PUT | `@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")` | ADMIN, MANAGER |
| `/api/products/{id}` | DELETE | `@PreAuthorize("hasRole('ADMIN')")` | **ADMIN only** |

#### OrderController (`/api/orders`)

| Endpoint | Method | Annotation | Allowed Roles | Special Logic |
|----------|--------|------------|---------------|---------------|
| `/api/orders` | GET | None | All | ADMIN/MANAGER see all, CUSTOMER sees own |
| `/api/orders` | POST | None | All | All can create orders |
| `/api/orders/{id}` | GET | None | All | ADMIN/MANAGER see all, CUSTOMER sees own |
| `/api/orders/{id}` | DELETE | `@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")` | ADMIN, MANAGER |

### 10 HTTP Requests for Lab 6 (10 × 0.5 = 5 points)

#### Request 1: GET Products as CUSTOMER ✅
```
GET http://localhost:8080/api/products
Authorization: Basic alice:alice123

Expected: 200 OK
Demonstrates: Authenticated users can read products
```

#### Request 2: POST Create Product as ADMIN ✅
```
POST http://localhost:8080/api/products
Authorization: Basic admin:admin123
Content-Type: application/json

{
  "sku": "TABLET-001",
  "name": "iPad Pro 12.9",
  "price": 1099.99,
  "stock": 8
}

Expected: 201 Created
Demonstrates: @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
```

#### Request 3: POST Create Product as MANAGER ✅
```
POST http://localhost:8080/api/products
Authorization: Basic manager:manager123
Content-Type: application/json

{
  "sku": "SPEAKER-001",
  "name": "Sonos One",
  "price": 219.99,
  "stock": 12
}

Expected: 201 Created
Demonstrates: @PreAuthorize("hasAnyRole('ADMIN','MANAGER')") allows MANAGER
```

#### Request 4: POST Create Product as CUSTOMER ❌
```
POST http://localhost:8080/api/products
Authorization: Basic alice:alice123
Content-Type: application/json

{
  "sku": "WEBCAM-001",
  "name": "Logitech C920",
  "price": 79.99,
  "stock": 20
}

Expected: 403 Forbidden
Demonstrates: CUSTOMER role blocked by @PreAuthorize
```

#### Request 5: PUT Update Product as MANAGER ✅
```
PUT http://localhost:8080/api/products/1
Authorization: Basic manager:manager123
Content-Type: application/json

{
  "sku": "TECH-001",
  "name": "Laptop Dell XPS 15 (Updated)",
  "price": 1199.99,
  "stock": 5
}

Expected: 200 OK
Demonstrates: @PreAuthorize("hasAnyRole('ADMIN','MANAGER')") allows update
```

#### Request 6: DELETE Product as ADMIN ✅
```
DELETE http://localhost:8080/api/products/3
Authorization: Basic admin:admin123

Expected: 204 No Content
Demonstrates: @PreAuthorize("hasRole('ADMIN')") - Only ADMIN
```

#### Request 7: DELETE Product as MANAGER ❌ (NEW in Lab 6!)
```
DELETE http://localhost:8080/api/products/4
Authorization: Basic manager:manager123

Expected: 403 Forbidden
Demonstrates: @PreAuthorize("hasRole('ADMIN')") blocks MANAGER
Note: This is DIFFERENT from Lab 5 where MANAGER could delete!
```

#### Request 8: DELETE Product as CUSTOMER ❌
```
DELETE http://localhost:8080/api/products/5
Authorization: Basic alice:alice123

Expected: 403 Forbidden
Demonstrates: CUSTOMER blocked by @PreAuthorize("hasRole('ADMIN')")
```

#### Request 9: PUT Update Product as CUSTOMER ❌
```
PUT http://localhost:8080/api/products/2
Authorization: Basic alice:alice123
Content-Type: application/json

{
  "sku": "TECH-002",
  "name": "Hacked Product",
  "price": 0.01,
  "stock": 999
}

Expected: 403 Forbidden
Demonstrates: @PreAuthorize("hasAnyRole('ADMIN','MANAGER')") blocks CUSTOMER
```

#### Request 10: GET Products WITHOUT Authentication ❌
```
GET http://localhost:8080/api/products
Authorization: No Auth

Expected: 401 Unauthorized
Demonstrates: All endpoints require authentication (SecurityConfig)
```

### Expected Results Summary

| # | Endpoint | Method | User | Annotation Pattern | Expected | Key Demonstration |
|---|----------|--------|------|-------------------|----------|-------------------|
| 1 | /api/products | GET | alice | Authenticated | 200 OK | Basic authentication |
| 2 | /api/products | POST | admin | hasAnyRole(ADMIN,MANAGER) | 201 Created | ADMIN allowed |
| 3 | /api/products | POST | manager | hasAnyRole(ADMIN,MANAGER) | 201 Created | MANAGER allowed |
| 4 | /api/products | POST | alice | hasAnyRole(ADMIN,MANAGER) | **403 Forbidden** | CUSTOMER blocked |
| 5 | /api/products/1 | PUT | manager | hasAnyRole(ADMIN,MANAGER) | 200 OK | MANAGER can update |
| 6 | /api/products/3 | DELETE | admin | hasRole(ADMIN) | 204 No Content | ADMIN can delete |
| 7 | /api/products/4 | DELETE | manager | hasRole(ADMIN) | **403 Forbidden** | **MANAGER blocked!** (Lab 6 change) |
| 8 | /api/products/5 | DELETE | alice | hasRole(ADMIN) | **403 Forbidden** | CUSTOMER blocked |
| 9 | /api/products/2 | PUT | alice | hasAnyRole(ADMIN,MANAGER) | **403 Forbidden** | CUSTOMER blocked |
| 10 | /api/products | GET | (none) | Authenticated | **401 Unauthorized** | No auth = denied |

### Annotation Patterns Demonstrated

1. **No annotation (default)** - Requires authentication only
   ```java
   @GetMapping
   public List<Product> list() { ... }
   ```

2. **hasAnyRole() - Multiple roles (OR)**
   ```java
   @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
   public Product create(...) { ... }
   ```

3. **hasRole() - Single role**
   ```java
   @PreAuthorize("hasRole('ADMIN')")
   public void delete(...) { ... }
   ```

4. **Custom logic in service layer**
   ```java
   // Controller: No @PreAuthorize
   // Service: Checks if user owns the resource
   public List<Order> listAll(Authentication auth) {
       if (isAdmin(auth) || isManager(auth)) {
           return findAll(); // See all orders
       }
       return findByOwner(auth.getName()); // See own only
   }
   ```

### Testing in Postman

**Important:** Request 7 is the **key difference** from Lab 5!

In Lab 5: MANAGER could DELETE products
In Lab 6: Only ADMIN can DELETE products

**To test:**
1. Start app: `./mvnw spring-boot:run`
2. Follow the 10 requests above
3. **Pay attention to Request 7** - MANAGER getting 403 when deleting

### Key Files Modified for Lab 6

1. **ProductController.java** - Enhanced annotations
   - DELETE changed to `@PreAuthorize("hasRole('ADMIN')")` (was `hasAnyRole` before)
   - Detailed JavaDoc comments explaining each annotation

2. **OrderController.java** - Added annotations
   - DELETE now has `@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")`
   - Documented custom authorization logic

3. **README.md** - This documentation

---

