package com.shop.ecommerce.multivendor.Service;

import com.shop.ecommerce.multivendor.request.LoginRequest;
import com.shop.ecommerce.multivendor.response.AuthResponse;
import com.shop.ecommerce.multivendor.response.SignupRequest;

public interface AuthService {

    void sentLoginOtp(String email) throws Exception;

    AuthResponse createUser(SignupRequest req) throws Exception;

    AuthResponse signing(LoginRequest req) throws Exception;
}