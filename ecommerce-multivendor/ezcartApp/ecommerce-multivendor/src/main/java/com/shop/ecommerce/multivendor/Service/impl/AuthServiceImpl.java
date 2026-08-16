package com.shop.ecommerce.multivendor.Service.impl;

import com.shop.ecommerce.multivendor.Config.JwtProvider;
import com.shop.ecommerce.multivendor.Service.AuthService;
import com.shop.ecommerce.multivendor.Service.EmailService;
import com.shop.ecommerce.multivendor.Util.OtpUtil;
import com.shop.ecommerce.multivendor.domain.USER_ROLE;
import com.shop.ecommerce.multivendor.model.*;
import com.shop.ecommerce.multivendor.repository.*;
import com.shop.ecommerce.multivendor.request.LoginRequest;
import com.shop.ecommerce.multivendor.response.AuthResponse;
import com.shop.ecommerce.multivendor.response.SignupRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final SellerRepository sellerRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final EmailService emailService;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserServiceImpl customUserService;

    private static final String SELLER_PREFIX = "seller_";
    private static final String ADMIN_EMAIL = "prakharj1231@gmail.com";

    // ================================
    // 🔹 SEND OTP
    // ================================
    @Override
    public void sentLoginOtp(String email) throws Exception {

        // Role check
        if (userRepository.findByEmail(email) != null) {
            System.out.println("Customer login OTP");
        } else if (sellerRepository.findByEmailIgnoreCase(email) != null) {
            System.out.println("Seller login OTP");
        } else {
            throw new Exception("User/Seller not found: " + email);
        }

        // Remove old OTP
        VerificationCode existing = verificationCodeRepository.findByEmail(email);
        if (existing != null) {
            verificationCodeRepository.delete(existing);
        }

        // Generate new OTP
        String otp = OtpUtil.generateOtp();

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setEmail(email);
        verificationCode.setOtp(otp);
        verificationCodeRepository.save(verificationCode);

        // Send email + console
        emailService.sendVerificationOtpEmail(email, otp);

        System.out.println("OTP generated and sent to email & console: " + otp);
    }
    // ================================
    // 🔹 CREATE USER (SIGNUP)
    // ================================
    @Override
    public AuthResponse createUser(SignupRequest req) throws Exception {

        // Validate OTP
        VerificationCode verificationCode = verificationCodeRepository.findByEmail(req.getEmail());
        if (verificationCode == null || !verificationCode.getOtp().equals(req.getOtp())) {
            throw new Exception("Invalid OTP");
        }

        // Check if user exists
        if (userRepository.findByEmail(req.getEmail()) != null) {
            throw new Exception("User already exists");
        }

        // Create user
        User newUser = new User();
        newUser.setEmail(req.getEmail());
        newUser.setFullName(req.getFullName());
        newUser.setRole(USER_ROLE.ROLE_CUSTOMER);
        newUser.setPassword(passwordEncoder.encode("DEFAULT_PASS")); // default password

        User savedUser = userRepository.save(newUser);

        // Create cart
        Cart cart = new Cart();
        cart.setUser(savedUser);
        cartRepository.save(cart);

        // Delete OTP
        verificationCodeRepository.delete(verificationCode);

        // Build response
        AuthResponse response = new AuthResponse();
        response.setMessage("Register Success");
        response.setRole(savedUser.getRole());
        response.setUser(savedUser);
        response.setJwt(null);

        return response;
    }

    // ================================
    // 🔹 LOGIN (OTP)
    // ================================
    @Override
    public AuthResponse signing(LoginRequest req) throws Exception {

        String username = req.getEmail();
        USER_ROLE role;

        // Admin login
        if (username.equals(ADMIN_EMAIL)) {
            role = USER_ROLE.ROLE_ADMIN;
            Authentication authentication = authenticateAdmin(req.getOtp());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return buildAuthResponse(authentication, role);
        }

        // Customer or Seller login
        User user = userRepository.findByEmail(username);
        if (user != null) {
            role = USER_ROLE.ROLE_CUSTOMER;
        } else {
            Seller seller = sellerRepository.findByEmailIgnoreCase(username);
            if (seller != null) {
                role = USER_ROLE.ROLE_SELLER;
                username = SELLER_PREFIX + username; // add prefix for userDetails
            } else {
                throw new BadCredentialsException("User not found");
            }
        }

        Authentication authentication = authenticate(username, req.getOtp());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return buildAuthResponse(authentication, role);
    }

    // ================================
    // 🔹 AUTHENTICATION HELPERS
    // ================================

    private Authentication authenticate(String username, String otp) throws Exception {

        String emailForOtp = username.startsWith(SELLER_PREFIX)
                ? username.substring(SELLER_PREFIX.length())
                : username;

        VerificationCode verificationCode =
                verificationCodeRepository.findByEmail(emailForOtp);

        if (verificationCode == null || !verificationCode.getOtp().equals(otp)) {
            throw new BadCredentialsException("No OTP found for this email");
        }


        verificationCodeRepository.delete(verificationCode);

        UserDetails userDetails = customUserService.loadUserByUsername(username);

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }
        // Delete OTP after successful login



    private Authentication authenticateAdmin(String otp) throws Exception {

        VerificationCode verificationCode = verificationCodeRepository.findByEmail(ADMIN_EMAIL);
        if (verificationCode == null || !verificationCode.getOtp().equals(otp)) {
            throw new BadCredentialsException("Wrong OTP for Admin");
        }

        // Delete OTP after login
        verificationCodeRepository.delete(verificationCode);

        UserDetails userDetails = customUserService.loadUserByUsername(ADMIN_EMAIL);
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }

    private AuthResponse buildAuthResponse(Authentication authentication, USER_ROLE role) {
        String token = jwtProvider.generateToken(authentication);
        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Login Success");
        authResponse.setRole(role);
        return authResponse;
    }
}