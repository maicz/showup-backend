package com.mz.examples;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.Objects;

// Before records — 30+ lines of boilerplate
public record BankOrder(
    String     orderId,
    String     accountId,
    OrderType  type,
    BigDecimal amount,
    Currency currency,
    Instant createdAt
) {
    // Compact canonical constructor — validate in here
    public BankOrder {
        Objects.requireNonNull(orderId, "orderId required");
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("amount must be positive");
    }

    // Custom instance method — records CAN have behaviour
    public boolean isHighValue() {
        return amount.compareTo(new BigDecimal("10000")) > 0;
    }

    // Wither — return a copy with one field changed
    public BankOrder withAmount(BigDecimal newAmount) {
        return new BankOrder(orderId, accountId, type,
                              newAmount, currency, createdAt);
    }
}