package com.shop.ecommerce.multivendor.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToMany
    private User customer;
    @OneToOne
     private Order order;

    @ManyToOne
     private Seller seller;

    private LocalDateTime date= LocalDateTime.now();
}
