package com.shop.ecommerce.multivendor.Service;

import com.shop.ecommerce.multivendor.model.Home;
import com.shop.ecommerce.multivendor.model.HomeCategory;

import java.util.List;

public interface HomeService {

    public Home createHomePageData(List<HomeCategory> allCategories);
}
