
# Simple Marketplace Web Application
Spring Boot CRUD + Basic Authentication  
Tasks №2 and №3

---

## Description
A simple Spring Boot web application that demonstrates:
- CRUD REST API for managing Products and Orders
- Basic Authentication protection with user roles
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

