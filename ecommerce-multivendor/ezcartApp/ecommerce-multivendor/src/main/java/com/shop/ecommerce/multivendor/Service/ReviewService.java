package com.shop.ecommerce.multivendor.Service;

import com.shop.ecommerce.multivendor.model.Product;
import com.shop.ecommerce.multivendor.model.Review;
import com.shop.ecommerce.multivendor.model.User;
import com.shop.ecommerce.multivendor.request.CreateReviewRequest;

import java.util.List;

public interface ReviewService {
    Review createReview(CreateReviewRequest req , User user , Product product);

    List<Review> getReviewByProductId(Long productId);

    Review updateReview(Long reviewId , String reviewText , double rating , Long userId) throws Exception;

    void deleteReview(Long reviewId , Long userId) throws Exception;
    Review getReviewById(Long reviewId) throws Exception;
}
