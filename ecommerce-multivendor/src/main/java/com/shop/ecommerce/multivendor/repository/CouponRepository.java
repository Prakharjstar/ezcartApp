package com.shop.ecommerce.multivendor.repository;

import com.shop.ecommerce.multivendor.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon ,Long> {
    Coupon findByCode(String code);

}
