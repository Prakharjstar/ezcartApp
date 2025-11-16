package com.shop.ecommerce.multivendor.repository;

import com.shop.ecommerce.multivendor.model.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentOrderRepository extends JpaRepository<PaymentOrder,Long> {
    PaymentOrder findByPaymentLinkId(String paymentId);
}
