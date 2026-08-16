package com.shop.ecommerce.multivendor.Service.impl;

import com.shop.ecommerce.multivendor.Service.HomeCategoryService;
import com.shop.ecommerce.multivendor.model.HomeCategory;
import com.shop.ecommerce.multivendor.repository.HomeCatogeryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeCategoryServiceImpl implements HomeCategoryService {
    private final HomeCatogeryRepository homeCatogeryRepository;


    @Override
    public HomeCategory createHomeCategory(HomeCategory homeCategory) {
        return homeCatogeryRepository.save(homeCategory);
    }

    @Override
    public List<HomeCategory> createCategories(List<HomeCategory> homeCategories) {
        if(homeCatogeryRepository.findAll().isEmpty()){
            return homeCatogeryRepository.saveAll(homeCategories);
        }
        return homeCatogeryRepository.findAll();
    }

    @Override
    public HomeCategory updateHomeCategory(HomeCategory category, Long id) throws Exception {
        HomeCategory existingCategory = homeCatogeryRepository.findById(id).orElseThrow(()-> new Exception("Category not found"));
        if(category.getImage()!=null){
            existingCategory.setImage(category.getImage());
        }
        if(category.getCategoryId()!=null){
            existingCategory.setCategoryId(category.getCategoryId());
        }
        return homeCatogeryRepository.save(existingCategory);
    }

    @Override
    public List<HomeCategory> getAllHomeCategories() {
        return  homeCatogeryRepository.findAll();
    }
}
