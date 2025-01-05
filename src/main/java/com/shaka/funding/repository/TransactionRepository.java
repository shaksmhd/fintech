package com.shaka.funding.repository;


import com.shaka.funding.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findByAccountNumber(String accountNumber);
}
