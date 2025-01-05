package com.shaka.funding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class BankResponse {

    private String responseCode;
    private String responseMessage;
    private  AccountInfo accountInfo;

}
