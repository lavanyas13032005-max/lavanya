package com.example.shop.repository;

import com.example.shop.model.Product;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {
    /** Locks the row so stock cannot change between the check and the payment. */
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    java.util.Optional<Product> findForUpdateById(@Param("id") Long id);

    /**
     * Filters by category (exact, case-insensitive) and search text. Pass "" for "no filter".
     * Null parameters are avoided on purpose: PostgreSQL cannot apply lower() to a null bound as bytea.
     */
    @Query("select p from Product p where (:category = '' or lower(p.category) = lower(:category)) "
            + "and (:q = '' or lower(p.name) like lower(concat('%', :q, '%')) "
            + "or lower(coalesce(p.category,'')) like lower(concat('%', :q, '%')))")
    Page<Product> search(@Param("q") String q, @Param("category") String category, Pageable pageable);

    @Query("select distinct p.category from Product p where p.category is not null and p.category <> '' order by p.category")
    List<String> findCategories();

    long countByStock(int stock);

    long countByStockBetween(int from, int to);

    @Query("select coalesce(sum(p.price * p.stock), 0) from Product p")
    java.math.BigDecimal inventoryValue();

    /** Pass "" for no text filter and Integer.MAX_VALUE for no stock limit. */
    @Query("select p from Product p where (:q = '' or lower(p.name) like lower(concat('%', :q, '%')) "
            + "or lower(coalesce(p.category,'')) like lower(concat('%', :q, '%'))) "
            + "and p.stock <= :maxStock order by p.stock asc, p.name asc")
    List<Product> adminSearch(@Param("q") String q, @Param("maxStock") int maxStock);
}
