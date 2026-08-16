package com.shop.ecommerce.multivendor.repository;

import com.shop.ecommerce.multivendor.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface  CartRepository extends JpaRepository<Cart,Long> {
    Cart findByUserId(Long id);
}
