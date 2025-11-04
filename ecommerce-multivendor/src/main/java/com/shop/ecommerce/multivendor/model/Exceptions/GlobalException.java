package com.shop.ecommerce.multivendor.model.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalException {

    @ExceptionHandler(SellerException.class)
    public ResponseEntity<ErrorDetail> sellerExceptionHandler(SellerException se , WebRequest req){
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setError(se.getMessage());
        errorDetail.setDetails(req.getDescription(false));
        errorDetail.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(errorDetail, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductException.class)
    public ResponseEntity<ErrorDetail> productExceptionHandler(ProductException se , WebRequest req){
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setError(se.getMessage());
        errorDetail.setDetails(req.getDescription(false));
        errorDetail.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(errorDetail, HttpStatus.BAD_REQUEST);
    }
}
