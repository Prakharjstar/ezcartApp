package com.shop.ecommerce.multivendor.Service;

import com.shop.ecommerce.multivendor.model.Cart;
import com.shop.ecommerce.multivendor.model.Coupon;
import com.shop.ecommerce.multivendor.model.User;

import java.util.List;

public interface CouponService {

    Cart applyCoupon(String code , double orderValue , User user) throws Exception;
    Cart removeCoupon(String code ,User user);
    Coupon findCouponById(Long id);
    Coupon createCoupon(Coupon coupon);
    List<Coupon> findAllCoupons();
    void deleteCoupon(Long id);
}
