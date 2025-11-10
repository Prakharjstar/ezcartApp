package com.shop.ecommerce.multivendor.repository;

import com.shop.ecommerce.multivendor.model.Cart;
import com.shop.ecommerce.multivendor.model.CartItem;
import com.shop.ecommerce.multivendor.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartitemRepository extends JpaRepository<CartItem , Long> {
    CartItem findByCartAndProductAndSize(Cart cart , Product product , String size);

}
