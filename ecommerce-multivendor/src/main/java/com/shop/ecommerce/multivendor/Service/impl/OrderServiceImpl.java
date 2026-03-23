package com.shop.ecommerce.multivendor.Service.impl;

import com.shop.ecommerce.multivendor.Service.OrderService;
import com.shop.ecommerce.multivendor.domain.OrderStatus;
import com.shop.ecommerce.multivendor.model.*;
import com.shop.ecommerce.multivendor.repository.AddressRepository;
import com.shop.ecommerce.multivendor.repository.OrderItemRepository;
import com.shop.ecommerce.multivendor.repository.OrderRepository;
import com.shop.ecommerce.multivendor.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public Set<Order> createOrder(User user, Address shippingAddress, Cart cart) {

        if (!user.getAddresses().contains(shippingAddress)) {
            user.getAddresses().add(shippingAddress);
        }

        Address address = addressRepository.save(shippingAddress);

        Map<Long, List<CartItem>> itemsBySeller = cart.getCartItems()
                .stream()
                .collect(Collectors.groupingBy(item -> item.getProduct().getSeller().getId()));

        Set<Order> orders = new HashSet<>();

        double cartTotalSellingPrice = cart.getTotalSellingPrice();
        double cartDiscount = cart.getCouponDiscountAmount();

        for (Map.Entry<Long, List<CartItem>> entry : itemsBySeller.entrySet()) {

            Long sellerId = entry.getKey();
            List<CartItem> items = entry.getValue();

            int sellerTotal = items.stream()
                    .mapToInt(item -> item.getSellingPrice() * item.getQuantity())
                    .sum();

            int totalItem = items.stream()
                    .mapToInt(CartItem::getQuantity)
                    .sum();

            int sellerDiscount = 0;
            if (cartTotalSellingPrice > 0) {
                double ratio = (double) sellerTotal / cartTotalSellingPrice;
                sellerDiscount = (int) (cartDiscount * ratio);
            }

            int finalAmount = sellerTotal - sellerDiscount;

            Order order = new Order();
            order.setUser(user);
            order.setSellerId(sellerId);
            order.setShippingAddress(address);
            order.setOrderStatus(OrderStatus.PENDING); // initial status

            order.setTotalMrpPrice(sellerTotal);
            order.setTotalSellingPrice(sellerTotal);
            order.setDiscount(sellerDiscount);
            order.setTotalAmount(finalAmount);
            order.setTotalItem(totalItem);

            PaymentDetails paymentDetails = new PaymentDetails();
            paymentDetails.setStatus(PaymentStatus.PENDING);
            order.setPaymentDetails(paymentDetails);

            for (CartItem item : items) {
                Product product = productRepository.findById(item.getProduct().getId())
                        .orElseThrow(() -> new RuntimeException("Product not found"));

                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProduct(product);
                orderItem.setQuantity(item.getQuantity());
                orderItem.setMrpPrice(item.getMrpPrice());
                orderItem.setSellingPrice(item.getSellingPrice());
                orderItem.setSize(item.getSize());
                orderItem.setUserId(user.getId());

                order.getOrderItems().add(orderItem);
            }

            orders.add(orderRepository.save(order));
        }

        return orders;
    }

    @Override
    public Order findOrderById(long id) throws Exception {
        return orderRepository.findById(id)
                .orElseThrow(() -> new Exception("Order not found"));
    }

    @Override
    public List<Order> usersOrderHistory(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        orders.forEach(order -> order.setOrderStatus(calculateOrderStatus(order)));
        return orders;
    }

    @Override
    public List<Order> sellersOrder(Long sellerId) {
        List<Order> orders = orderRepository.findBySellerId(sellerId);
        orders.forEach(order -> order.setOrderStatus(calculateOrderStatus(order)));
        return orders;
    }

    @Override
    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus orderStatus) throws Exception {
        Order order = findOrderById(orderId);
        if (orderStatus == OrderStatus.SHIPPED) {
            order.setShippedDate(LocalDate.now().atStartOfDay());
        }
        order.setOrderStatus(calculateOrderStatus(order));
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order cancelOrder(Long orderId, User user) throws Exception {
        Order order = findOrderById(orderId);
        if (!user.getId().equals(order.getUser().getId())) {
            throw new Exception("You don't have access to this Order");
        }
        order.setCancelled(true);
        order.setOrderStatus(calculateOrderStatus(order));
        return orderRepository.save(order);
    }

    @Override
    public OrderItem getOrderItemById(Long id) throws Exception {
        return orderItemRepository.findById(id)
                .orElseThrow(() -> new Exception("Order item does not exist"));
    }

    @Override
    public int countTodayOrders(User user) {
        LocalDate today = LocalDate.now();
        List<Order> orders = orderRepository.findByUserId(user.getId());
        return (int) orders.stream()
                .filter(order -> order.getOrderDate().toLocalDate().equals(today))
                .count();
    }

    @Override
    public void deleteOrder(Long orderId) {

    }


    private OrderStatus calculateOrderStatus(Order order) {
        if (order.isCancelled()) return OrderStatus.CANCELLED;
        if (order.getShippedDate() != null) {
            if (LocalDate.now().isAfter(order.getDeliverDate().toLocalDate())) return OrderStatus.DELIVERED;
            return OrderStatus.SHIPPED;
        }
        return OrderStatus.PENDING;
    }
}