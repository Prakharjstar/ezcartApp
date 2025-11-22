package com.shop.ecommerce.multivendor.Service.impl;

import com.shop.ecommerce.multivendor.Service.DealService;
import com.shop.ecommerce.multivendor.Service.HomeCategoryService;
import com.shop.ecommerce.multivendor.model.Deal;
import com.shop.ecommerce.multivendor.model.HomeCategory;
import com.shop.ecommerce.multivendor.repository.DealRepository;
import com.shop.ecommerce.multivendor.repository.HomeCatogeryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DealServiceImpl implements DealService {

    private final DealRepository dealRepository;
    private final HomeCatogeryRepository homeCatogeryRepository;


    @Override
    public List<Deal> getDeals() {
        return dealRepository.findAll();
    }

    @Override
    public Deal createDeal(Deal deal) {
        HomeCategory  category = homeCatogeryRepository.findById(deal.getCategory().getId()).orElse(null);
        Deal newDeal = dealRepository.save(deal);
        newDeal.setCategory(category);
        newDeal.setDiscount(deal.getDiscount());
        return dealRepository.save(newDeal);


    }

    @Override
    public Deal updateDeal(Deal deal , Long id) throws Exception {
        Deal existingDeal = dealRepository.findById(id).orElse(null);
        HomeCategory category = homeCatogeryRepository.findById(deal.getCategory().getId()).orElse(null);

        if (existingDeal != null) {
            if (deal.getDiscount() != null) {
                existingDeal.setDiscount(deal.getDiscount());
            }
            if (category != null) {
                existingDeal.setCategory(category);
            }
            return dealRepository.save(existingDeal);
        }
        throw new Exception("deal Not Found");
    }

    @Override
    public void deleteDeal(Long id) throws Exception {
        Deal deal= dealRepository.findById(id).orElseThrow(()-> new Exception("deal not found"));
        dealRepository.delete(deal);

    }
}
