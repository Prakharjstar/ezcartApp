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

        for (Map.Entry<Long, List<CartItem>> entry : itemsBySeller.entrySet()) {
            Long sellerId = entry.getKey();
            List<CartItem> items = entry.getValue();

            int totalOrderPrice = items.stream()
                    .mapToInt(item -> item.getSellingPrice() * item.getQuantity())
                    .sum();

            int totalItem = items.stream()
                    .mapToInt(CartItem::getQuantity)
                    .sum();

            Order order = new Order();
            order.setUser(user);
            order.setSellerId(sellerId);
            order.setShippingAddress(address);
            order.setOrderStatus(OrderStatus.PENDING);
            order.setTotalMrpPrice(totalOrderPrice);
            order.setTotalSellingPrice(totalOrderPrice);
            order.setTotalItem(totalItem);

            // initialize payment details
            PaymentDetails paymentDetails = new PaymentDetails();
            paymentDetails.setStatus(PaymentStatus.PENDING);
            order.setPaymentDetails(paymentDetails);

            // add order items
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
                orderItem.setUserId(user.getId()); // <--- important
                order.getOrderItems().add(orderItem);


            }

            orders.add(orderRepository.save(order));
        }

        return orders;
    }
    @Override
    public Order findOrderById(long id) throws Exception {
        return orderRepository.findById(id).orElseThrow(() -> new Exception("Order not found..."));
    }


    @Override
    public List<Order> usersOrderHistory(Long userId) {
        return orderRepository.findByUserId(userId);
    }



    @Override
    public List<Order> sellersOrder(Long sellerId) {
        return orderRepository.findBySellerId(sellerId);
    }

    @Override
    public Order updateOrderStatus(Long orderId, OrderStatus orderStatus) throws Exception {
        Order order = findOrderById(orderId);
        order.setOrderStatus(orderStatus);
        return orderRepository.save(order);
    }

    @Override
    public Order cancelOrder(Long orderId, User user) throws Exception {
        Order order = findOrderById(orderId);
        if (!user.getId().equals(order.getUser().getId())) {
            throw new Exception("You don't have access to this Order");
        }
        order.setOrderStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);

    }

    @Override
    public OrderItem getOrderItemById(Long id) throws Exception {
        return orderItemRepository.findById(id).orElseThrow(()-> new Exception("order item not exist...."));

    }

    @Override
    public int countTodayOrders(User user) {

        LocalDate today = LocalDate.now();

        List<Order> orders = orderRepository.findByUserId(user.getId());

        return (int) orders.stream()
                .filter(order -> order.getOrderDate().toLocalDate().equals(today))
                .count();
    }

}