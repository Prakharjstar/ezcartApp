package com.shop.ecommerce.multivendor.Controller;

import com.shop.ecommerce.multivendor.Service.UserService;
import com.shop.ecommerce.multivendor.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

      @GetMapping("/api/users/profile")
    public ResponseEntity<User> UserProfileHandler(@RequestHeader("Authorization") String jwt) throws Exception {

      User user = userService.findUserByJwtToken(jwt);
          System.out.println("jwt --" + jwt);

        return ResponseEntity.ok(user);
    }

}
