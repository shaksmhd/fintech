package com.shaka.funding.service.impl;

import com.shaka.funding.dto.BankResponse;
import com.shaka.funding.dto.LoginRequest;
import com.shaka.funding.dto.UserRequest;
import com.shaka.funding.entity.Role;
//import com.shaka.funding.entity.User;
import com.shaka.funding.entity.User;
import com.shaka.funding.repository.UserRepository;
import com.shaka.funding.service.JwtService;
import com.shaka.funding.service.TransactionService;
import com.shaka.funding.utils.AccountUtils;
import org.apache.catalina.Authenticator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.mockito.ArgumentMatchers.any;


import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private TransactionService transactionService;
    @Mock
    private JwtService jwtService;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRequest userRequest;
    @Mock
    private AuthenticationManager authenticationManager;

//    @InjectMocks
//    private YourServiceClass yourService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userRequest = new UserRequest();
        userRequest.setFirstName("John");
        userRequest.setLastName("Doe");
        userRequest.setEmail("john.doe@example.com");
        userRequest.setPassword("password123");
        userRequest.setPhoneNumber("123456789");
        userRequest.setGender("male");
        userRequest.setAlternativePhoneNumber("");
        userRequest.setAddress("Lagos");
        userRequest.setDateOfBirth("2021-08-01");
        userRequest.setStateOfOrigin("Lagos");
        userRequest.setRole(Role.USER);
    }

    @Test
    void createAccount_ShouldReturnSuccess_WhenUserIsNew() {
        // Arrange
        when(userRepository.existsByEmail(userRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(userRequest.getPassword())).thenReturn("password123");
        when(userRepository.save(ArgumentMatchers.any(User.class))).thenReturn(new User());

        // Act
        BankResponse response = userService.createAccount(userRequest);

        // Assert
        assertNotNull(response);
        assertEquals(AccountUtils.ACCOUNT_CREATION_SUCCESS_CODE, response.getResponseCode());
        assertEquals(AccountUtils.ACCOUNT_CREATION_SUCCESS_MESSAGE, response.getResponseMessage());
    }
    @Test
    void createAccount_ShouldReturnError_WhenUserAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmail(userRequest.getEmail())).thenReturn(true);

        // Act
        BankResponse response = userService.createAccount(userRequest);

        // Assert
        assertNotNull(response);
        assertEquals(AccountUtils.ACCOUNT_EXIST_CODE, response.getResponseCode());
        assertEquals("User with email john.doe@example.com already exists", response.getResponseMessage());
    }

    @Test
    void login_ShouldReturnSuccess_WhenCredentialsAreValid() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("john.doe@example.com", "password123");
        User mockUser = new User();
        mockUser.setAccountNumber("123456789");
        mockUser.setAccountBalance(BigDecimal.valueOf(1000));
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(mockUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("mock-jwt-token");
        when(authenticationManager.authenticate(any())).thenReturn(null); // Simulate successful authentication

        // Act
        BankResponse response = userService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals(AccountUtils.ACCOUNT_FOUND_CODE, response.getResponseCode());
        assertEquals("Login successful", response.getResponseMessage());
        assertNotNull(response.getAccountInfo().getToken());
    }

    @Test
    void login_ShouldReturnError_WhenUserNotFound() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("non.existing@example.com", "password123");
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(null);

        // Act
        BankResponse response = userService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals(AccountUtils.ACCOUNT_NOT_EXIST_CODE, response.getResponseCode());
        assertEquals("Invalid credentials", response.getResponseMessage());
    }


}