package com.shop.ecommerce.multivendor.Controller;

import com.shop.ecommerce.multivendor.Service.SellerService;
import com.shop.ecommerce.multivendor.domain.AccountStatus;
import com.shop.ecommerce.multivendor.model.Seller;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sellers")
public class SellerController {

    private final SellerService sellerService;

    // CREATE SELLER
    @PostMapping("/register")
    public Seller createSeller(@RequestBody Seller seller) throws Exception {
        return sellerService.createSeller(seller);
    }

    // VERIFY EMAIL
    @PostMapping("/verify")
    public Seller verifySellerEmail(
            @RequestParam String email,
            @RequestParam String otp
    ) throws Exception {

        return sellerService.verifyEmail(email, otp);
    }

    // GET SELLER PROFILE
    @GetMapping("/profile")
    public Seller getSellerProfile(@RequestHeader("Authorization") String jwt) throws Exception {

        return sellerService.getSellerProfile(jwt);
    }

    // GET SELLER BY ID
    @GetMapping("/{id}")
    public Seller getSellerById(@PathVariable Long id) throws Exception {

        return sellerService.getSellerById(id);
    }

    // GET ALL SELLERS BY STATUS
    @GetMapping
    public List<Seller> getAllSellers(@RequestParam AccountStatus status) {

        return sellerService.getAllSeller(status);
    }

    // UPDATE SELLER
    @PutMapping("/{id}")
    public Seller updateSeller(
            @PathVariable Long id,
            @RequestBody Seller seller
    ) throws Exception {

        return sellerService.updateSeller(id, seller);
    }

    // DELETE SELLER
    @DeleteMapping("/{id}")
    public String deleteSeller(@PathVariable Long id) throws Exception {

        sellerService.deleteSeller(id);
        return "Seller deleted successfully";
    }

    // UPDATE ACCOUNT STATUS
    @PutMapping("/{id}/status")
    public Seller updateSellerStatus(
            @PathVariable Long id,
            @RequestParam AccountStatus status
    ) throws Exception {

        return sellerService.updateSellerAccountStatus(id, status);
    }
}