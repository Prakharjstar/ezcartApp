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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    // 🔹 Send OTP for Customer, Seller, Admin
    @Override
    public void sentLoginOtp(String email) throws Exception {
        USER_ROLE role;
        if (email.equals(ADMIN_EMAIL)) {
            role = USER_ROLE.ROLE_ADMIN;
        } else if (userRepository.findByEmail(email) != null) {
            role = USER_ROLE.ROLE_CUSTOMER;
        } else if (sellerRepository.findByEmail(email) != null) {
            role = USER_ROLE.ROLE_SELLER;
        } else {
            throw new Exception("User/Seller not found: " + email);
        }

        // Remove previous OTP
        VerificationCode existing = verificationCodeRepository.findByEmail(email);
        if (existing != null) verificationCodeRepository.delete(existing);

        // Generate OTP
        String otp = OtpUtil.generateOtp();
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setOtp(otp);
        verificationCode.setEmail(email);
        verificationCodeRepository.save(verificationCode);

        // Send email
        String subject = "Ezcart Login OTP";
        String text = "Your OTP is: " + otp;
        emailService.sendVerificationOtpEmail(email, otp, subject, text);

        System.out.println("OTP sent to: " + email + " | Role: " + role);
    }
    @Override
    public String createUser(SignupRequest req) throws Exception {
        // 1️⃣ Check if OTP exists and is correct
        VerificationCode verificationCode = verificationCodeRepository.findByEmail(req.getEmail());
        if (verificationCode == null || !verificationCode.getOtp().equals(req.getOtp())) {
            throw new Exception("Invalid OTP");
        }

        // 2️⃣ Check if user already exists
        User user = userRepository.findByEmail(req.getEmail());
        if (user != null) {
            throw new Exception("User already exists with this email");
        }

        // 3️⃣ Create new Customer user
        User newUser = new User();
        newUser.setEmail(req.getEmail());
        newUser.setFullName(req.getFullName());
        newUser.setRole(USER_ROLE.ROLE_CUSTOMER);

        newUser.setPassword(passwordEncoder.encode(req.getOtp())); // you can store OTP or random password


        User savedUser = userRepository.save(newUser);

        // 4️⃣ Create Cart for new customer
        Cart cart = new Cart();
        cart.setUser(savedUser);
        cartRepository.save(cart);

        // 5️⃣ Authenticate user immediately and generate JWT
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(savedUser.getRole().name()));

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                savedUser.getEmail(),
                null,
                authorities
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtProvider.generateToken(authentication); // return JWT to frontend
    }

    //  Login (Customer, Seller, Admin)
    @Override
    public AuthResponse signing(LoginRequest req) throws Exception {
        String username = req.getEmail();
        USER_ROLE role;

        //  Admin login
        if (username.equals(ADMIN_EMAIL)) {
            role = USER_ROLE.ROLE_ADMIN;
            Authentication authentication = authenticateAdmin(req.getOtp());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            return buildAuthResponse(authentication, role);
        }

        // ✅ Customer/Seller login
        User user = userRepository.findByEmail(username);
        if (user != null) {
            role = USER_ROLE.ROLE_CUSTOMER;
        } else {
            Seller seller = sellerRepository.findByEmail(username);
            if (seller != null) {
                role = USER_ROLE.ROLE_SELLER;
                username = SELLER_PREFIX + username;
            } else {
                throw new BadCredentialsException("User not found");
            }
        }

        Authentication authentication = authenticate(username, req.getOtp(), role);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return buildAuthResponse(authentication, role);
    }

    // 🔹 Authentication for Customer/Seller
    private Authentication authenticate(String username, String otp, USER_ROLE role) throws Exception {
        String emailForOtp = username.startsWith(SELLER_PREFIX)
                ? username.substring(SELLER_PREFIX.length())
                : username;

        VerificationCode verificationCode = verificationCodeRepository.findByEmail(emailForOtp);
        if (verificationCode == null || !verificationCode.getOtp().equals(otp)) {
            throw new BadCredentialsException("Wrong OTP");
        }

        UserDetails userDetails = customUserService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    // 🔹 Authentication for Admin
    private Authentication authenticateAdmin(String otp) throws Exception {
        VerificationCode verificationCode = verificationCodeRepository.findByEmail(ADMIN_EMAIL);
        if (verificationCode == null || !verificationCode.getOtp().equals(otp)) {
            throw new BadCredentialsException("Wrong OTP for Admin");
        }

        UserDetails userDetails = customUserService.loadUserByUsername(ADMIN_EMAIL);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
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