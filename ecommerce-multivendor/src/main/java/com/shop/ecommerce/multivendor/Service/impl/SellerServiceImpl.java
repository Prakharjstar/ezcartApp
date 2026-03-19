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

    // ====================== CREATE SELLER ======================
    @Override
    public Seller createSeller(Seller seller) throws Exception {
        Seller existing = sellerRepository.findByEmail(seller.getEmail());
        if (existing != null) throw new Exception("Seller already exists");

        Address savedAddress = addressRepository.save(seller.getPickupAddress());

        seller.setPassword(passwordEncoder.encode(seller.getPassword()));
        seller.setPickupAddress(savedAddress);
        seller.setRole(USER_ROLE.ROLE_SELLER);
        seller.setAccountStatus(AccountStatus.ACTIVE);

        return sellerRepository.save(seller);
    }

    // ====================== GET SELLER ======================
    @Override
    public Seller getSellerById(Long id) throws SellerException {
        return sellerRepository.findById(id)
                .orElseThrow(() -> new SellerException("Seller not found with id: " + id));
    }

    @Override
    public Seller getSellerByEmail(String email) throws Exception {
        Seller seller = sellerRepository.findByEmail(email);
        if (seller == null) throw new Exception("Seller not found with email: " + email);
        return seller;
    }

    @Override
    public List<Seller> getAllSeller(AccountStatus status) {
        if (status != null) {
            return sellerRepository.findByAccountStatus(status);
        }
        return sellerRepository.findAll();
    }

    // ====================== UPDATE SELLER ======================
    @Override
    public Seller updateSeller(Long id, Seller seller) throws Exception {
        Seller existing = getSellerById(id);

        existing.setSellerName(seller.getSellerName());
        existing.setMobile(seller.getMobile());
        existing.setGstin(seller.getGstin());
        existing.setBankDetails(seller.getBankDetails());
        existing.setBusinessDetails(seller.getBusinessDetails());

        if (seller.getPickupAddress() != null) {
            Address savedAddress = addressRepository.save(seller.getPickupAddress());
            existing.setPickupAddress(savedAddress);
        }

        return sellerRepository.save(existing);
    }

    public void hashExistingPasswords() {
        List<Seller> sellers = sellerRepository.findAll();
        for (Seller seller : sellers) {
            String plain = seller.getPassword();

            // Only hash if it is not already BCrypt
            if (!plain.startsWith("$2a$")) {
                seller.setPassword(passwordEncoder.encode(plain));
            }
        }
        sellerRepository.saveAll(sellers);
        System.out.println("All existing passwords hashed!");
    }

    // ====================== DELETE SELLER ======================
    @Override
    public void deleteSeller(Long id) throws Exception {
        Seller existing = getSellerById(id);
        sellerRepository.delete(existing);
    }

    // ====================== EMAIL VERIFICATION ======================
    @Override
    public Seller verifyEmail(String email, String otp) throws Exception {
        if (!validateOtp(email, otp)) throw new Exception("OTP verification failed");
        return getSellerByEmail(email);
    }

    // ====================== ACCOUNT STATUS ======================
    @Override
    public Seller updateSellerAccountStatus(Long sellerId, AccountStatus status) throws Exception {
        Seller seller = getSellerById(sellerId);
        seller.setAccountStatus(status);
        return sellerRepository.save(seller);
    }

    // ====================== PROFILE ======================
    @Override
    public Seller getSellerProfile(String jwt) throws Exception {
        String email = jwtProvider.getEmailFromJwtToken(jwt);
        return getSellerByEmail(email);
    }

    // ====================== LOGIN (EMAIL + PASSWORD) ======================
    @Override
    public Map<String, Object> loginSellerWithEmailPassword(String email, String password) throws Exception {
        Seller seller = sellerRepository.findByEmail(email);
        if (seller == null) throw new SellerException("Seller not found");

        if (seller.getRole() != USER_ROLE.ROLE_SELLER) throw new SellerException("Access denied");
        if (seller.getAccountStatus() != AccountStatus.ACTIVE) throw new SellerException("Account not active");

        if (!passwordEncoder.matches(password, seller.getPassword())) throw new SellerException("Invalid password");

        String token = jwtProvider.generateToken(seller);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("email", seller.getEmail());
        response.put("role", seller.getRole());
        response.put("sellerName", seller.getSellerName());

        return response;
    }

    @Override
    public List<Seller> getAllSellers() {
        return sellerRepository.findAll();
    }

    @Override
    public List<Seller> getSellersByStatus(AccountStatus status) {
        return sellerRepository.findByAccountStatus(status);
    }

    // ====================== OTP ======================
    @Override
    public void generateAndSendOtp(String email) throws Exception {
        Seller seller = getSellerByEmail(email);

        if (seller.getRole() != USER_ROLE.ROLE_SELLER) throw new Exception("Access denied");
        if (seller.getAccountStatus() != AccountStatus.ACTIVE) throw new Exception("Account not active");

        String otp = String.format("%06d", random.nextInt(999999));
        otpStore.put(email, new OtpEntry(otp, LocalDateTime.now().plusMinutes(5)));

        System.out.println("OTP for " + email + ": " + otp);
    }

    @Override
    public boolean validateOtp(String email, String otp) throws Exception {
        OtpEntry entry = otpStore.get(email);
        if (entry == null) throw new Exception("No OTP found for email");
        if (LocalDateTime.now().isAfter(entry.expiresAt)) {
            otpStore.remove(email);
            throw new Exception("OTP expired");
        }
        if (!entry.otp.equals(otp)) throw new Exception("Invalid OTP");

        otpStore.remove(email);
        return true;
    }
}