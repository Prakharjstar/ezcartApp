package com.shop.ecommerce.multivendor.Controller;

import com.shop.ecommerce.multivendor.Service.OrderService;
import com.shop.ecommerce.multivendor.Service.SellerService;
import com.shop.ecommerce.multivendor.domain.OrderStatus;
import com.shop.ecommerce.multivendor.model.Order;
import com.shop.ecommerce.multivendor.model.Seller;
import com.shop.ecommerce.multivendor.model.SellerReport;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller/orders")
public class SellerOrderController {
    private final OrderService orderService;
    private  final SellerService sellerService;

    @GetMapping()
    public ResponseEntity<List<Order>> getAllOrderHandler(@RequestHeader("Authorization") String jwt) throws Exception {
        Seller seller = sellerService.getSellerProfile(jwt);
        List<Order> orders = orderService.sellersOrder(seller.getId());
        return new ResponseEntity<>(orders , HttpStatus.ACCEPTED);
            }

            @PatchMapping("/{orderId}/status/{orderStatus}")
            public ResponseEntity<Order> updateOrderHandler(@RequestHeader("Authorization") String jwt, @PathVariable Long orderId , @PathVariable OrderStatus orderStatus) throws Exception {

            Order orders = orderService.updateOrderStatus(orderId , orderStatus);
          return new ResponseEntity<>(orders,HttpStatus.ACCEPTED);
            }
}
