package com.shop.ecommerce.multivendor.request;

import com.shop.ecommerce.multivendor.domain.USER_ROLE;
import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String otp;
    private USER_ROLE role;
}
