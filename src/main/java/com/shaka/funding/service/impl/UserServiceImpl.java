package com.shaka.funding.service.impl;

import com.shaka.funding.dto.*;
import com.shaka.funding.entity.Role;
import com.shaka.funding.entity.User;
import com.shaka.funding.exceptions.ResourceNotFoundException;
import com.shaka.funding.repository.UserRepository;
import com.shaka.funding.service.JwtService;
import com.shaka.funding.service.TransactionService;
import com.shaka.funding.service.UserService;
import com.shaka.funding.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TransactionService transactionService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        log.info("Starting account creation for email: {}", userRequest.getEmail());
        try {
            if (userRepository.existsByEmail(userRequest.getEmail())) {
                log.warn("User with email {} already exists", userRequest.getEmail());
                throw new ResourceNotFoundException("User with email " + userRequest.getEmail() + " already exists");
            }

            String encodedPassword = passwordEncoder.encode(userRequest.getPassword());

            // Assign role, default to USER if not provided
            Role role = userRequest.getRole() != null ? userRequest.getRole() : Role.USER;

            User newUser = User.builder()
                    .firstName(userRequest.getFirstName())
                    .lastName(userRequest.getLastName())
                    .otherName(userRequest.getOtherName())
                    .gender(userRequest.getGender())
                    .address(userRequest.getAddress())
                    .StateOfOrigin(userRequest.getStateOfOrigin())
                    .email(userRequest.getEmail())
                    .password(encodedPassword)
                    .phoneNumber(userRequest.getPhoneNumber())
                    .accountNumber(AccountUtils.generateAccountNumber())
                    .accountBalance(BigDecimal.ZERO)
                    .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                    .status("ACTIVE")
                    .role(role) // Set the role
                    .build();

            User savedUser = userRepository.save(newUser);
            log.info("Account created successfully for email: {}", userRequest.getEmail());

            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_CREATION_SUCCESS_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountBalance(savedUser.getAccountBalance())
                            .accountNumber(savedUser.getAccountNumber())
                            .accountName(savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName())
                            .build())
                    .build();
        } catch (ResourceNotFoundException e) {
            log.error("Account creation failed: {}", e.getMessage(), e);
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXIST_CODE)
                    .responseMessage(e.getMessage())
                    .accountInfo(null)
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error during account creation", e);
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_CREATION_FAILURE_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_CREATION_FAILURE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
    }


    @Override
    public BankResponse login(LoginRequest userRequest) {
        log.info("Starting login for email: {}", userRequest.getEmail());
        try {
            // Authenticate user using AuthenticationManager
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userRequest.getEmail(), userRequest.getPassword())
            );

            // Retrieve user details from the database
            User user = userRepository.findByEmail(userRequest.getEmail());
            if (user == null) {
                log.warn("User with email {} not found", userRequest.getEmail());
                return BankResponse.builder()
                        .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                        .responseMessage("Invalid credentials")
                        .accountInfo(null)
                        .build();
            }

            // Generate JWT token
            String token = jwtService.generateToken(user);

            log.info("Login successful for email: {}", userRequest.getEmail());
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                    .responseMessage("Login successful")
                    .accountInfo(AccountInfo.builder()
                            .accountBalance(user.getAccountBalance())
                            .accountNumber(user.getAccountNumber())
                            .accountName(user.getFirstName() + " " + user.getLastName() + " " + user.getOtherName())
                            .token(token) // Include token in the response
                            .build())
                    .build();
        } catch (Exception e) {
            log.error("Login failed: {}", e.getMessage(), e);
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_FOUND_CODE)
                    .responseMessage("Invalid credentials")
                    .accountInfo(null)
                    .build();
        }
    }

    @Override
    public BankResponse getAccountDetails(String accountNumber) {
        log.info("Starting account details retrieval for accountNumber: {}", accountNumber);
        try {
            boolean accountExist = userRepository.existsByAccountNumber(accountNumber);
            if (!accountExist) {
                log.warn("Account with accountNumber {} does not exist", accountNumber);
                return BankResponse.builder()
                        .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                        .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                        .accountInfo(null)
                        .build();
            }
            User user = userRepository.findByAccountNumber(accountNumber);
            log.info("Account details retrieved successfully for accountNumber: {}", accountNumber);
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_FOUND_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountBalance(user.getAccountBalance())
                            .accountNumber(user.getAccountNumber())
                            .accountName(user.getFirstName() + " " + user.getLastName() + " " + user.getOtherName())
                            .build())
                    .build();
        } catch (ResourceNotFoundException e) {
            log.error("Account not found: {}", e.getMessage(), e);
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_FOUND_CODE)
                    .responseMessage(e.getMessage())
                    .accountInfo(null)
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error during account details retrieval", e);
            return BankResponse.builder()
                    .responseCode(AccountUtils.BALANCE_ENQUIRY_FAILURE_CODE)
                    .responseMessage(AccountUtils.BALANCE_ENQUIRY_FAILURE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
    }

    @Override
    public BankResponse updateAccount(UserRequest userRequest) {
        log.info("Starting account update for accountNumber: {}", userRequest.getAccountNumber());
        try {
            boolean accountExist = userRepository.existsByAccountNumber(userRequest.getAccountNumber());
            if (!accountExist) {
                log.warn("Account with accountNumber {} does not exist", userRequest.getAccountNumber());
                return BankResponse.builder()
                        .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                        .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                        .accountInfo(null)
                        .build();
            }
            User user = userRepository.findByAccountNumber(userRequest.getAccountNumber());
            user.setFirstName(userRequest.getFirstName());
            user.setLastName(userRequest.getLastName());
            user.setOtherName(userRequest.getOtherName());
            user.setGender(userRequest.getGender());
            user.setAddress(userRequest.getAddress());
            user.setStateOfOrigin(userRequest.getStateOfOrigin());
            user.setEmail(userRequest.getEmail());
            user.setPassword(userRequest.getPassword());
            user.setPhoneNumber(userRequest.getPhoneNumber());
            user.setAlternativePhoneNumber(userRequest.getAlternativePhoneNumber());
            userRepository.save(user);
            log.info("Account updated successfully for accountNumber: {}", userRequest.getAccountNumber());
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_CREATION_SUCCESS_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountBalance(user.getAccountBalance())
                            .accountNumber(user.getAccountNumber())
                            .accountName(user.getFirstName() + " " + user.getLastName() + " " + user.getOtherName())
                            .build())
                    .build();
        } catch (ResourceNotFoundException e) {
            log.error("Account update failed: {}", e.getMessage(), e);
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_FOUND_CODE)
                    .responseMessage(e.getMessage())
                    .accountInfo(null)
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error during account update", e);
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_CREATION_FAILURE_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_CREATION_FAILURE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
    }

    @Override
    public void deleteAccount(String accountNumber) {
        log.info("Starting account deletion for accountNumber: {}", accountNumber);
        try {
            boolean accountExist = userRepository.existsByAccountNumber(accountNumber);
            if (!accountExist) {
                log.warn("Account with accountNumber {} does not exist", accountNumber);
                throw new ResourceNotFoundException(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE);
            }
            userRepository.deleteByAccountNumber(accountNumber);
            log.info("Account deleted successfully for accountNumber: {}", accountNumber);
        } catch (ResourceNotFoundException e) {
            log.error("Account deletion failed: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error during account deletion", e);
        }
    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest enquiryRequest) {
        log.info("Starting balance enquiry for accountNumber: {}", enquiryRequest.getAccountNumber());
        try {
            boolean accountExist = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
            if (!accountExist) {
                log.warn("Account with accountNumber {} does not exist", enquiryRequest.getAccountNumber());
                return BankResponse.builder()
                        .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                        .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                        .accountInfo(null)
                        .build();
            }
            User user = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
            log.info("Balance enquiry successful for accountNumber: {}", enquiryRequest.getAccountNumber());
            return BankResponse.builder()
                    .responseCode(AccountUtils.BALANCE_ENQUIRY_SUCCESS_CODE)
                    .responseMessage(AccountUtils.BALANCE_ENQUIRY_SUCCESS_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountBalance(user.getAccountBalance())
                            .accountNumber(user.getAccountNumber())
                            .accountName(user.getFirstName() + " " + user.getLastName() + " " + user.getOtherName())
                            .build())
                    .build();
        } catch (ResourceNotFoundException e) {
            log.error("Balance enquiry failed: {}", e.getMessage(), e);
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_FOUND_CODE)
                    .responseMessage(e.getMessage())
                    .accountInfo(null)
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error during balance enquiry", e);
            return BankResponse.builder()
                    .responseCode(AccountUtils.BALANCE_ENQUIRY_FAILURE_CODE)
                    .responseMessage(AccountUtils.BALANCE_ENQUIRY_FAILURE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
    }

    @Override
    public String nameEnquiry(EnquiryRequest enquiryRequest) {
        log.info("Starting name enquiry for accountNumber: {}", enquiryRequest.getAccountNumber());
        try {
            boolean accountExist = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
            if (!accountExist) {
                log.warn("Account with accountNumber {} does not exist", enquiryRequest.getAccountNumber());
                return AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE;
            }
            User user = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
            log.info("Name enquiry successful for accountNumber: {}", enquiryRequest.getAccountNumber());
            return user.getFirstName() + " " + user.getLastName() + " " + user.getOtherName();
        } catch (ResourceNotFoundException e) {
            log.error("Name enquiry failed: {}", e.getMessage(), e);
            return e.getMessage();
        } catch (Exception e) {
            log.error("Unexpected error during name enquiry", e);
            return AccountUtils.NAME_ENQUIRY_FAILURE_MESSAGE;
        }
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
        log.info("Starting credit account for accountNumber: {}", request.getAccountNumber());
        try {
            boolean accountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
            if (!accountExist) {
                log.warn("Account with accountNumber {} does not exist", request.getAccountNumber());
                return BankResponse.builder()
                        .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                        .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                        .accountInfo(null)
                        .build();
            }
            User user = userRepository.findByAccountNumber(request.getAccountNumber());
            BigDecimal newBalance = user.getAccountBalance().add(request.getAmount());
            user.setAccountBalance(newBalance);
            userRepository.save(user);
            log.info("Account credited successfully for accountNumber: {}", request.getAccountNumber());
            TransactionDto transactionDto = TransactionDto.builder()
                    .accountNumber(user.getAccountNumber())
                    .transactionAmount(request.getAmount())
                    .transactionType("CREDIT")
                    .transactionDate(LocalDateTime.now())
                    .build();

            transactionService.saveTransaction(transactionDto);

            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_CREDITED_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountBalance(newBalance)
                            .accountNumber(user.getAccountNumber())
                            .accountName(user.getFirstName() + " " + user.getLastName() + " " + user.getOtherName())
                            .build())
                    .build();
        } catch (ResourceNotFoundException e) {
            log.error("Credit account failed: {}", e.getMessage(), e);
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_FOUND_CODE)
                    .responseMessage(e.getMessage())
                    .accountInfo(null)
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error during credit account", e);
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_CREATION_FAILURE_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_CREATION_FAILURE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
        log.info("Starting debit account for accountNumber: {}", request.getAccountNumber());
        try {
            boolean accountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
            if (!accountExist) {
                log.warn("Account with accountNumber {} does not exist", request.getAccountNumber());
                return BankResponse.builder()
                        .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                        .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                        .accountInfo(null)
                        .build();
            }
            User user = userRepository.findByAccountNumber(request.getAccountNumber());
            if (user.getAccountBalance().compareTo(request.getAmount()) < 0) {
                log.warn("Insufficient balance for accountNumber: {}", request.getAccountNumber());
                return BankResponse.builder()
                        .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                        .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                        .accountInfo(null)
                        .build();
            }
            BigDecimal newBalance = user.getAccountBalance().subtract(request.getAmount());
            user.setAccountBalance(newBalance);
            userRepository.save(user);
            log.info("Account debited successfully for accountNumber: {}", request.getAccountNumber());
            TransactionDto transactionDto = TransactionDto.builder()
                    .accountNumber(user.getAccountNumber())
                    .transactionAmount(request.getAmount())
                    .transactionType("DEBIT")
                    .transactionDate(LocalDateTime.now())
                    .build();

            transactionService.saveTransaction(transactionDto);

            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DEBITED_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_DEBITED_SUCCESS_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountBalance(newBalance)
                            .accountNumber(user.getAccountNumber())
                            .accountName(user.getFirstName() + " " + user.getLastName() + " " + user.getOtherName())
                            .build())
                    .build();
        } catch (ResourceNotFoundException e) {
            log.error("Debit account failed: {}", e.getMessage(), e);
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_FOUND_CODE)
                    .responseMessage(e.getMessage())
                    .accountInfo(null)
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error during debit account", e);
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_CREATION_FAILURE_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_CREATION_FAILURE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
    }
}