package com.shop.ecommerce.multivendor.Controller;

import com.shop.ecommerce.multivendor.Service.ProductService;
import com.shop.ecommerce.multivendor.Service.SellerService;
import com.shop.ecommerce.multivendor.model.Exceptions.ProductException;
import com.shop.ecommerce.multivendor.model.Exceptions.SellerException;
import com.shop.ecommerce.multivendor.model.Product;
import com.shop.ecommerce.multivendor.model.Seller;
import com.shop.ecommerce.multivendor.request.CreateProductRequest;
import jdk.jshell.spi.ExecutionControl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sellers/products")
public class SellerProductController {


    private final ProductService  productService;
    private final SellerService sellerService;

    @GetMapping()
    public ResponseEntity<List<Product>> getProductBySellerId(@RequestHeader("Authorization") String jwt) throws Exception {
              Seller seller =sellerService.getSellerProfile(jwt);

              List<Product> products = productService.getProductBySellerId(seller.getId());
              return new ResponseEntity<>(products , HttpStatus.OK);

    }

    @PostMapping()
    public ResponseEntity<Product> createProduct(@RequestBody CreateProductRequest request, @RequestHeader("Authorization") String jwt) throws Exception {

        Seller seller = sellerService.getSellerProfile(jwt);
        Product product = productService.createProduct(request,seller);
        return new ResponseEntity<>(product,HttpStatus.CREATED);
    }


    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId){
        try{
            productService.deleteProduct(productId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ProductException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<Product> updateProduct(@PathVariable Long productId , @RequestBody Product product) throws ProductException{

            Product updatedProduct = productService.updateProduct(productId,product);
            return new ResponseEntity<>(updatedProduct , HttpStatus.OK);
        }
}



