package com.shaka.funding.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AccountInfo {

    private String accountName;
    private BigDecimal accountBalance;
    private String accountNumber;
    private String token;

}
