package com.shop.ecommerce.multivendor.Service.impl;

import com.shop.ecommerce.multivendor.Service.HomeService;
import com.shop.ecommerce.multivendor.model.Deal;
import com.shop.ecommerce.multivendor.model.Home;
import com.shop.ecommerce.multivendor.model.HomeCategory;
import com.shop.ecommerce.multivendor.model.HomeCategorySection;
import com.shop.ecommerce.multivendor.repository.DealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

    private final DealRepository dealRepository;


    @Override
    public Home createHomePageData(List<HomeCategory> allCategories) {

        List<HomeCategory> gridCategories = allCategories.stream().filter(category-> category.getSection()== HomeCategorySection.GRID)
                .collect(Collectors.toList());

        List<HomeCategory> shopByCategries = allCategories.stream().filter(category-> category.getSection()== HomeCategorySection.SHOP_BY_CATEGORIES)
                .collect(Collectors.toList());

        List<HomeCategory> electricCategories = allCategories.stream().filter(category-> category.getSection()== HomeCategorySection.ELECTRIC_CATEGORIES)
                .collect(Collectors.toList());

        List<HomeCategory> dealCategories= allCategories.stream().filter(category-> category.getSection()== HomeCategorySection.DEALS)
                .collect(Collectors.toList());

       List<Deal> createdDeals = new ArrayList<>();

       if(dealRepository.findAll().isEmpty()){
           List<Deal> deals = allCategories.stream().filter(category-> category.getSection()==HomeCategorySection.DEALS)
                   .map(category-> new Deal(null, 10,category))
                   .collect(Collectors.toList());

           createdDeals =dealRepository.saveAll(deals);

       }else createdDeals=dealRepository.findAll();

       Home home = new Home();
       home.setGird(gridCategories);
       home.setShopByCategories(shopByCategries);
       home.setElectricCategories(electricCategories);
       home.setDeals(createdDeals);
       home.setDealCategories(dealCategories);

       return home;




    }
}
