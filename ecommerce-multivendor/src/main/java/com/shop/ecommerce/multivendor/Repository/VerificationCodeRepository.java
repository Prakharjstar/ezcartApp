package com.shop.ecommerce.multivendor.Repository;

import com.shop.ecommerce.multivendor.model.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode,Long> {

    VerificationCode findByEmail(String email);
}
