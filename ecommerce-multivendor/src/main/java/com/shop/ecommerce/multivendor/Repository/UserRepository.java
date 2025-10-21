package com.shop.ecommerce.multivendor.Repository;

import com.shop.ecommerce.multivendor.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {


}
