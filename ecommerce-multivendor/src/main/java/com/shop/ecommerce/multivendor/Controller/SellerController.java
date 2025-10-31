package com.shop.ecommerce.multivendor.Controller;

import com.shop.ecommerce.multivendor.Service.AuthService;
import com.shop.ecommerce.multivendor.Service.SellerService;
import com.shop.ecommerce.multivendor.model.VerificationCode;
import com.shop.ecommerce.multivendor.repository.VerificationCodeRepository;
import com.shop.ecommerce.multivendor.request.LoginRequest;
import com.shop.ecommerce.multivendor.response.ApiResponse;
import com.shop.ecommerce.multivendor.response.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sellers")
public class SellerController {
    private final SellerService sellerService;
    private final VerificationCodeRepository verificationCodeRepository;
    private final AuthService authService;

//    @PostMapping("/sent/login-otp")
//    public ResponseEntity<ApiResponse> sentOrpHandler(@RequestBody VerificationCode req) throws Exception {
//        authService.sentLoginOtp(req.getEmail() , req.getSeller().getRole());
//        ApiResponse res = new ApiResponse();
//        res.setMessage("otp sent successfully");
//        return ResponseEntity.ok(res);
//    }


    @PostMapping("/login")
    public  ResponseEntity<AuthResponse> loginSeller(@RequestBody LoginRequest req) throws Exception {

        String otp = req.getOtp();
        String email = req.getEmail();

        req.setEmail("seller"+email);
        AuthResponse authResponse = authService.signing(req);

        return ResponseEntity.ok(authResponse);
    }



}
