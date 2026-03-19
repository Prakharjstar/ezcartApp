package com.shop.ecommerce.multivendor.Service;

import com.shop.ecommerce.multivendor.domain.AccountStatus;
import com.shop.ecommerce.multivendor.model.Exceptions.SellerException;
import com.shop.ecommerce.multivendor.model.Seller;

import java.util.List;
import java.util.Map;

public interface SellerService {

    // ================== PROFILE ==================
    Seller getSellerProfile(String jwt) throws Exception;

    // ================== LOGIN ==================
    Map<String, Object> loginSellerWithEmailPassword(String email, String password) throws Exception;

    // ================== CRUD ==================
    Seller createSeller(Seller seller) throws Exception;
    Seller getSellerById(Long id) throws SellerException;
    Seller getSellerByEmail(String email) throws Exception;
    List<Seller> getAllSellers();


    List<Seller> getSellersByStatus(AccountStatus status);

    List<Seller> getAllSeller(AccountStatus status);

    Seller updateSeller(Long id, Seller seller) throws Exception;
    void deleteSeller(Long id) throws Exception;

    // ================== EMAIL VERIFICATION ==================
    Seller verifyEmail(String email, String otp) throws Exception;

    // ================== ACCOUNT STATUS ==================
    Seller updateSellerAccountStatus(Long sellerId, AccountStatus status) throws Exception;

    // ================== OTP (OPTIONAL) ==================
    void generateAndSendOtp(String email) throws Exception;
    boolean validateOtp(String email, String otp) throws Exception;


}