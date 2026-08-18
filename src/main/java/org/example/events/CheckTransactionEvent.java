package org.example.events;

import java.math.BigDecimal;

public record CheckTransactionEvent(String phoneNumber, String transactionNumber, BigDecimal amount, String owner) {
}
