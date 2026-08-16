package com.shop.ecommerce.multivendor.repository;

import com.shop.ecommerce.multivendor.model.Deal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DealRepository  extends JpaRepository<Deal ,Long> {
}
