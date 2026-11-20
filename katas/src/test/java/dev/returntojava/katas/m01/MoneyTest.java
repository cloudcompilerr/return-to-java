package dev.returntojava.katas.m01;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.Currency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MoneyTest {

    private static final Currency GBP = Currency.getInstance("GBP");
    private static final Currency JPY = Currency.getInstance("JPY");

    @Test
    void normalisesScaleToCurrencyMinorUnits() {
        assertThat(Money.of("10", "GBP").amount()).isEqualTo(new BigDecimal("10.00"));
        assertThat(Money.of("10", "GBP").amount().scale()).isEqualTo(2);
        assertThat(Money.of("500", "JPY").amount().scale()).isZero();
    }

    @Test
    void equalAmountsWithDifferentScalesAreEqualAndHashAlike() {
        Money a = new Money(new BigDecimal("10"), GBP);
        Money b = new Money(new BigDecimal("10.000"), GBP);
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @ParameterizedTest(name = "{0} {1} is rejected")
    @CsvSource({
            "10.001, GBP",   // more decimals than GBP allows
            "100.5,  JPY",   // JPY has no minor unit
            "-0.01,  GBP",   // negative
            "abc,    GBP",   // not a number
            "10,     XYZ",   // unknown currency
            "10,     XXX",   // ISO code with no defined minor unit
    })
    void rejectsInvalidAmounts(String amount, String currency) {
        assertThatThrownBy(() -> Money.of(amount, currency)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsNulls() {
        assertThatThrownBy(() -> new Money(null, GBP)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Money(BigDecimal.ONE, null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void addsSameCurrency() {
        assertThat(Money.of("10.50", "GBP").plus(Money.of("0.75", "GBP"))).isEqualTo(Money.of("11.25", "GBP"));
    }

    @Test
    void refusesToAddDifferentCurrencies() {
        assertThatThrownBy(() -> Money.of("1", "GBP").plus(Money.of("1", "EUR")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void multipliesByQuantity() {
        assertThat(Money.of("0.10", "GBP").times(3)).isEqualTo(Money.of("0.30", "GBP"));
        assertThat(Money.of("9.99", "GBP").times(0)).isEqualTo(Money.zero(GBP));
        assertThatThrownBy(() -> Money.of("1", "GBP").times(-1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void zeroIsNormalised() {
        assertThat(Money.zero(JPY)).isEqualTo(Money.of("0", "JPY"));
        assertThat(Money.zero(GBP).amount().scale()).isEqualTo(2);
    }
}
