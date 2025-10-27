package com.shop.ecommerce.multivendor.Service.impl;

import com.shop.ecommerce.multivendor.Config.JwtProvider;
import com.shop.ecommerce.multivendor.Repository.CartRepository;
import com.shop.ecommerce.multivendor.Repository.UserRepository;
import com.shop.ecommerce.multivendor.Repository.VerificationCodeRepository;
import com.shop.ecommerce.multivendor.Service.AuthService;
import com.shop.ecommerce.multivendor.Service.EmailService;
import com.shop.ecommerce.multivendor.Util.OtpUtil;
import com.shop.ecommerce.multivendor.domain.USER_ROLE;
import com.shop.ecommerce.multivendor.model.Cart;
import com.shop.ecommerce.multivendor.model.User;
import com.shop.ecommerce.multivendor.model.VerificationCode;
import com.shop.ecommerce.multivendor.response.SignupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
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

    @Override
    public void sentLoginOtp(String email) throws Exception {
        String SIGNING_PREFIX="signin_";

        if(email.startsWith(SIGNING_PREFIX)){
            email = email.substring(SIGNING_PREFIX.length());

            User user = userRepository.findByEmail(email);
            if(user==null){
                throw new Exception("user not exist with the provided email");
            }
        }

        VerificationCode isExist = verificationCodeRepository.findByEmail(email);

        if(isExist!=null){
            verificationCodeRepository.delete(isExist);

        }
        String otp = OtpUtil.generateOtp();

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setOpt(otp);
        verificationCode.setEmail(email);
        verificationCodeRepository.save(verificationCode);

        String subject= "Ezcart login/signup otp";
        String text = "Your Login/Signup otp is - ";
        emailService.sendVerificationOtpEmail(email,otp,subject ,text);



    }

    @Override
    public String createUser(SignupRequest req) throws Exception {

        String SIGNING_PREFIX="signing_";

        VerificationCode verificationCode = verificationCodeRepository.findByEmail(req.getEmail());

        if(verificationCode==null || !verificationCode.getOpt().equals(req.getOtp())){
            throw new Exception("Wrong otp..");
        }




        User user = userRepository.findByEmail(req.getEmail());

        if(user==null){
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

        Authentication authentication = new UsernamePasswordAuthenticationToken(req.getEmail(),null,authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtProvider.generateToken(authentication);
    }
}
