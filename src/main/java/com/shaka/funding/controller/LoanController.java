package com.shaka.funding.controller;

import com.shaka.funding.dto.LoanRequest;
import com.shaka.funding.dto.LoanResponse;
import com.shaka.funding.entity.Loan;
import com.shaka.funding.service.LoanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/loans")
public class LoanController {

    private final LoanService loanService;

    /**
     * Endpoint to apply for a loan
     * @param loanRequest LoanRequest object containing userId, amount, and tenure
     * @return LoanResponse with loan details
     */
    @PostMapping("/apply")
    public ResponseEntity<LoanResponse> applyForLoan(@RequestBody LoanRequest loanRequest) {
        log.info("Received loan application request: {}", loanRequest);
        LoanResponse loanResponse = loanService.applyForLoan(loanRequest);
        log.info("Loan application processed: {}", loanResponse);
        return ResponseEntity.ok(loanResponse);
    }

    /**
     * Endpoint to fetch loans by user ID
     * @param userId User ID
     * @return List of loans for the specified user
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LoanResponse>> getLoansByUserId(@PathVariable Long userId) {
        log.info("Fetching loans for user ID: {}", userId);
        List<Loan> loans = loanService.getLoansByUserId(userId);
        List<LoanResponse> loanResponses = loans.stream()
                .map(LoanResponse::new)
                .collect(Collectors.toList());
        log.info("Fetched {} loans for user ID: {}", loanResponses.size(), userId);
        return ResponseEntity.ok(loanResponses);
    }

    /**
     * Endpoint to update the status of a loan
     * @param loanId Loan ID
     * @param status New status for the loan
     * @return Updated LoanResponse
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{loanId}/status")
    public ResponseEntity<LoanResponse> updateLoanStatus(
            @PathVariable Long loanId,
            @RequestParam String status) {
        log.info("Updating status for loan ID: {} to {}", loanId, status);
        LoanResponse loanResponse = loanService.updateLoanStatus(loanId, status);
        log.info("Loan status updated: {}", loanResponse);
        return ResponseEntity.ok(loanResponse);
    }
}
