package org.example.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "successful_transactions")
@Getter
@Setter
@NoArgsConstructor
public class SuccessfulTransaction {
    @Id
    @Column(name = "transaction_number")
    private String transactionNumber;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "owner", nullable = false)
    private String owner;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    public SuccessfulTransaction(String transactionNumber, String phoneNumber, BigDecimal amount, String owner) {
        this.transactionNumber = transactionNumber;
        this.phoneNumber = phoneNumber;
        this.amount = amount;
        this.owner = owner;
        this.processedAt = LocalDateTime.now();
    }
}
