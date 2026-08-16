package com.shop.ecommerce.multivendor.Service;

import com.shop.ecommerce.multivendor.model.Deal;

import java.util.List;

public interface DealService {

    List<Deal> getDeals();
    Deal createDeal(Deal deal);
    Deal updateDeal(Deal deal , Long id) throws Exception;
    void deleteDeal(Long id) throws Exception;
}
