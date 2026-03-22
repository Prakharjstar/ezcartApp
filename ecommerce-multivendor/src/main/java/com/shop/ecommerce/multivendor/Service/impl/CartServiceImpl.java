package com.shop.ecommerce.multivendor.Service.impl;

import com.shop.ecommerce.multivendor.Service.CartService;
import com.shop.ecommerce.multivendor.model.Cart;
import com.shop.ecommerce.multivendor.model.CartItem;
import com.shop.ecommerce.multivendor.model.Product;
import com.shop.ecommerce.multivendor.model.User;
import com.shop.ecommerce.multivendor.repository.CartRepository;
import com.shop.ecommerce.multivendor.repository.CartitemRepository;
import com.shop.ecommerce.multivendor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartitemRepository cartitemRepository;
    private final UserRepository userRepository;

    @Override
    public CartItem addcartItem(User user, Product product, String size, int quantity) {
        Cart cart = findUserCart(user);

        CartItem existingItem = cartitemRepository.findByCartAndProductAndSize(cart, product, size);

        if (existingItem == null) {
            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setSize(size);
            cartItem.setUserId(user.getId());
            cartItem.setSellingPrice(product.getSellingPrice() * quantity);
            cartItem.setMrpPrice(product.getMrpPrice() * quantity);

            cart.getCartItems().add(cartItem);
            cartItem.setCart(cart);

            cartitemRepository.save(cartItem);
        } else {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.setSellingPrice(existingItem.getQuantity() * product.getSellingPrice());
            existingItem.setMrpPrice(existingItem.getQuantity() * product.getMrpPrice());
            cartitemRepository.save(existingItem);
        }

        // Recalculate totals without overwriting coupon discount
        return recalcCart(cart).getCartItems().stream()
                .filter(i -> i.getProduct().getId().equals(product.getId()) && i.getSize().equals(size))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Cart findUserCart(User user) {
        Cart cart = cartRepository.findByUserId(user.getId());
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart.setCartItems(new ArrayList<>());
            cart.setTotalItem(0);
            cart.setTotalMrpPrice(0);
            cart.setTotalSellingPrice(0);
            cart.setProductDiscountAmount(0);
            cart.setCouponDiscountAmount(0);
            cart.setFinalPrice(0);
            cartRepository.save(cart);
        }

        return recalcCart(cart);
    }

    @Override
    public Cart getCartByUserId(Long id) {
        // Fetch the cart by user id from the repository
        Cart cart = cartRepository.findByUserId(id);

        if (cart == null) {
            // Optional: create a new cart if it doesn't exist
            cart = new Cart();
            cart.setUser(userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found")));
            cart.setCartItems(new ArrayList<>());
            cart.setTotalItem(0);
            cart.setTotalMrpPrice(0);
            cart.setTotalSellingPrice(0);
            cart.setFinalPrice(0);
            cartRepository.save(cart);
        }

        // Ensure totalSellingPrice is correct
        double totalSellingPrice = 0;
        double totalMrpPrice = 0;
        int totalItem = 0;

        for (var item : cart.getCartItems()) {
            totalSellingPrice += item.getSellingPrice() * item.getQuantity();
            totalMrpPrice += item.getMrpPrice() * item.getQuantity();
            totalItem += item.getQuantity();
        }

        cart.setTotalSellingPrice(totalSellingPrice);
        cart.setTotalMrpPrice(totalMrpPrice);
        cart.setTotalItem(totalItem);

        // Ensure finalPrice is at least equal to totalSellingPrice if no coupon
        if (cart.getFinalPrice() <= 0) {
            cart.setFinalPrice(totalSellingPrice);
        }

        return cart;
    }

    // Recalculate totals
    private Cart recalcCart(Cart cart) {
        double totalMrp = 0;
        double totalSelling = 0;
        int totalItems = 0;

        List<CartItem> items = cart.getCartItems() != null ? cart.getCartItems() : new ArrayList<>();

        for (CartItem item : items) {
            totalMrp += item.getMrpPrice();
            totalSelling += item.getSellingPrice();
            totalItems += item.getQuantity();
        }

        cart.setTotalMrpPrice(totalMrp);
        cart.setTotalSellingPrice(totalSelling);
        cart.setProductDiscountAmount(totalMrp - totalSelling);

        // Keep coupon discount as-is; final price considers coupon
        cart.setFinalPrice(totalSelling - cart.getCouponDiscountAmount());
        cart.setTotalItem(totalItems);

        cartRepository.save(cart);
        return cart;
    }
}