package com.shop.ecommerce.multivendor.Controller;


import com.shop.ecommerce.multivendor.Repository.UserRepository;
import com.shop.ecommerce.multivendor.Service.AuthService;
import com.shop.ecommerce.multivendor.domain.USER_ROLE;
import com.shop.ecommerce.multivendor.model.User;
import com.shop.ecommerce.multivendor.response.AuthResponse;
import com.shop.ecommerce.multivendor.response.SignupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody SignupRequest req) throws Exception {
       String jwt = authService.createUser(req);
        AuthResponse res = new AuthResponse();
        res.setJwt(jwt);
        res.setMessage("register success");
        res.setRole(USER_ROLE.ROLE_CUSTOMER);


        return ResponseEntity.ok(res);
    }
}
