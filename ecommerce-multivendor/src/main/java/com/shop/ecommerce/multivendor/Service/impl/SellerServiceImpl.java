package com.shop.ecommerce.multivendor.Service.impl;

import com.shop.ecommerce.multivendor.Config.JwtProvider;
import com.shop.ecommerce.multivendor.Service.SellerService;
import com.shop.ecommerce.multivendor.domain.AccountStatus;
import com.shop.ecommerce.multivendor.domain.USER_ROLE;
import com.shop.ecommerce.multivendor.model.Address;
import com.shop.ecommerce.multivendor.model.Seller;
import com.shop.ecommerce.multivendor.repository.AddressRepository;
import com.shop.ecommerce.multivendor.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SellerServiceImpl implements SellerService {

    private final SellerRepository sellerRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final AddressRepository addressRepository;

    @Override
    public Seller getSellerProfile(String jwt) throws Exception {
        String email = jwtProvider.getEmailFromJwtToken(jwt);
        return this.getSellerByEmail(email);
    }

    @Override
    public Seller createSeller(Seller seller) throws Exception {
        Seller sellerExists = sellerRepository.findByEmail(seller.getEmail());
        if(sellerExists!=null){
            throw new Exception("Seller already exist, use different email");
        }
        Address savedAddress = addressRepository.save(seller.getPickupAddress());

        Seller newSeller = new Seller();
        newSeller.setEmail(seller.getEmail());
        newSeller.setPassword(seller.getPassword());
        newSeller.setSellerName(seller.getSellerName());
        newSeller.setPickupAddress(savedAddress);
        newSeller.setGSTIN(seller.getGSTIN());
        newSeller.setRole(USER_ROLE.ROLE_SELLER);
        newSeller.setMobile(seller.getMobile());
        newSeller.setBankDetails(seller.getBankDetails());
        newSeller.setBusinessDetails(seller.getBusinessDetails());
        return sellerRepository.save(newSeller);
    }

    @Override
    public Seller getSellerById(Long id) throws Exception {
        return sellerRepository.findById(id).orElseThrow(()-> new Exception("seller not found with id " + id));
    }

    @Override
    public Seller getSellerByEmail(String email) throws Exception {
        Seller seller = sellerRepository.findByEmail(email);
        if(seller==null){
            throw new Exception("Seller not Found");
        }
        return seller;
    }

    @Override
    public List<Seller> getAllSeller(AccountStatus status) {
        return sellerRepository.findByAccountStatus(status);
    }

    @Override
    public Seller updateSeller(Long id, Seller seller) {
        Seller existingSeller = sellerRepository.findById(id).orElseThrow(()-> new SellerException("Seller not  found with id " + id));

        if(seller.getSellerName()!=null){
            existingSeller.setSellerName(seller.getSellerName());
        }
        if(seller.getMobile() != null){
            existingSeller.setMobile(seller.getMobile());
        }
        if(seller.getEmail() !=null){
            existingSeller.setEmail(seller.getEmail());
        }
        if(seller.getBusinessDetails() !=null && seller.getBusinessDetails().getBusinessName() !=null){
            existingSeller.getBusinessDetails().setBusinessName(seller.getBusinessDetails().getBusinessName());

        }


        return null;
    }

    @Override
    public void deleteSeller(Long id) {

    }

    @Override
    public Seller verifyEmail(String email, String opt) {
        return null;
    }

    @Override
    public Seller updateSellerAccountStatus(Long sellerId, AccountStatus status) {
        return null;
    }
}
