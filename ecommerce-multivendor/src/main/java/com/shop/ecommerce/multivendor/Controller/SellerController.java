package com.shop.ecommerce.multivendor.Controller;

import com.shop.ecommerce.multivendor.Service.AuthService;
import com.shop.ecommerce.multivendor.Service.EmailService;
import com.shop.ecommerce.multivendor.Service.SellerService;
import com.shop.ecommerce.multivendor.Util.OtpUtil;
import com.shop.ecommerce.multivendor.domain.AccountStatus;
import com.shop.ecommerce.multivendor.model.Exceptions.SellerException;
import com.shop.ecommerce.multivendor.model.Seller;
import com.shop.ecommerce.multivendor.model.SellerReport;
import com.shop.ecommerce.multivendor.model.VerificationCode;
import com.shop.ecommerce.multivendor.repository.VerificationCodeRepository;
import com.shop.ecommerce.multivendor.request.LoginRequest;
import com.shop.ecommerce.multivendor.response.ApiResponse;
import com.shop.ecommerce.multivendor.response.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sellers")
public class SellerController {
    private final SellerService sellerService;
    private final VerificationCodeRepository verificationCodeRepository;
    private final AuthService authService;
    private final EmailService emailService;


    @PostMapping("/login")
    public  ResponseEntity<AuthResponse> loginSeller(@RequestBody LoginRequest req) throws Exception {

        String otp = req.getOtp();
        String email = req.getEmail();

        req.setEmail("seller_"+email);
        AuthResponse authResponse = authService.signing(req);

        return ResponseEntity.ok(authResponse);
    }

    @PatchMapping("/verify/{otp}")
    public ResponseEntity<Seller> verifySellerEmail(@PathVariable String otp) throws Exception {
        VerificationCode verificationCode = verificationCodeRepository.findByOtp(otp);

        if(verificationCode==null || !verificationCode.getOtp().equals(otp)){
            throw new Exception("Wrong otp.....");

        }
        Seller seller = sellerService.verifyEmail(verificationCode.getEmail(), otp);
        return new ResponseEntity<>(seller, HttpStatus.OK);


    }

    @PostMapping
    public ResponseEntity<Seller> createSeller(@RequestBody Seller seller) throws Exception {
        Seller savedSeller = sellerService.createSeller(seller);
        String otp = OtpUtil.generateOtp();

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setOtp(otp);
        verificationCode.setEmail(seller.getEmail());
        verificationCodeRepository.save(verificationCode);

        String subject = "Ezcart Email Verification Code";
        String text = "Welcome to Ezcart , verify your account using this link";
         String frontend_url = "http://localhost:3000/verify-seller/";

         emailService.sendVerificationOtpEmail(seller.getEmail() , verificationCode.getOtp() , subject, text + frontend_url);
         return new ResponseEntity<>(savedSeller, HttpStatus.CREATED);

    }

    @GetMapping("/{id}")
    public ResponseEntity<Seller> getSellerById(@PathVariable Long id) throws SellerException {
        Seller seller = sellerService.getSellerById(id);
        return new ResponseEntity<>(seller , HttpStatus.OK);


    }

    @GetMapping("/profile")
    public ResponseEntity<Seller> getSellerByJwt( @RequestHeader("Authorization") String jwt ) throws Exception {
        Seller seller = sellerService.getSellerProfile(jwt);

        return  new ResponseEntity<>(seller , HttpStatus.OK);
    }

//    @GetMapping("/report")
//    public ResponseEntity<SellerReport> getSellerReport(@RequestHeader("Authorization") String jwt) throws Exception {
//        String email =jwtProvider.getEmailFromJwtToken(jwt);
//        Seller seller = sellerService.getSellerByEmail(email);
//        SellerReport report = sellerReportService.getSellerReport(seller);
//
//        return new ResponseEntity<>(report , HttpStatus.OK);
//    }

    @GetMapping
    public ResponseEntity<List<Seller>>getAllSellers(@RequestParam(required = false)AccountStatus status){
        List<Seller> sellers = sellerService.getAllSeller(status);
        return ResponseEntity.ok(sellers);

    }

    @PatchMapping()
    public ResponseEntity<Seller> updateSeller(@RequestHeader("Authorization") String jwt , @RequestBody Seller seller) throws Exception {
        Seller profile = sellerService.getSellerProfile(jwt);
        Seller updatedSeller = sellerService.updateSeller(profile.getId(),seller);
        return ResponseEntity.ok(updatedSeller);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeller(@PathVariable Long id) throws Exception {
        sellerService.deleteSeller(id);
        return ResponseEntity.noContent().build();
    }

}
