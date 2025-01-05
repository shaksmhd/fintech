package com.shaka.funding.dto;

import com.shaka.funding.entity.Loan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanResponse {
    private Long id;
    private Long userId;
    private BigDecimal amount;
    private BigDecimal interestRate;
    private Integer tenure; // in months
    private String status; // e.g., "APPLIED", "APPROVED", "REJECTED", "REPAID"

    public LoanResponse(Loan loan) {
        this.id = loan.getId();
        this.userId = loan.getUserId();
        this.amount = loan.getAmount();
        this.interestRate = loan.getInterestRate();
        this.tenure = loan.getTenure();
        this.status = loan.getStatus();
    }
}