package com.shop.ecommerce.multivendor.Service;


import com.shop.ecommerce.multivendor.model.Cart;
import com.shop.ecommerce.multivendor.model.CartItem;
import com.shop.ecommerce.multivendor.model.Product;
import com.shop.ecommerce.multivendor.model.User;

public interface CartService {
    public CartItem addcartItem(User user , Product product ,String size, int quantity);

    public Cart findUserCart(User user);




}
