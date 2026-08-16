package com.shop.ecommerce.multivendor.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

   @NotNull
    @JoinColumn(unique = true)
    private String categoryId;
    @ManyToOne
    private Category parentCategory;

    @NotNull
    private Integer level;
}
