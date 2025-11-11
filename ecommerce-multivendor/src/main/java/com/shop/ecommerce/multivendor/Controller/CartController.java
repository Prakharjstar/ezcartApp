package com.shop.ecommerce.multivendor.Controller;

import com.shop.ecommerce.multivendor.Service.CartItemService;
import com.shop.ecommerce.multivendor.Service.CartService;
import com.shop.ecommerce.multivendor.Service.ProductService;
import com.shop.ecommerce.multivendor.Service.UserService;
import com.shop.ecommerce.multivendor.model.Cart;
import com.shop.ecommerce.multivendor.model.CartItem;
import com.shop.ecommerce.multivendor.model.Exceptions.ProductException;
import com.shop.ecommerce.multivendor.model.Product;
import com.shop.ecommerce.multivendor.model.User;
import com.shop.ecommerce.multivendor.request.AddItemRequest;
import com.shop.ecommerce.multivendor.response.ApiResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final CartItemService cartItemService;
    private final UserService userService;
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Cart> findUserCartHandler(@RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);

        Cart cart = cartService.findUserCart(user);

        return new ResponseEntity<Cart>(cart, HttpStatus.OK);
    }


    @PutMapping("/add")
    public ResponseEntity<CartItem> addItemToCart(@RequestBody AddItemRequest req, @RequestHeader("Authorization") String jwt) throws ProductException, Exception {
        User user = userService.findUserByJwtToken(jwt);
        Product product = productService.findProductById(req.getProductId());

        CartItem item = cartService.addcartItem(user, product, req.getSize(), req.getQuantity());

        ApiResponse res = new ApiResponse();
        res.setMessage("Items added to Cart Successfully");

        return new ResponseEntity<>(item, HttpStatus.ACCEPTED);

    }

    @DeleteMapping("/item/{cartItemId}")
    public ResponseEntity<ApiResponse> deleteCartItemHandler(@PathVariable Long cartItemId, @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserByJwtToken(jwt);
        cartItemService.removeCartItem(user.getId(), cartItemId);
        ApiResponse res = new ApiResponse();
        res.setMessage("Item Remove From Cart ");
        return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
    }

    @PutMapping("/item/{cartItemId}")
    public ResponseEntity<CartItem> updateCartItemHandler(@PathVariable Long cartItemId , @RequestBody CartItem cartItem , @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserByJwtToken(jwt);

        CartItem updateCartItem = null;
        if(cartItem.getQuantity()>0){
            updateCartItem = cartItemService.updateCartItem(user.getId(), cartItemId ,cartItem);
        }
        return new ResponseEntity<>(updateCartItem,HttpStatus.ACCEPTED);
    }
}