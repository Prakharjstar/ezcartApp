package com.shop.ecommerce.multivendor.Service.impl;

import com.shop.ecommerce.multivendor.Service.CartItemService;
import com.shop.ecommerce.multivendor.model.CartItem;
import com.shop.ecommerce.multivendor.model.User;
import com.shop.ecommerce.multivendor.repository.CartitemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private final CartitemRepository cartitemRepository;

    @Override
    public CartItem updateCartItem(Long userId, Long id, CartItem cartItem) throws Exception {
        CartItem item = findCartItemById(id);

        User cartItemUser = item.getCart().getUser();

        if(cartItemUser.getId().equals(userId)){
            item.setQuantity( cartItem.getQuantity());
            item.setMrpPrice(item.getQuantity()*item.getProduct().getMrpPrice());
            item.setSellingPrice(item.getQuantity()*item.getProduct().getSellingPrice());
            return cartitemRepository.save(item);
        }
          throw new Exception("You can't update this CartItem");
    }

    @Override
    public void removeCartItem(Long userId, Long cartItemId) throws Exception {

        CartItem item = findCartItemById(cartItemId);

        User cartItemUser = item.getCart().getUser();

        if(cartItemUser.getId().equals(userId)){
            cartitemRepository.delete(item);
        }
        else throw new Exception("you can't delete this item");

    }

    @Override
    public CartItem findCartItemById(Long id) throws Exception {
        return cartitemRepository.findById(id).orElseThrow(()-> new Exception("cart item not found with id " +id));
    }
}
