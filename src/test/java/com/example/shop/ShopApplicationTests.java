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
                .param("name", "N").param("email", "n@example.com").param("password", "short"))
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
}
