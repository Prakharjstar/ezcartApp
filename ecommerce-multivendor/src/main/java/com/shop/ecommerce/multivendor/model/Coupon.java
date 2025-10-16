package com.shop.ecommerce.multivendor.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;
import java.util.HashSet;


import java.time.LocalDate;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String code;

    private double discountPercentage;

    private LocalDate validityStartDate;

    private LocalDate validityEndDate;

    private double minimumOrderValue;

    private boolean isActive= true;

    @ManyToMany(mappedBy = "usedCoupons")  //it will not create extra table in database
    private Set<User> usedByUsers = new HashSet<>();


}
