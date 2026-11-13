# Shop

A Spring Boot 3 e-commerce application built with Maven, Java 17, Thymeleaf, Spring Data JPA, Spring Security and H2.

## Features

- Product catalogue with search and paging
- Session-based shopping cart
- Customer registration and login (BCrypt passwords, CSRF protection)
- Checkout with a stock check, and order history
- Admin pages to manage products and update order status (cancelling restores stock)

## Run it

    mvn spring-boot:run

Set `ADMIN_EMAIL` and `ADMIN_PASSWORD` to choose the admin login. If you leave the password unset, a random one is written to the log on first start.

Run the tests with `mvn verify`.

## Database

By default the app uses an embedded H2 file database in `data/`. Products, orders and accounts survive restarts. No server is needed.

To use PostgreSQL, start the app with the `postgres` profile and set the connection through environment variables:

    DB_HOST=localhost DB_PORT=5432 DB_NAME=shop DB_USER=shop DB_PASSWORD=... \
      java -jar target/shop-0.0.1-SNAPSHOT.jar --spring.profiles.active=postgres

`DB_PASSWORD` is required and has no default. The schema is created automatically on first start.

## Notes

- Payment is simulated.
