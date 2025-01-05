package com.shaka.funding.controller;


import com.shaka.funding.dto.*;
import com.shaka.funding.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public BankResponse createAccount(@RequestBody UserRequest userRequest) {
        log.info("Received request to create account for email: {}", userRequest.getEmail());
        BankResponse response = userService.createAccount(userRequest);
        log.info("Response for account creation: {}", response.getResponseMessage());
        return response;
    }

    @PostMapping("/login")
    public BankResponse login(@RequestBody LoginRequest userRequest) {
        log.info("Received request to login for email: {}", userRequest.getEmail());
        BankResponse response = userService.login(userRequest);
        log.info("Response for login: {}", response.getResponseMessage());
        return response;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAccountDetails")
    public BankResponse getAccountDetails(@RequestParam String accountNumber) {
        log.info("Received request to get account details for accountNumber: {}", accountNumber);
        BankResponse response = userService.getAccountDetails(accountNumber);
        log.info("Response for account details: {}", response.getResponseMessage());
        return response;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update")
    public BankResponse updateAccount(@RequestBody UserRequest userRequest) {
        log.info("Received request to update account with accountNumber: {}", userRequest.getAccountNumber());
        BankResponse response = userService.updateAccount(userRequest);
        log.info("Response for account update: {}", response.getResponseMessage());
        return response;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete")
    public void deleteAccount(@RequestParam String accountNumber) {
        log.info("Received request to delete account with accountNumber: {}", accountNumber);
        userService.deleteAccount(accountNumber);
        log.info("Account with accountNumber {} deleted successfully", accountNumber);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/balanceEnquiry")
    public BankResponse balanceEnquiry(@RequestBody EnquiryRequest enquiryRequest) {
        log.info("Received request to check balance for accountNumber: {}", enquiryRequest.getAccountNumber());
        return userService.balanceEnquiry(enquiryRequest);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/nameEnquiry")
    public String nameEnquiry(@RequestBody EnquiryRequest enquiryRequest) {
        log.info("Received request to check name for accountNumber: {}", enquiryRequest.getAccountNumber());
        return userService.nameEnquiry(enquiryRequest);
    }

    @PostMapping("/credit")
    public BankResponse creditAccount(@RequestBody CreditDebitRequest request) {
        log.info("Received request to credit account with accountNumber: {}", request.getAccountNumber());
        return userService.creditAccount(request);
    }

    @PostMapping("/debit")
    public BankResponse debitAccount(@RequestBody CreditDebitRequest request) {
        log.info("Received request to debit account with accountNumber: {}", request.getAccountNumber());
        return userService.debitAccount(request);
    }
}
