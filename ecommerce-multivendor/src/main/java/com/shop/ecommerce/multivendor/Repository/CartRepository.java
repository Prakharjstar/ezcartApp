package com.shop.ecommerce.multivendor.Repository;

import com.shop.ecommerce.multivendor.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface  CartRepository extends JpaRepository<Cart,Long> {
}
