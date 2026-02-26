package com.shop.ecommerce.multivendor.model;

import lombok.Data;

@Data
public class UserDto {
    private String name;      // sellerName or customerName
    private String email;
    private String password;
    private String role;      // ROLE_SELLER / ROLE_CUSTOMER
    private String mobile;


}

