package com.shop.ecommerce.multivendor.Service;

import com.shop.ecommerce.multivendor.domain.USER_ROLE;
import com.shop.ecommerce.multivendor.request.LoginRequest;
import com.shop.ecommerce.multivendor.response.AuthResponse;
import com.shop.ecommerce.multivendor.response.SignupRequest;
import org.springframework.stereotype.Service;


public interface AuthService {

    void sentLoginOtp(String email , USER_ROLE role) throws Exception;
    String createUser(SignupRequest req) throws Exception;
    AuthResponse signing(LoginRequest req) throws Exception;
}
