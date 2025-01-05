package com.shaka.funding.service;


import com.shaka.funding.dto.*;

public interface UserService {

    BankResponse createAccount(UserRequest userRequest);
    BankResponse login(LoginRequest userRequest);
    BankResponse getAccountDetails(String accountNumber);
    BankResponse updateAccount(UserRequest userRequest);
    void deleteAccount(String accountNumber);
    BankResponse balanceEnquiry(EnquiryRequest enquiryRequest);
    String nameEnquiry(EnquiryRequest enquiryRequest);
    BankResponse creditAccount (CreditDebitRequest request);
    BankResponse debitAccount (CreditDebitRequest request);
}
