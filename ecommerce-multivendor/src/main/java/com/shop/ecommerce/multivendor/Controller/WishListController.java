package com.shop.ecommerce.multivendor.Controller;

import com.shop.ecommerce.multivendor.Service.ProductService;
import com.shop.ecommerce.multivendor.Service.UserService;
import com.shop.ecommerce.multivendor.Service.WishListService;
import com.shop.ecommerce.multivendor.model.Exceptions.ProductException;
import com.shop.ecommerce.multivendor.model.Product;
import com.shop.ecommerce.multivendor.model.User;
import com.shop.ecommerce.multivendor.model.Wishlist;
import com.shop.ecommerce.multivendor.repository.WishListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wishlist")
public class WishListController {
    private final WishListService wishListService;
    private final UserService userService;
    private final ProductService productService;


    @GetMapping()
    public ResponseEntity<Wishlist> getWishlistByUserId(@RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        Wishlist wishlist = wishListService.getWishlistByUserId(user);
        return ResponseEntity.ok(wishlist);

    }
    @PostMapping("/add-product/{productId}")
    public ResponseEntity<Wishlist> addProductToWishList(@PathVariable Long productId , @RequestHeader("Authorization") String jwt) throws Exception {
        Product product =productService.findProductById(productId);
        User user = userService.findUserByJwtToken(jwt);
        Wishlist updatedWishList = wishListService.addProductToWishlist(user,product);

        return ResponseEntity.ok(updatedWishList);

    }
}
