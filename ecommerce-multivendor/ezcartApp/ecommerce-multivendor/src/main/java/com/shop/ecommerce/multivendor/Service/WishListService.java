package com.shop.ecommerce.multivendor.Service;

import com.shop.ecommerce.multivendor.model.Product;
import com.shop.ecommerce.multivendor.model.User;
import com.shop.ecommerce.multivendor.model.Wishlist;

public interface WishListService {
    Wishlist createWishlist(User user);
    Wishlist getWishlistByUserId(User user);
    Wishlist addProductToWishlist(User user , Product product);
}
