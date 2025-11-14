package com.shop.ecommerce.multivendor.Controller;

import com.shop.ecommerce.multivendor.Service.OrderService;
import com.shop.ecommerce.multivendor.Service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller/orders")
public class SellerOrderController {
    private final OrderService orderService;
    private  final SellerService sellerServicel;
}
