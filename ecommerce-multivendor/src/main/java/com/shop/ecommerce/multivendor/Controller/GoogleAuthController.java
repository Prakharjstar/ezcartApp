package com.shop.ecommerce.multivendor.Controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;

import com.shop.ecommerce.multivendor.Config.JwtProvider;
import com.shop.ecommerce.multivendor.model.User;
import com.shop.ecommerce.multivendor.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@RestController
@RequestMapping("/auth")
public class GoogleAuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtProvider jwtProvider;

    private static final String CLIENT_ID = "607479622858-j1d0iu2vabjnc9pb3gpuddkqg39solt5.apps.googleusercontent.com";

    @PostMapping("/google")
    public Map<String,String> googleLogin(@RequestBody Map<String,String> body) throws Exception {

        String idTokenString = body.get("accessToken");

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                JacksonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);

        if(idToken == null){
            throw new Exception("Invalid Google Token");
        }

        GoogleIdToken.Payload payload = idToken.getPayload();

        String email = payload.getEmail();
        String name = (String) payload.get("name");

        User user = userRepository.findByEmail(email);

        if(user == null){
            user = new User();
            user.setEmail(email);
            user.setFullName(name);
            userRepository.save(user);
        }

        String jwt = jwtProvider.generateTokenFromEmail(email);

        Map<String,String> result = new HashMap<>();
        result.put("jwt", jwt);

        return result;
    }
}