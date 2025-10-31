package com.shop.ecommerce.multivendor.Service.impl;

import com.shop.ecommerce.multivendor.Config.JwtProvider;
import com.shop.ecommerce.multivendor.model.Seller;
import com.shop.ecommerce.multivendor.repository.CartRepository;
import com.shop.ecommerce.multivendor.repository.SellerRepository;
import com.shop.ecommerce.multivendor.repository.UserRepository;
import com.shop.ecommerce.multivendor.repository.VerificationCodeRepository;
import com.shop.ecommerce.multivendor.Service.AuthService;
import com.shop.ecommerce.multivendor.Service.EmailService;
import com.shop.ecommerce.multivendor.Util.OtpUtil;
import com.shop.ecommerce.multivendor.domain.USER_ROLE;
import com.shop.ecommerce.multivendor.model.Cart;
import com.shop.ecommerce.multivendor.model.User;
import com.shop.ecommerce.multivendor.model.VerificationCode;
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
    private final PasswordEncoder passwordEncoder;
    private final CartRepository cartRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final EmailService emailService;
    private final JwtProvider jwtProvider;
    private final CustomUserServiceImpl customUserService;
    private final SellerRepository sellerRepository;

    @Override
    public void sentLoginOtp(String email , USER_ROLE role) throws Exception {

        String SIGNING_PREFIX = "signing_";

        if (email.startsWith(SIGNING_PREFIX)) {
            email = email.substring(SIGNING_PREFIX.length());

            if(role.equals(USER_ROLE.ROLE_SELLER)) {
                Seller seller = sellerRepository.findByEmail(email);
                if (seller == null) {
                    throw new Exception("seller not found");
                }
            }

            else{
                System.out.println("email"+email);
                User user = userRepository.findByEmail(email);
                if (user == null) {
                    throw new Exception("User not exist with the provided email");
                }

            }

        }

        VerificationCode existing = verificationCodeRepository.findByEmail(email);
        if (existing != null) {
            verificationCodeRepository.delete(existing);
        }

        String otp = OtpUtil.generateOtp();
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setOpt(otp);
        verificationCode.setEmail(email);
        verificationCodeRepository.save(verificationCode);

        String subject = "Ezcart login/signup OTP";
        String text = "Your Login/Signup OTP is - " + otp;
        emailService.sendVerificationOtpEmail(email, otp, subject, text);
    }

    @Override
    public String createUser(SignupRequest req) throws Exception {
        VerificationCode verificationCode = verificationCodeRepository.findByEmail(req.getEmail());
        if (verificationCode == null || !verificationCode.getOpt().equals(req.getOtp())) {
            throw new Exception("Wrong OTP");
        }

        User user = userRepository.findByEmail(req.getEmail());
        if (user == null) {
            User createdUser = new User();
            createdUser.setEmail(req.getEmail());
            createdUser.setFullName(req.getFullName());
            createdUser.setRole(USER_ROLE.ROLE_CUSTOMER);
            createdUser.setMobile("9878655457");
            createdUser.setPassword(passwordEncoder.encode(req.getOtp()));
            user = userRepository.save(createdUser);

            Cart cart = new Cart();
            cart.setUser(user);
            cartRepository.save(cart);
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(USER_ROLE.ROLE_CUSTOMER.toString()));

        Authentication authentication = new UsernamePasswordAuthenticationToken(req.getEmail(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtProvider.generateToken(authentication);
    }

    @Override
    public AuthResponse signing(LoginRequest req) {
        String username = req.getEmail();
        String otp = req.getOtp();

        Authentication authentication = authenticate(username, otp);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Login Success");
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String roleName = authorities.isEmpty() ? null : authorities.iterator().next().getAuthority();
        authResponse.setRole(USER_ROLE.valueOf(roleName));

        return authResponse;
    }

    private Authentication authenticate(String username, String otp) {
        UserDetails userDetails = customUserService.loadUserByUsername(username);
        if (userDetails == null) {
            throw new BadCredentialsException("Invalid username or password");
        }

        VerificationCode verificationCode = verificationCodeRepository.findByEmail(username);
        if (verificationCode == null || !verificationCode.getOpt().equals(otp)) {
            throw new BadCredentialsException("Wrong OTP");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
