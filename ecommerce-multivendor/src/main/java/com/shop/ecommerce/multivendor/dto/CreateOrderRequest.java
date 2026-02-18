package com.shop.ecommerce.multivendor.dto;

import com.shop.ecommerce.multivendor.model.Address;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderRequest {
    private Address shippingAddress;
    private String paymentMethod; // "RAZORPAY" or "STRIPE"
}