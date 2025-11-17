package com.shop.ecommerce.multivendor.Service;

import com.razorpay.PaymentLink;
import com.razorpay.RazorpayException;
import com.shop.ecommerce.multivendor.model.Order;
import com.shop.ecommerce.multivendor.model.PaymentOrder;
import com.shop.ecommerce.multivendor.model.User;
import com.stripe.exception.StripeException;

import java.util.Set;

public interface PaymentService {

    PaymentOrder CreateOrder(User user , Set<Order> orders);
    PaymentOrder getPaymentOrderById(Long orderId) throws Exception;
    PaymentOrder getPaymentOrderByPaymentId(String orderId) throws Exception;
    Boolean ProceedPaymentOrder(PaymentOrder  paymentOrder , String paymentId , String paymentLinkId) throws RazorpayException;

    PaymentLink createRazorpayPaymentLink(User user , Long amount , Long orderId) throws RazorpayException;

    String createStripePaymentLink(User user  ,Long amount , Long orderId) throws StripeException;
}
