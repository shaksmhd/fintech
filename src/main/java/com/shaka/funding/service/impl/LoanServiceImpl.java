package com.shaka.funding.service.impl;

import com.shaka.funding.dto.LoanRequest;
import com.shaka.funding.dto.LoanResponse;
import com.shaka.funding.entity.Loan;
import com.shaka.funding.repository.LoanRepository;
import com.shaka.funding.service.LoanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;

    @Override
    public LoanResponse applyForLoan(LoanRequest loanRequest) {
        log.info("Applying for loan for userId: {}", loanRequest.getUserId());

        // Calculate interest rate and total amount
        BigDecimal interestRate = calculateInterestRate(loanRequest.getTenure());
        BigDecimal totalAmount = loanRequest.getAmount().add(
                loanRequest.getAmount().multiply(interestRate.divide(BigDecimal.valueOf(100)))
        );

        // Create a new Loan entity
        Loan loan = Loan.builder()
                .userId(loanRequest.getUserId())
                .amount(loanRequest.getAmount())
                .interestRate(interestRate)
                .tenure(loanRequest.getTenure())
                .status("APPLIED")
                .totalAmount(totalAmount)
                .build();

        log.debug("Saving loan: {}", loan);
        Loan savedLoan = loanRepository.save(loan);

        log.info("Loan successfully applied for userId: {}. Loan ID: {}", savedLoan.getUserId(), savedLoan.getId());
        return new LoanResponse(savedLoan);
    }

    @Override
    public List<Loan> getLoansByUserId(Long userId) {
        log.info("Fetching loans for userId: {}", userId);

        List<Loan> loans = loanRepository.findByUserId(userId);
        log.debug("Found {} loans for userId: {}", loans.size(), userId);

        return loans;
    }

    @Override
    public LoanResponse updateLoanStatus(Long loanId, String status) {
        log.info("Updating loan status for loanId: {} to status: {}", loanId, status);

        Loan loan = (Loan) loanRepository.findById(loanId)
                .orElseThrow(() -> {
                    log.error("Loan not found for loanId: {}", loanId);
                    return new IllegalArgumentException("Loan not found");
                });

        log.debug("Current status of loanId {}: {}", loanId, loan.getStatus());
        loan.setStatus(status);
        Loan updatedLoan = loanRepository.save(loan);

        log.info("Loan status updated for loanId: {}. New status: {}", loanId, updatedLoan.getStatus());
        return new LoanResponse(updatedLoan);
    }

    private BigDecimal calculateInterestRate(Integer tenure) {
        log.debug("Calculating interest rate for tenure: {} months", tenure);

        // Simple logic to determine interest rate based on tenure
        BigDecimal interestRate;
        if (tenure <= 12) {
            interestRate = BigDecimal.valueOf(5); // 5% for <= 12 months
        } else if (tenure <= 24) {
            interestRate = BigDecimal.valueOf(10); // 10% for <= 24 months
        } else {
            interestRate = BigDecimal.valueOf(15); // 15% for > 24 months
        }

        log.debug("Calculated interest rate: {}%", interestRate);
        return interestRate;
    }
}
