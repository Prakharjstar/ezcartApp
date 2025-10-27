package com.shop.ecommerce.multivendor.Service;

import com.shop.ecommerce.multivendor.response.SignupRequest;

public interface AuthService {

    void sentLoginOtp(String email) throws Exception;
    String createUser(SignupRequest req) throws Exception;
}
