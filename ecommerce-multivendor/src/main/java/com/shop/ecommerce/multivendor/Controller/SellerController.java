
package com.shop.ecommerce.multivendor.Controller;

import com.shop.ecommerce.multivendor.Config.JwtProvider;
import com.shop.ecommerce.multivendor.Service.SellerService;
import com.shop.ecommerce.multivendor.model.Seller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sellers")
public class SellerController {

    private final SellerService sellerService;
    private final JwtProvider jwtProvider;

    // REGISTER SELLER
    @PostMapping("/register")
    public Seller createSeller(@RequestBody Seller seller) throws Exception {
        return sellerService.createSeller(seller);
    }

    // SEND OTP
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        try {
            sellerService.generateAndSendOtp(email);
            return ResponseEntity.ok(Map.of("message", "OTP sent successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // LOGIN WITH OTP
    @PostMapping("/login")
    public ResponseEntity<?> loginWithOtp(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String otp = payload.get("otp");

        try {
            Seller seller = sellerService.getSellerByEmail(email);

            boolean valid = sellerService.validateOtp(email, otp);
            if (!valid) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Invalid OTP"));
            }

            String jwt = jwtProvider.generateToken(seller);

            return ResponseEntity.ok(Map.of(
                    "jwt", jwt,
                    "role", seller.getRole().name()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // VERIFY EMAIL (existing)
    @PostMapping("/verify")
    public Seller verifySellerEmail(
            @RequestParam String email,
            @RequestParam String otp
    ) throws Exception {
        return sellerService.verifyEmail(email, otp);
    }

    // GET SELLER PROFILE
    @GetMapping("/profile")
    public ResponseEntity<?> getSellerProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Missing or invalid Authorization header"));
            }
            String jwt = authHeader.substring(7);
            Seller seller = sellerService.getSellerProfile(jwt);
            return ResponseEntity.ok(seller);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // Other CRUD endpoints remain unchanged...
}