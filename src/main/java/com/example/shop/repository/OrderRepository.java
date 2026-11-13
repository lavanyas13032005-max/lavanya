package com.example.shop.repository;

import com.example.shop.model.CustomerOrder;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<CustomerOrder, Long> {
    @EntityGraph(attributePaths = "items")
    List<CustomerOrder> findByUserEmailIgnoreCaseOrderByCreatedAtDesc(String email);

    @EntityGraph(attributePaths = {"items", "user"})
    List<CustomerOrder> findAllByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = {"items", "user"})
    Optional<CustomerOrder> findWithItemsById(Long id);
}
