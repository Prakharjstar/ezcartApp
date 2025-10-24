package com.shop.ecommerce.multivendor.Service;

import com.shop.ecommerce.multivendor.response.SignupRequest;

public interface AuthService {
    String createUser(SignupRequest req);
}
