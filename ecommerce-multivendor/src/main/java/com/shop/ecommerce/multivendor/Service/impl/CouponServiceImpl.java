package com.shop.ecommerce.multivendor.Service.impl;

import com.shop.ecommerce.multivendor.Service.CouponService;
import com.shop.ecommerce.multivendor.model.Cart;
import com.shop.ecommerce.multivendor.model.CartItem;
import com.shop.ecommerce.multivendor.model.Coupon;
import com.shop.ecommerce.multivendor.model.User;
import com.shop.ecommerce.multivendor.repository.CartRepository;
import com.shop.ecommerce.multivendor.repository.CouponRepository;
import com.shop.ecommerce.multivendor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
        Cart cart = cartRepository.findByUserId(user.getId());
        if (cart == null) throw new Exception("Cart not found");

// 🔹 Force load cartItems
        cart.getCartItems().size(); // triggers lazy load if FetchType.LAZY

// 🔹 Recalculate totalSellingPrice
        double totalSellingPrice = 0;
        for (CartItem item : cart.getCartItems()) {
            totalSellingPrice += item.getSellingPrice() * item.getQuantity();
        }
        cart.setTotalSellingPrice(totalSellingPrice);

        double originalPrice = cart.getTotalSellingPrice();

// 🔹 DEBUG
        System.out.println("originalPrice = " + originalPrice);
        System.out.println("couponValue = " + coupon.getDiscountValue());

// Calculate discount
        double discountAmount = 0;
        double value = coupon.getDiscountValue();
        if (value > 0) {
            if (value <= 100) discountAmount = (originalPrice * value) / 100.0;
            else discountAmount = value;
        }

// Optional max discount
        if (coupon.getMaxDiscount() != null && discountAmount > coupon.getMaxDiscount()) {
            discountAmount = coupon.getMaxDiscount();
        }

// Update cart
        cart.setCouponDiscountAmount(discountAmount);
        cart.setFinalPrice(originalPrice - discountAmount);
        cart.setCouponCode(code);

        cartRepository.save(cart);
        return cart;
    }

    @Override
    public Cart removeCoupon(String code, User user) throws Exception {
        Cart cart = cartRepository.findByUserId(user.getId());
        if (cart == null) throw new Exception("Cart not found");

        // Recalculate totals
        double totalSellingPrice = 0;
        for (CartItem item : cart.getCartItems()) {
            totalSellingPrice += item.getSellingPrice() * item.getQuantity();
        }
        cart.setTotalSellingPrice(totalSellingPrice);

        cart.setCouponCode(null);
        cart.setCouponDiscountAmount(0);
        cart.setFinalPrice(totalSellingPrice);

        cartRepository.save(cart);
        return cart;
    }

    @Override
    public Coupon findCouponById(Long id) throws Exception {
        return couponRepository.findById(id)
                .orElseThrow(() -> new Exception("Coupon not found"));
    }

    @Override
    public Coupon createCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    @Override
    public List<Coupon> findAllCoupons() {
        return couponRepository.findAll();
    }

    @Override
    public void deleteCoupon(Long id) throws Exception {
        Coupon coupon = findCouponById(id);
        couponRepository.delete(coupon);
    }
}