package com.shaka.funding.service;


import com.shaka.funding.dto.TransactionDto;

public interface TransactionService {
    void saveTransaction(TransactionDto transactionDto);

}
