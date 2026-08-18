package org.example.eventEntities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "producessed_transactions")
@NoArgsConstructor
@Getter
@Setter
public class ProducessedTransactions {

    @Id
    @Column(name = "transaction_number")
    private String transactionNumber;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    public ProducessedTransactions(String transactionNumber) {
        this.transactionNumber = transactionNumber;
        this.processedAt = LocalDateTime.now();
    }
}
