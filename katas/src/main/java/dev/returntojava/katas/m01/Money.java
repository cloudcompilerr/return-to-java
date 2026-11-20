package dev.returntojava.katas.m01;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * A non-negative monetary amount in a single ISO-4217 currency.
 * See missions/M01-order-domain-model/BRIEF.md, FR-1 to FR-4.
 */
public record Money(BigDecimal amount, Currency currency) {

    // TODO M1: compact constructor — validate and normalise (FR-1, FR-2)

    public static Money of(String amount, String currencyCode) {
        throw new UnsupportedOperationException("M1: not implemented yet");
    }

    public static Money zero(Currency currency) {
        throw new UnsupportedOperationException("M1: not implemented yet");
    }

    public Money plus(Money other) {
        throw new UnsupportedOperationException("M1: not implemented yet");
    }

    public Money times(int quantity) {
        throw new UnsupportedOperationException("M1: not implemented yet");
    }
}
