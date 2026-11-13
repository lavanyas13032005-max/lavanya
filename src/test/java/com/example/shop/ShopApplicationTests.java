package com.example.shop;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.shop.model.*;
import com.example.shop.repository.*;
import com.example.shop.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "app.admin.password=test-admin-pass"})
@AutoConfigureMockMvc
@Transactional
class ShopApplicationTests {
    @Autowired MockMvc mvc;
    @Autowired ProductRepository products;
    @Autowired UserRepository users;
    @Autowired OrderService orders;

    private Product product(int stock) {
        Product p = new Product();
        p.setName("Test");
        p.setPrice(new java.math.BigDecimal("10.00"));
        p.setStock(stock);
        return products.save(p);
    }

    private UserAccount customer() {
        UserAccount u = new UserAccount();
        u.setEmail("c@example.com");
        u.setName("C");
        u.setPasswordHash("x");
        return users.save(u);
    }

    @Test
    void homePageListsSeedProducts() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Wireless Headphones")));
    }

    @Test
    void categoryFilterShowsOnlyMatchingProducts() throws Exception {
        mvc.perform(get("/").param("category", "Books")).andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Clean Code")))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Wireless Headphones"))));
    }

    @Test
    void catalogShowsCategoryChipsAndPlaceholderImage() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(">Kitchen</a>")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/images/placeholder.svg")));
    }

    @Test
    void productImageUrlMustBeHttp() throws Exception {
        mvc.perform(post("/admin/products").with(user("a").roles("ADMIN")).with(csrf())
                        .param("name", "X").param("price", "5.00").param("stock", "1")
                        .param("imageUrl", "javascript:alert(1)"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("must be an http(s) URL")));
    }

    @Test
    void adminAreaRequiresAdminRole() throws Exception {
        mvc.perform(get("/admin/products")).andExpect(status().is3xxRedirection());
        mvc.perform(get("/admin/products").with(user("c@example.com").roles("USER"))).andExpect(status().isForbidden());
        mvc.perform(get("/admin/products").with(user("a").roles("ADMIN"))).andExpect(status().isOk());
    }

    @Test
    void registrationRejectsShortPassword() throws Exception {
        mvc.perform(post("/register").with(csrf())
                .param("name", "N").param("email", "n@example.com").param("password", "short").param("confirmPassword", "short"))
                .andExpect(status().isOk()).andExpect(view().name("register"));
    }

    @Test
    void checkoutDecrementsStockAndCancelRestoresIt() {
        Product p = product(5);
        customer();
        Cart cart = new Cart();
        cart.add(p.getId(), 3);
        CustomerOrder o = orders.checkout("c@example.com", cart, "1 Main St");
        assertThat(products.findById(p.getId()).get().getStock()).isEqualTo(2);
        assertThat(o.getTotal()).isEqualByComparingTo("30.00");
        assertThat(cart.isEmpty()).isTrue();

        orders.updateStatus(o.getId(), OrderStatus.CANCELLED);
        assertThat(products.findById(p.getId()).get().getStock()).isEqualTo(5);
    }

    @Test
    void checkoutFailsWhenStockInsufficient() {
        Product p = product(1);
        customer();
        Cart cart = new Cart();
        cart.add(p.getId(), 2);
        assertThatThrownBy(() -> orders.checkout("c@example.com", cart, "addr"))
                .isInstanceOf(OutOfStockException.class);
    }

    @Test
    void cartAddUpdateAndTotal() throws Exception {
        Product p = product(5);
        var session = new org.springframework.mock.web.MockHttpSession();
        mvc.perform(post("/cart/add").session(session).with(csrf())
                .param("productId", p.getId().toString()).param("quantity", "2"))
                .andExpect(redirectedUrl("/cart"));
        mvc.perform(get("/cart").session(session)).andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Total (2 items)")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("20.00")));
        mvc.perform(post("/cart/update").session(session).with(csrf())
                .param("productId", p.getId().toString()).param("quantity", "3"));
        mvc.perform(get("/cart").session(session))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("30.00")));
    }

    @Test
    void cartQuantityIsCappedAtStockAndZeroRemoves() throws Exception {
        Product p = product(2);
        var session = new org.springframework.mock.web.MockHttpSession();
        mvc.perform(post("/cart/add").session(session).with(csrf())
                .param("productId", p.getId().toString()).param("quantity", "9"))
                .andExpect(flash().attributeExists("error"));
        mvc.perform(get("/cart").session(session))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Total (2 items)")));
        mvc.perform(post("/cart/update").session(session).with(csrf())
                .param("productId", p.getId().toString()).param("quantity", "0"));
        mvc.perform(get("/cart").session(session))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Your cart is empty")));
    }

    @Test
    void cartWarnsWhenStockDropsBelowQuantity() throws Exception {
        Product p = product(5);
        var session = new org.springframework.mock.web.MockHttpSession();
        mvc.perform(post("/cart/add").session(session).with(csrf())
                .param("productId", p.getId().toString()).param("quantity", "4"));
        p.setStock(2);
        products.saveAndFlush(p);
        mvc.perform(get("/cart").session(session))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Only 2 in stock")))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("/checkout"))));
        p.setStock(0);
        products.saveAndFlush(p);
        mvc.perform(get("/cart").session(session))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Out of stock. Remove")));
    }

    private org.springframework.test.web.servlet.ResultActions register(String name, String email, String pw, String confirm) throws Exception {
        return mvc.perform(post("/register").with(csrf()).param("name", name).param("email", email)
                .param("password", pw).param("confirmPassword", confirm));
    }

    @Test
    void registrationValidatesFields() throws Exception {
        register("", "not-an-email", "password1", "password1")
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors("form", "name", "email"));
        register("N", "n@example.com", "onlyletters", "onlyletters")
                .andExpect(model().attributeHasFieldErrors("form", "password"));
        register("N", "n@example.com", "password1", "password2")
                .andExpect(model().attributeHasFieldErrors("form", "confirmPassword"));
    }

    @Test
    void registrationCreatesAccountAndRejectsDuplicateEmail() throws Exception {
        register("New User", "New@Example.com", "password1", "password1")
                .andExpect(redirectedUrl("/login?registered"));
        assertThat(users.findByEmailIgnoreCase("new@example.com")).isPresent();
        assertThat(users.findByEmailIgnoreCase("new@example.com").get().getPasswordHash()).startsWith("$2");
        register("Other", "NEW@example.com", "password1", "password1")
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors("form", "email"));
    }

    @Test
    void loginPageRendersAndRegisteredUserCanLogIn() throws Exception {
        mvc.perform(get("/login")).andExpect(status().isOk()).andExpect(view().name("login"));
        register("Log In", "login@example.com", "password1", "password1");
        mvc.perform(post("/login").with(csrf()).param("username", "login@example.com").param("password", "password1"))
                .andExpect(redirectedUrl("/"));
        mvc.perform(post("/login").with(csrf()).param("username", "login@example.com").param("password", "wrong"))
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    void dashboardIsAdminOnlyAndShowsStockAlerts() throws Exception {
        mvc.perform(get("/admin").with(user("c@example.com").roles("USER"))).andExpect(status().isForbidden());
        Product p = product(0);
        p.setName("Zero Widget");
        products.save(p);
        mvc.perform(get("/admin").with(user("a").roles("ADMIN"))).andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Admin dashboard")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Zero Widget")));
    }

    @Test
    void adminCanUpdateStockAndInvalidValueIsRejected() throws Exception {
        Product p = product(3);
        var admin = user("a").roles("ADMIN");
        mvc.perform(post("/admin/products/" + p.getId() + "/stock").with(admin).with(csrf()).param("stock", "42"))
                .andExpect(flash().attributeExists("message"));
        assertThat(products.findById(p.getId()).orElseThrow().getStock()).isEqualTo(42);
        mvc.perform(post("/admin/products/" + p.getId() + "/stock").with(admin).with(csrf()).param("stock", "-1"))
                .andExpect(flash().attributeExists("error"));
        assertThat(products.findById(p.getId()).orElseThrow().getStock()).isEqualTo(42);
        mvc.perform(post("/admin/products/" + p.getId() + "/stock").with(user("c@example.com").roles("USER")).with(csrf())
                .param("stock", "1")).andExpect(status().isForbidden());
    }

    @Test
    void adminCanAddProductAndLowStockFilterWorks() throws Exception {
        var admin = user("a").roles("ADMIN");
        mvc.perform(post("/admin/products").with(admin).with(csrf())
                .param("name", "Brand New Gadget").param("price", "9.99").param("stock", "2").param("category", "Tech"))
                .andExpect(redirectedUrl("/admin/products"));
        mvc.perform(get("/admin/products").with(admin).param("lowOnly", "true"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Brand New Gadget")))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Wireless Headphones"))));
        mvc.perform(post("/admin/products").with(admin).with(csrf())
                .param("name", "Too Many").param("price", "1.00").param("stock", "2000000"))
                .andExpect(status().isOk()).andExpect(view().name("admin/product-form"));
    }
}
