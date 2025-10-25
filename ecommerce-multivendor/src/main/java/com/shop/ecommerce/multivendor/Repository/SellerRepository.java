package com.shop.ecommerce.multivendor.Repository;

import com.shop.ecommerce.multivendor.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, Long> {
    Seller findByEmail(String email);
}
