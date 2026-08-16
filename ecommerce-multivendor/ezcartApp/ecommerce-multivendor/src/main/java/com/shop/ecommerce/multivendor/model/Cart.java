package com.shop.ecommerce.multivendor.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    private List<CartItem> cartItems;

    private double totalMrpPrice;          // Total of all MRP
    private double totalSellingPrice;      // Total of all selling prices
    private double productDiscountAmount;  // Product discount only
    private double couponDiscountAmount;   // Coupon discount only
    private double finalPrice;             // totalSellingPrice - couponDiscountAmount
    private String couponCode;
    private int totalItem;



}