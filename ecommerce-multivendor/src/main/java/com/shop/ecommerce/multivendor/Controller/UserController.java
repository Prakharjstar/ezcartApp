package com.shop.ecommerce.multivendor.Controller;

import com.shop.ecommerce.multivendor.Service.UserService;
import com.shop.ecommerce.multivendor.domain.USER_ROLE;
import com.shop.ecommerce.multivendor.model.User;
import com.shop.ecommerce.multivendor.response.AuthResponse;
import com.shop.ecommerce.multivendor.response.SignupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

      @GetMapping("/users/profile")
    public ResponseEntity<User> createUserHandler(@RequestHeader("Authorization") String jwt) throws Exception {

      User user = userService.findUserByJwtToken(jwt);

        return ResponseEntity.ok(user);
    }

}
