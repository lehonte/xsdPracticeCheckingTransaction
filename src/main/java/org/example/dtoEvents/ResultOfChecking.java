package org.example.dtoEvents;

import lombok.Builder;
import org.example.enums.TransactionStatus;

@Builder
public record ResultOfChecking(TransactionStatus status, String transactionNumber, String reason) {
}
