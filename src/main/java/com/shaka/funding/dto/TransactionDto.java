package com.shaka.funding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransactionDto {

    private String transactionType;
    private BigDecimal transactionAmount;
    private String accountNumber;
    private String status;
    private LocalDateTime transactionDate;
}
