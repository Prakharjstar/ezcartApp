package com.shop.ecommerce.multivendor.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    // Type of discount: PERCENTAGE or FIXED
    @Enumerated(EnumType.STRING)
    private DiscountType discountType = DiscountType.PERCENTAGE;

    // Value of discount
    private double discountValue;

    // Maximum discount applicable (optional, null if no cap)
    private Double maxDiscount;

    private double minimumOrderValue;

    private LocalDate validityStartDate;

    private LocalDate validityEndDate;

    private boolean isActive = true;

    // Users who have used this coupon
    @ManyToMany(mappedBy = "usedCoupons")
    private Set<User> usedByUsers = new HashSet<>();

    // Enum for discount type
    public enum DiscountType {
        PERCENTAGE,
        FIXED
    }
}