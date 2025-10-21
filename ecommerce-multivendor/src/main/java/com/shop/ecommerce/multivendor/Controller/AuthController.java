package com.shop.ecommerce.multivendor.Controller;


import com.shop.ecommerce.multivendor.model.User;
import com.shop.ecommerce.multivendor.response.SignupRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/signup")
    public ResponseEntity<User> createUserHandler(@RequestBody SignupRequest req){
        User user = new User();
        user.setEmail(req.getEmail());
        user.setFullName(req.getFullName());

        return ResponseEntity.ok(user);
    }
}
