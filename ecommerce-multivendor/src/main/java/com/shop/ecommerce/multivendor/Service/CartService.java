package com.shop.ecommerce.multivendor.Service;

import com.shop.ecommerce.multivendor.model.Cart;
import com.shop.ecommerce.multivendor.model.CartItem;
import com.shop.ecommerce.multivendor.model.Product;
import com.shop.ecommerce.multivendor.model.User;

public interface CartService {

    // Add or update an item in the cart
    CartItem addcartItem(User user, Product product, String size, int quantity);

    Cart findUserCart(User user);

    Cart getCartByUserId(Long id);

    // Optional: remove an item from cart

}