package org.example.utils.email;

import java.math.BigDecimal;

public record DataToSend(String code, String email, BigDecimal amount, String owner) {
}
