package com.shop.ecommerce.multivendor.Controller;

import com.shop.ecommerce.multivendor.Service.SellerService;
import com.shop.ecommerce.multivendor.domain.AccountStatus;
import com.shop.ecommerce.multivendor.domain.USER_ROLE;
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

    // REGISTER SELLER
    @PostMapping("/register")
    public Seller createSeller(@RequestBody Seller seller) throws Exception {
        return sellerService.createSeller(seller);
    }

    // ===== NEW: LOGIN WITH EMAIL + PASSWORD =====
    @PostMapping("/login-email")
    public ResponseEntity<?> loginWithEmailPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String password = payload.get("password");

        try {
            Map<String, Object> response = sellerService.loginSellerWithEmailPassword(email, password);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllSellers(
            @RequestParam(required = false) AccountStatus status
    ) {
        try {

            if (status == null) {
                return ResponseEntity.ok(sellerService.getAllSellers());
            }

            return ResponseEntity.ok(sellerService.getSellersByStatus(status));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // ===== OPTIONAL: OLD LOGIN WITH OTP =====
    @PostMapping("/login")
    public ResponseEntity<?> loginWithOtp(@RequestBody Map<String, String> payload) {

        String email = payload.get("email");
        String otp = payload.get("otp");

        try {
            Seller seller = sellerService.getSellerByEmail(email);

            // ROLE CHECK
            if (seller.getRole() != USER_ROLE.ROLE_SELLER) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Access denied. Not a seller"));
            }

            // ACCOUNT STATUS CHECK
            if (seller.getAccountStatus() != AccountStatus.ACTIVE) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Seller account not active"));
            }

            boolean valid = sellerService.validateOtp(email, otp);

            if (!valid) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Invalid OTP"));
            }

            String jwt = sellerService.loginSellerWithEmailPassword(email, seller.getPassword())
                    .get("token").toString(); // optional: reuse method to generate token

            return ResponseEntity.ok(
                    Map.of(
                            "jwt", jwt,
                            "role", seller.getRole().name(),
                            "email", seller.getEmail()
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
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
}