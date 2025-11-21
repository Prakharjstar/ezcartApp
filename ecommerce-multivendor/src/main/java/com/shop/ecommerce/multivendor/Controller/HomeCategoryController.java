package com.shop.ecommerce.multivendor.Controller;

import com.shop.ecommerce.multivendor.Service.HomeCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor

public class HomeCategoryController {
    private final HomeCategoryService homeCategoryService;
}
