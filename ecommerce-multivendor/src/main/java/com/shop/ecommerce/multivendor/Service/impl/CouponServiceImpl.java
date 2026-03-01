package com.shop.ecommerce.multivendor.Service.impl;

import com.shop.ecommerce.multivendor.Service.CouponService;
import com.shop.ecommerce.multivendor.model.Cart;
import com.shop.ecommerce.multivendor.model.Coupon;
import com.shop.ecommerce.multivendor.model.User;
import com.shop.ecommerce.multivendor.repository.CartRepository;
import com.shop.ecommerce.multivendor.repository.CouponRepository;
import com.shop.ecommerce.multivendor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    @Override
    public Cart applyCoupon(String code, double orderValue, User user) throws Exception {
        Coupon coupon = couponRepository.findByCode(code);

        if (coupon == null) {
            throw new Exception("Coupon not valid");
        }

        if (user.getUsedCoupons().contains(coupon)) {
            throw new Exception("Coupon already used");
        }

        if (orderValue < coupon.getMinimumOrderValue()) {
            throw new Exception("Valid for minimum order value " + coupon.getMinimumOrderValue());
        }

        LocalDate today = LocalDate.now();
        // Inclusive check: start <= today <= end
        if (!coupon.isActive() || today.isBefore(coupon.getValidityStartDate()) || today.isAfter(coupon.getValidityEndDate())) {
            throw new Exception("Coupon not valid at this time");
        }

        Cart cart = cartRepository.findByUserId(user.getId());

        // Calculate discount and final price
        double originalPrice = cart.getTotalSellingPrice();
        double discountAmount = (originalPrice * coupon.getDiscountPercentage()) / 100.0;
        double finalPrice = originalPrice - discountAmount;

        // Update cart
        cart.setDiscountAmount(discountAmount);
        cart.setFinalPrice(finalPrice);
        cart.setCouponCode(code);

        // Mark coupon as used by user
        user.getUsedCoupons().add(coupon);
        userRepository.save(user);

        cartRepository.save(cart);

        return cart;
    }

    @Override
    public Cart removeCoupon(String code, User user) throws Exception {
        Coupon coupon = couponRepository.findByCode(code);

        if (coupon == null) {
            throw new Exception("Coupon not found");
        }

        Cart cart = cartRepository.findByUserId(user.getId());

        // Reset discount and final price
        cart.setDiscountAmount(0);
        cart.setFinalPrice(cart.getTotalSellingPrice());
        cart.setCouponCode(null);

        cartRepository.save(cart);

        return cart;
    }

    @Override
    public Coupon findCouponById(Long id) throws Exception {
        return couponRepository.findById(id)
                .orElseThrow(() -> new Exception("Coupon not found"));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Coupon createCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    @Override
    public List<Coupon> findAllCoupons() {
        return couponRepository.findAll();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCoupon(Long id) throws Exception {
        findCouponById(id);
        couponRepository.deleteById(id);
    }
}