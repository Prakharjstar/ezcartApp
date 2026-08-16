package com.shop.ecommerce.multivendor.repository;

import com.shop.ecommerce.multivendor.model.Order;
import com.shop.ecommerce.multivendor.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
