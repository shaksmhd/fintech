package com.shaka.funding.controller;

import com.shaka.funding.dto.*;
import com.shaka.funding.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/user")
@Slf4j
@Tag(name = "User Controller", description = "Endpoints for user operations")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    @Operation(summary = "Create a new user account", description = "Creates a new user account with the provided details")
    public BankResponse createAccount(@RequestBody UserRequest userRequest) {
        log.info("Received request to create account for email: {}", userRequest.getEmail());
        BankResponse response = userService.createAccount(userRequest);
        log.info("Response for account creation: {}", response.getResponseMessage());
        return response;
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Logs in a user with the provided credentials")
    public BankResponse login(@RequestBody LoginRequest userRequest) {
        log.info("Received request to login for email: {}", userRequest.getEmail());
        BankResponse response = userService.login(userRequest);
        log.info("Response for login: {}", response.getResponseMessage());
        return response;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAccountDetails")
    @Operation(summary = "Get account details", description = "Retrieves account details for the provided account number")
    public BankResponse getAccountDetails(@RequestParam String accountNumber) {
        log.info("Received request to get account details for accountNumber: {}", accountNumber);
        BankResponse response = userService.getAccountDetails(accountNumber);
        log.info("Response for account details: {}", response.getResponseMessage());
        return response;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update")
    @Operation(summary = "Update account details", description = "Updates account details with the provided information")
    public BankResponse updateAccount(@RequestBody UserRequest userRequest) {
        log.info("Received request to update account with accountNumber: {}", userRequest.getAccountNumber());
        BankResponse response = userService.updateAccount(userRequest);
        log.info("Response for account update: {}", response.getResponseMessage());
        return response;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete")
    @Operation(summary = "Delete account", description = "Deletes the account with the provided account number")
    public void deleteAccount(@RequestParam String accountNumber) {
        log.info("Received request to delete account with accountNumber: {}", accountNumber);
        userService.deleteAccount(accountNumber);
        log.info("Account with accountNumber {} deleted successfully", accountNumber);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/balanceEnquiry")
    @Operation(summary = "Balance enquiry", description = "Checks the balance for the provided account number")
    public BankResponse balanceEnquiry(@RequestBody EnquiryRequest enquiryRequest) {
        log.info("Received request to check balance for accountNumber: {}", enquiryRequest.getAccountNumber());
        return userService.balanceEnquiry(enquiryRequest);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/nameEnquiry")
    @Operation(summary = "Name enquiry", description = "Checks the name for the provided account number")
    public String nameEnquiry(@RequestBody EnquiryRequest enquiryRequest) {
        log.info("Received request to check name for accountNumber: {}", enquiryRequest.getAccountNumber());
        return userService.nameEnquiry(enquiryRequest);
    }

    @PostMapping("/credit")
    @Operation(summary = "Credit account", description = "Credits the account with the provided amount")
    public BankResponse creditAccount(@RequestBody CreditDebitRequest request) {
        log.info("Received request to credit account with accountNumber: {}", request.getAccountNumber());
        return userService.creditAccount(request);
    }

    @PostMapping("/debit")
    @Operation(summary = "Debit account", description = "Debits the account with the provided amount")
    public BankResponse debitAccount(@RequestBody CreditDebitRequest request) {
        log.info("Received request to debit account with accountNumber: {}", request.getAccountNumber());
        return userService.debitAccount(request);
    }
}