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

## Notes

- Data is stored in a local H2 file database under `data/`.
- Payment is simulated. No card details are collected.
