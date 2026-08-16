package com.shop.ecommerce.multivendor.Controller;

import com.shop.ecommerce.multivendor.Service.ProductService;
import com.shop.ecommerce.multivendor.Service.ReviewService;
import com.shop.ecommerce.multivendor.Service.UserService;
import com.shop.ecommerce.multivendor.model.Product;
import com.shop.ecommerce.multivendor.model.Review;
import com.shop.ecommerce.multivendor.model.User;
import com.shop.ecommerce.multivendor.request.CreateReviewRequest;
import com.shop.ecommerce.multivendor.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;
    private final ProductService productService;


    @GetMapping("/products/{productId}/reviews")
    public ResponseEntity<List<Review>> getReviewByProductId(@PathVariable Long productId){
        List<Review> reviews = reviewService.getReviewByProductId(productId);
        return ResponseEntity.ok(reviews);
    }


    @PostMapping("/products/{productId}/reviews")
    public ResponseEntity<Review> writeReview(@RequestBody CreateReviewRequest req, @PathVariable Long productId , @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        Product product = productService.findProductById(productId);
        Review review = reviewService.createReview(req,user, product);
        return ResponseEntity.ok(review);

    }

    @PatchMapping("/reviews/{reviewId}")
    public ResponseEntity<Review> updateReview(@RequestBody CreateReviewRequest req , @PathVariable Long reviewId ,@ RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);

        Review review = reviewService.updateReview(reviewId,req.getReviewText(),req.getReviewRating(),user.getId());
        return ResponseEntity.ok(review);


    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponse> deleteReview(@PathVariable Long reviewId , @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        reviewService.deleteReview(reviewId,user.getId());
        ApiResponse res = new ApiResponse();
        res.setMessage("Review Deleted Successfully");

        return ResponseEntity.ok(res);
    }
}
