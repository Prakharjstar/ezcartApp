package com.shop.ecommerce.multivendor.Service.impl;

import com.shop.ecommerce.multivendor.Service.SellerReportService;
import com.shop.ecommerce.multivendor.model.Seller;
import com.shop.ecommerce.multivendor.model.SellerReport;
import com.shop.ecommerce.multivendor.repository.SellerReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SellerReportServiceImpl implements SellerReportService {
    private final SellerReportRepository sellerReportRepository;


    @Override
    public SellerReport getSellerReport(Seller seller) {
        SellerReport sr = sellerReportRepository.findBySellerId(seller.getId());

        if(sr==null){
            SellerReport newReport = new SellerReport();
            newReport.setSeller(seller);
            return sellerReportRepository.save(newReport);
        }
        return sr;
    }

    @Override
    public SellerReport updateSellerReport(SellerReport sellerReport) {
        return sellerReportRepository.save(sellerReport);
    }
}
