package dev.returntojava.katas.m01;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class LineItemTest {

    @Test
    void acceptsWellFormedSku() {
        assertThat(new Sku("ABC-123").value()).isEqualTo("ABC-123");
    }

    @ParameterizedTest(name = "\"{0}\" is not a valid SKU")
    @ValueSource(strings = {"", "  ", "AB", "abc-123", "ABC 123", "ABC_123", "A23456789012345678901234567890123"})
    void rejectsMalformedSku(String raw) {
        assertThatThrownBy(() -> new Sku(raw)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsNullSku() {
        assertThatThrownBy(() -> new Sku(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void totalIsUnitPriceTimesQuantity() {
        var line = new LineItem(new Sku("MUG-01"), 3, Money.of("4.99", "GBP"));
        assertThat(line.total()).isEqualTo(Money.of("14.97", "GBP"));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void rejectsNonPositiveQuantity(int quantity) {
        assertThatThrownBy(() -> new LineItem(new Sku("MUG-01"), quantity, Money.of("1", "GBP")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsNullParts() {
        assertThatThrownBy(() -> new LineItem(null, 1, Money.of("1", "GBP"))).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new LineItem(new Sku("MUG-01"), 1, null)).isInstanceOf(NullPointerException.class);
    }
}
