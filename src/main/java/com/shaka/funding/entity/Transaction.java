package com.shaka.funding.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbl_transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String transactionId;
    private String transactionType;
    private LocalDate transactionDate;
    private LocalTime transactionTime;
    private BigDecimal transactionAmount;
    private String accountNumber;
    private String status;

}
