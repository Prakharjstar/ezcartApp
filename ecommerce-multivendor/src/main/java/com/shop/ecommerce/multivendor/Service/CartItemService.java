package com.shop.ecommerce.multivendor.Service;

import com.shop.ecommerce.multivendor.model.CartItem;

public interface CartItemService {
    CartItem updateCartItem(Long userId , Long id , CartItem cartItem) throws Exception;
    void removeCartItem(Long userId , Long cartItemId) throws Exception;
    CartItem findCartItemById(Long id) throws Exception;
}
