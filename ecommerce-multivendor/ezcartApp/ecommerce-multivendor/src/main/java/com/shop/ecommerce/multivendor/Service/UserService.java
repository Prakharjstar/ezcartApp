package com.shop.ecommerce.multivendor.Service;

import com.shop.ecommerce.multivendor.model.User;

public interface UserService {
     User findUserByJwtToken(String jwt) throws Exception;
     User findUserByEmail(String email) throws Exception;
}
