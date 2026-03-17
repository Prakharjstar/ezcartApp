package com.shop.ecommerce.multivendor.Service.impl;

import com.shop.ecommerce.multivendor.Config.JwtProvider;
import com.shop.ecommerce.multivendor.Service.SellerService;
import com.shop.ecommerce.multivendor.domain.AccountStatus;
import com.shop.ecommerce.multivendor.domain.USER_ROLE;
import com.shop.ecommerce.multivendor.model.Address;
import com.shop.ecommerce.multivendor.model.Exceptions.SellerException;
import com.shop.ecommerce.multivendor.model.Seller;
import com.shop.ecommerce.multivendor.repository.AddressRepository;
import com.shop.ecommerce.multivendor.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SellerServiceImpl implements SellerService {

    private final SellerRepository sellerRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final AddressRepository addressRepository;

    // 🔹 OTP STORAGE (email -> OTP + expiry)
    private final Map<String, OtpEntry> otpStore = new HashMap<>();
    private final Random random = new Random();

    private static class OtpEntry {
        String otp;
        LocalDateTime expiresAt;
        OtpEntry(String otp, LocalDateTime expiresAt) {
            this.otp = otp;
            this.expiresAt = expiresAt;
        }
    }

    @Override
    public Seller getSellerProfile(String jwt) throws Exception {
        String email = jwtProvider.getEmailFromJwtToken(jwt);
        return this.getSellerByEmail(email);
    }

    @Override
    public Seller createSeller(Seller seller) throws Exception {
        Seller sellerExists = sellerRepository.findByEmail(seller.getEmail());
        if (sellerExists != null) {
            throw new Exception("Seller already exists, use different email");
        }
        Address savedAddress = addressRepository.save(seller.getPickupAddress());

        Seller newSeller = new Seller();
        newSeller.setEmail(seller.getEmail());
        newSeller.setPassword(seller.getPassword());
        newSeller.setSellerName(seller.getSellerName());
        newSeller.setPickupAddress(savedAddress);
        newSeller.setGstin(seller.getGstin());
        newSeller.setRole(USER_ROLE.ROLE_SELLER);
        newSeller.setMobile(seller.getMobile());
        newSeller.setBankDetails(seller.getBankDetails());
        newSeller.setBusinessDetails(seller.getBusinessDetails());
        return sellerRepository.save(newSeller);
    }

    @Override
    public Seller getSellerById(Long id) throws SellerException {
        return sellerRepository.findById(id)
                .orElseThrow(() -> new SellerException("Seller not found with id " + id));
    }

    @Override
    public Seller getSellerByEmail(String email) throws Exception {
        Seller seller = sellerRepository.findByEmail(email);
        if (seller == null) {
            throw new Exception("Seller not found");
        }
        return seller;
    }

    @Override
    public List<Seller> getAllSeller(AccountStatus status) {
        return sellerRepository.findByAccountStatus(status);
    }

    @Override
    public Seller updateSeller(Long id, Seller seller) throws Exception {
        Seller existingSeller = this.getSellerById(id);

        if (seller.getSellerName() != null) existingSeller.setSellerName(seller.getSellerName());
        if (seller.getMobile() != null) existingSeller.setMobile(seller.getMobile());
        if (seller.getEmail() != null) existingSeller.setEmail(seller.getEmail());

        if (seller.getBusinessDetails() != null && seller.getBusinessDetails().getBusinessName() != null)
            existingSeller.getBusinessDetails().setBusinessName(seller.getBusinessDetails().getBusinessName());

        if (seller.getBankDetails() != null &&
                seller.getBankDetails().getAccountHolderName() != null &&
                seller.getBankDetails().getAccountNumber() != null &&
                seller.getBankDetails().getIfscCode() != null) {

            existingSeller.getBankDetails().setAccountHolderName(seller.getBankDetails().getAccountHolderName());
            existingSeller.getBankDetails().setAccountNumber(seller.getBankDetails().getAccountNumber());
            existingSeller.getBankDetails().setIfscCode(seller.getBankDetails().getIfscCode());
        }

        if (seller.getPickupAddress() != null &&
                seller.getPickupAddress().getAddress() != null &&
                seller.getPickupAddress().getMobile() != null) {

            existingSeller.getPickupAddress().setAddress(seller.getPickupAddress().getAddress());
            existingSeller.getPickupAddress().setCity(seller.getPickupAddress().getCity());
            existingSeller.getPickupAddress().setState(seller.getPickupAddress().getState());
            existingSeller.getPickupAddress().setMobile(seller.getPickupAddress().getMobile());
            existingSeller.getPickupAddress().setPinCode(seller.getPickupAddress().getPinCode());
        }

        if (seller.getGstin() != null) existingSeller.setGstin(seller.getGstin());

        return sellerRepository.save(existingSeller);
    }

    @Override
    public void deleteSeller(Long id) throws Exception {
        Seller seller = getSellerById(id);
        sellerRepository.delete(seller);
    }

    @Override
    public Seller verifyEmail(String email, String otp) throws Exception {
        Seller seller = getSellerByEmail(email);
        seller.setEmailVerified(true);
        return sellerRepository.save(seller);
    }

    @Override
    public Seller updateSellerAccountStatus(Long sellerId, AccountStatus status) throws Exception {
        Seller seller = getSellerById(sellerId);
        seller.setAccountStatus(status);
        return sellerRepository.save(seller);
    }

    // 🔹 OTP METHODS
    @Override
    public void generateAndSendOtp(String email) throws Exception {
        Seller seller = sellerRepository.findByEmail(email);
        if (seller == null) throw new Exception("Seller not found");

        String otp = String.format("%06d", random.nextInt(999999));
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5);

        otpStore.put(email, new OtpEntry(otp, expiresAt));

        // 🔹 Replace with email sending in production
        System.out.println("OTP for " + email + " is: " + otp);
    }

    @Override
    public boolean validateOtp(String email, String otp) throws Exception {
        OtpEntry entry = otpStore.get(email);
        if (entry == null) throw new Exception("No OTP sent for this email");

        if (LocalDateTime.now().isAfter(entry.expiresAt)) {
            otpStore.remove(email);
            throw new Exception("OTP expired");
        }

        if (!entry.otp.equals(otp)) throw new Exception("Invalid OTP");

        otpStore.remove(email);
        return true;
    }
}