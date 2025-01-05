package com.shaka.funding.service;

import com.shaka.funding.dto.LoanRequest;
import com.shaka.funding.dto.LoanResponse;
import com.shaka.funding.entity.Loan;

import java.util.List;

public interface LoanService {
    LoanResponse applyForLoan(LoanRequest loanRequest);
    List<Loan> getLoansByUserId(Long userId);
    LoanResponse updateLoanStatus(Long loanId, String status);
}