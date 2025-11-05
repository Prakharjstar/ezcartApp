package com.shop.ecommerce.multivendor.repository;

import com.shop.ecommerce.multivendor.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product , Long> {
}
