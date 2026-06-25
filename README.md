# 🎮 GameStop - A Video Game Store — E-Commerce API

A full-stack e-commerce application for an online video game store, built as a capstone project. The backend is a Spring Boot REST API backed by MySQL; the frontend is a provided storefront that utilizes the API for browsing games, managing a shopping cart, and checking out.

This project started from an existing Spring Boot API which was partially built and buggy. It was extended with new features, bug fixes, and full test coverage via Insomnia.

---

## 📸 Screenshots


| Home Page | Product Browsing | Shopping Cart |
|---|---|---|
| <img width="200" height="256" alt="image" src="https://github.com/user-attachments/assets/f74047a7-989a-46c4-ad79-cd9c2282c1c7" /> | <img width="203" height="251" alt="image" src="https://github.com/user-attachments/assets/12167d87-5ebc-4774-a2e1-dc9153908d64" />| <img width="200" height="250" alt="image" src="https://github.com/user-attachments/assets/ae96d23c-2f14-420f-84dc-7d373a73ba86" />

---

## 🧱 Tech Stack

- **Backend:** Java, Spring Boot, Spring Security (JWT authentication), Spring Data JPA
- **Database:** MySQL
- **Frontend:** Provided storefront web app (Video Game Store theme)
- **Testing:** Insomnia (REST API test suites)

---

## ✨ Features

### Core Features
- User registration and login with JWT-based authentication
- Browse products by category
- Search and filter products by category, price range, and Genre
- Full CRUD on categories and products, restricted to `ADMIN` users for write operations

### Shopping Cart
- Add products to a per-user based shopping cart
- Automatically increments quantity if a product is already in the cart
- Update item quantity directly
- Clear the entire cart
- Cart contents saved after logout/login

### User Profile
- View and update a personal profile (name, contact info, shipping address)
- Profile is automatically created at registration

### Checkout
- Convert a shopping cart into a real order with line items
- Order shipping address is pulled automatically from the user's profile
- Cart is cleared automatically once an order is placed
- Guards against checking out an empty cart (`400 Bad Request`)

---

## 🐛 Bugs Fixed

1. **Product search returning incomplete results** — the search/filter logic for `catergory`, `minPrice`, `maxPrice`, and `Genre` query parameters was silently excluding valid products under certain filter combinations. Fixed the filtering logic so all valid query-parameter combinations return the correct result set.
2. **Product stock updates not persisting** — updating a product's price or description worked, but updating `stock` had no effect on the database despite returning `200 OK`. Fixed the update logic so all editable product fields are persisted correctly.
3. **Category lookups returning `500` instead of `404`** — looking up a category by an ID that doesn't exist threw an unhandled `ResourceNotFoundException`, resulting in a generic `500 Internal Server Error`. Added a global exception handler (`@GlobalExceptionHandler`) to translate this (and other custom exceptions, like checking out an empty cart) into proper HTTP status codes.
4. **`DELETE /cart` throwing a transaction error** — clearing the cart threw `TransactionRequiredException` because the custom `deleteByUserId` repository method wasn't wrapped in a transaction. Fixed by adding `@Modifying` and `@Transactional` to the repository method.

---

## 🔌 API Endpoints

### Authentication
| Verb | URL | Body |
|---|---|---|
| POST | `/register` | `{ username, password, confirmPassword, role }` |
| POST | `/login` | `{ username, password }` |

### Categories
| Verb | URL | Auth |
|---|---|---|
| GET | `/categories` | Public |
| GET | `/categories/{id}` | Public |
| GET | `/categories/{id}/products` | Public |
| POST | `/categories` | Admin |
| PUT | `/categories/{id}` | Admin |
| DELETE | `/categories/{id}` | Admin |

### Products
| Verb | URL | Auth |
|---|---|---|
| GET | `/products` | Public |
| GET | `/products/{id}` | Public |
| POST | `/products` | Admin |
| PUT | `/products/{id}` | Admin |
| DELETE | `/products/{id}` | Admin |

### Shopping Cart
| Verb | URL | Auth |
|---|---|---|
| GET | `/cart` | Logged in |
| POST | `/cart/products/{productId}` | Logged in |
| PUT | `/cart/products/{productId}` | Logged in |
| DELETE | `/cart` | Logged in |

### Profile
| Verb | URL | Auth |
|---|---|---|
| GET | `/profile` | Logged in |
| PUT | `/profile` | Logged in |

### Orders
| Verb | URL | Auth |
|---|---|---|
| POST | `/orders` | Logged in |

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- MySQL Server
- MySQL Workbench
- Maven 
- [Insomnia](https://insomnia.rest/) for API testing

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/yu26s9-Tushar673/gamestop-ecom-api-backend
   cd (repo-folder)
   ```

2. **Create the database**
   Open the `video_game_store.sql` script (in the `database` folder) in MySQL Workbench and execute it. This creates the schema and seeds it with sample products and three demo users (`user`, `admin`, `george` — password for all is `password`).

3. **Configure the database connection**
   Update `src/main/resources/application.properties` with your local MySQL credentials:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/<your_db_name>
   spring.datasource.username=<your_username>
   spring.datasource.password=<your_password>
   ```

4. **Run the application**
   Run EcommerceApplication.java -> ... wait for SpringBoot to Start Up
   The API will start on `http://localhost:8080`.

5. **Run the frontend**
   Open the provided frontend project, navigate to index.html, click on browser icon.

6. **Test with Insomnia**
   Import the included Insomnia collection (if provided in the repo) or test endpoints manually. Log in via `/login` first and attach the returned JWT as a `Bearer` token in the `Authorization` header for any endpoint that requires authentication.

---

## 💡 Interesting Code: Fixing a Silent Transaction Bug with `@Modifying` + `@Transactional`

While testing the "Clear Cart" feature, `DELETE /cart` was returning a `500 Internal Server Error` instead of clearing the cart. The exception was:

```
jakarta.persistence.TransactionRequiredException: No EntityManager with actual
transaction available for current thread - cannot reliably process 'remove' call
```

The cause was this derived query method in the repository:

```java
public interface ShoppingCartRepository extends JpaRepository<CartItem, Integer>
{
    List<CartItem> findByUserId(int userId);

    CartItem findByUserIdAndProductId(int userId, int productId);

    void deleteByUserId(int userId);
}
```

Spring Data JPA generates a working implementation for `findByUserId` and `findByUserIdAndProductId` automatically, since they're read-only queries. But `deleteByUserId` is a bulk *modifying* operation, and Spring Data won't run it safely without being told that it's a write, and without an active transaction to run it in. Built-in methods like `save()` and `deleteById()` get this transaction wrapping for free; custom derived delete/update methods don't.

The fix was two annotations on the method itself:

```java
@Modifying
@Transactional
void deleteByUserId(int userId);
```

`@Modifying` tells Spring Data this query changes data rather than reading it, and `@Transactional` opens a transaction with an active `EntityManager` so the delete actually has something to execute within.

---

## 🔮 Future Improvements

Some features I'd consider for a future version:
- Implement a working checkout that logs the order into system.
- Order history page so users can view past orders
- Wishlist separate from the active cart

---

## 📋 Project Board

Work was tracked on a GitHub Project board, with starter bug issues auto-created on first push.
