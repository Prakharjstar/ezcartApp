package com.shop.ecommerce.multivendor.Controller;

import com.shop.ecommerce.multivendor.Service.CartService;
import com.shop.ecommerce.multivendor.Service.OrderService;
import com.shop.ecommerce.multivendor.Service.UserService;
import com.shop.ecommerce.multivendor.model.*;

import com.shop.ecommerce.multivendor.response.PaymentLinkResponse;
import jdk.jshell.spi.ExecutionControl;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;
    private final CartService cartService;


    @PostMapping()
    public ResponseEntity<PaymentLinkResponse> createOrderHandler(@RequestBody Address shippingAddress,
                                                                  @RequestParam PaymentMethod paymentMethod,
                                                                  @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        Cart cart = cartService.findUserCart(user);
        Set<Order> orders = orderService.createOrder(user, shippingAddress, cart);
// For payment list .....


        PaymentLinkResponse res = new PaymentLinkResponse();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/users")
    public ResponseEntity<List<Order>> userOrderHistoryHandler(@RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        List<Order> orders = orderService.usersOrderHistory(user.getId());
        return new ResponseEntity<>(orders , HttpStatus.ACCEPTED);

    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId , @RequestHeader("Authorization") String jwt ) throws Exception {

        User user = userService.findUserByJwtToken(jwt);
        Order orders = orderService.findOrderById(orderId);
        return new ResponseEntity<>(orders,HttpStatus.ACCEPTED);
    }


}