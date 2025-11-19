package com.shop.ecommerce.multivendor.repository;

import com.shop.ecommerce.multivendor.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishListRepository extends JpaRepository<Wishlist,Long>{
    Wishlist findUserById(Long  userId);
}
