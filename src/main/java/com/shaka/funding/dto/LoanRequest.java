package com.shaka.funding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanRequest {
    private Long userId;
    private BigDecimal amount;
    private Integer tenure; // in months
}
