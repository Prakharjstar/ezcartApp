package com.shop.ecommerce.multivendor.Service;

import com.shop.ecommerce.multivendor.domain.AccountStatus;
import com.shop.ecommerce.multivendor.model.Exceptions.SellerException;
import com.shop.ecommerce.multivendor.model.Seller;

import java.util.List;

public interface SellerService {

    Seller getSellerProfile(String jwt) throws Exception;
    Seller createSeller(Seller seller) throws Exception;
    Seller getSellerById(Long id) throws SellerException;
    Seller getSellerByEmail(String email) throws Exception;
    List<Seller> getAllSeller(AccountStatus status);
    Seller updateSeller(Long id ,Seller seller) throws Exception;
    void deleteSeller(Long id) throws Exception;
    Seller verifyEmail(String email ,String opt) throws Exception;
    Seller updateSellerAccountStatus(Long sellerId , AccountStatus status) throws Exception;

}
