package com.shop.ecommerce.multivendor.repository;

import com.shop.ecommerce.multivendor.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category , Long> {
    Category findByCategoryId(String categoryId);
}
