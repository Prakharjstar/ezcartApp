package com.shop.ecommerce.multivendor.Controller;

import com.shop.ecommerce.multivendor.Service.SellerService;
import com.shop.ecommerce.multivendor.domain.AccountStatus;
import com.shop.ecommerce.multivendor.model.Seller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AdminController {

    private final SellerService sellerService;

    @PatchMapping("/seller/{id}/status/{status}")
    public ResponseEntity<Seller> updatedSellerStatus(@PathVariable Long id , @PathVariable AccountStatus status) throws Exception {
        Seller updatedSeller =sellerService.updateSellerAccountStatus(id,status);
        return ResponseEntity.ok(updatedSeller);
    }
}
