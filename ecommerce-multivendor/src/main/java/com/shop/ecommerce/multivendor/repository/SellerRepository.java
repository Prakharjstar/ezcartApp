package com.shop.ecommerce.multivendor.repository;

import com.shop.ecommerce.multivendor.domain.AccountStatus;
import com.shop.ecommerce.multivendor.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {
    Seller findByEmail(String email);
    List<Seller> findByAccountStatus(AccountStatus accountStatus);
}
