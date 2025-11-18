package com.shop.ecommerce.multivendor.Service;

import com.shop.ecommerce.multivendor.model.Order;
import com.shop.ecommerce.multivendor.model.Seller;
import com.shop.ecommerce.multivendor.model.Transaction;

import java.util.List;

public interface TransactionService {

    Transaction createTransaction(Order order);
    List<Transaction> getTransactionBySellerId(Seller seller);
    List<Transaction> getAllTransactions();
}
