package com.shop.ecommerce.multivendor.repository;

import com.shop.ecommerce.multivendor.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address,Long> {
}
