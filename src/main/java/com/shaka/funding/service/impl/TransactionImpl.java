package com.shaka.funding.service.impl;


import com.shaka.funding.dto.TransactionDto;
import com.shaka.funding.entity.Transaction;
import com.shaka.funding.repository.TransactionRepository;
import com.shaka.funding.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TransactionImpl implements TransactionService {
    private final TransactionRepository transactionRepository;

    @Override
    public void saveTransaction(TransactionDto transactionDto) {
        Transaction transaction = Transaction.builder()
                .transactionType(transactionDto.getTransactionType())
                .transactionAmount(transactionDto.getTransactionAmount())
                .accountNumber(transactionDto.getAccountNumber())
                .status("SUCCESS")
                .build();
        transactionRepository.save(transaction);
        System.out.println("Transaction saved successfully");
    }
}
