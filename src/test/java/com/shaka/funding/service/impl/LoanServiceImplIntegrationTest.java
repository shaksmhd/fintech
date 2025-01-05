package com.shaka.funding.service.impl;

import com.shaka.funding.dto.LoanRequest;
import com.shaka.funding.entity.Loan;
import com.shaka.funding.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LoanServiceImplIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanServiceImpl loanService;

    private LoanRequest loanRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create a sample loan request
        loanRequest = new LoanRequest();
        loanRequest.setUserId(1L);
        loanRequest.setAmount(BigDecimal.valueOf(1000));
        loanRequest.setTenure(12); // 12 months
    }

    @Test
//    @WithMockUser(username = "testUser", roles = {"USER"}) // Simulate an authenticated user
    void testApplyForLoan() throws Exception {
        // Arrange: Mock the loan repository behavior
        Loan savedLoan = new Loan();
        savedLoan.setId(1L);
        savedLoan.setUserId(1L);
        savedLoan.setAmount(BigDecimal.valueOf(1000));
        savedLoan.setTenure(12);
        savedLoan.setInterestRate(BigDecimal.valueOf(5)); // 5% interest
        savedLoan.setTotalAmount(BigDecimal.valueOf(1050)); // Total = Amount + (Amount * Interest Rate)
        savedLoan.setStatus("APPLIED");

        when(loanRepository.save(any(Loan.class))).thenReturn(savedLoan);

        // Act: Perform the loan application request
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/loans/apply")
                        .contentType("application/json")
                        .content("{ \"userId\": 1, \"amount\": 1000, \"tenure\": 12 }"))
                .andExpect(status().isOk()) // Expect 200 OK
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.status").value("APPLIED"))
                .andReturn();

        // Verify that the loan repository was called
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    void testGetLoansByUserId() throws Exception {
        // Arrange: Mock the loan repository behavior
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setUserId(1L);
        loan.setAmount(BigDecimal.valueOf(1000));
        loan.setTenure(12);
        loan.setInterestRate(BigDecimal.valueOf(5)); // 5% interest
        loan.setTotalAmount(BigDecimal.valueOf(1050));
        loan.setStatus("APPLIED");

        when(loanRepository.findByUserId(1L)).thenReturn(List.of(loan));

        // Act: Perform the get loans request for userId 1
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/loans/user/1")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].status").value("APPLIED"))
                .andReturn();

        // Verify the repository method was called
        verify(loanRepository, times(1)).findByUserId(1L);
    }

    @Test
    void testUpdateLoanStatus() throws Exception {
        // Arrange: Mock the loan repository behavior
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setUserId(1L);
        loan.setAmount(BigDecimal.valueOf(1000));
        loan.setTenure(12);
        loan.setInterestRate(BigDecimal.valueOf(5));
        loan.setTotalAmount(BigDecimal.valueOf(1050));
        loan.setStatus("APPLIED");

        when(loanRepository.findById(1L)).thenReturn(java.util.Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        // Act: Perform the update loan status request
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.patch("/loans/1/status")
                        .contentType("application/json")
                        .content("{ \"status\": \"APPROVED\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andReturn();

        // Verify that the loan status was updated
        verify(loanRepository, times(1)).save(any(Loan.class));
    }
}