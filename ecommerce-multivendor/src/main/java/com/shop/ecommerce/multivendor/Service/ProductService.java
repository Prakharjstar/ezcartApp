package com.shop.ecommerce.multivendor.Service;

import com.shop.ecommerce.multivendor.model.Product;
import com.shop.ecommerce.multivendor.model.Seller;
import com.shop.ecommerce.multivendor.request.CreateProductRequest;
import org.springframework.data.domain.Page;



import java.util.List;

public interface ProductService {

 public Product createProduct(CreateProductRequest req , Seller seller);
 public void deleteProduct(Long productId);
 public Product updateProduct(Long productId, Product product);
 Product findProductById(Long productId);
 List<Product> searchProduct();
 public Page<Product> getAllProducts( String category, String brand , String colors, String sizes , String minPrice , String maxPrice ,String minDiscount, String sort , String stock , String pageNumber);
 List<Product> getProductBySellerId(Long sellerId);
    }

