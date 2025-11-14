package com.shop.ecommerce.multivendor.Service;

import com.shop.ecommerce.multivendor.model.Seller;
import com.shop.ecommerce.multivendor.model.SellerReport;

public interface SellerReportService {
    SellerReport getSellerReport(Seller seller);
    SellerReport updateSellerReport(SellerReport sellerReport);
}
