package dev.returntojava.katas.m01;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.returntojava.katas.m01.OrderStatus.Pending;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class OrderTest {

    static final LineItem MUG = new LineItem(new Sku("MUG-01"), 2, Money.of("4.99", "GBP"));
    static final LineItem TEA = new LineItem(new Sku("TEA-42"), 1, Money.of("12.50", "GBP"));

    @Test
    void placeCreatesPendingOrderWithFreshId() {
        Order first = Order.place("C-1", List.of(MUG, TEA));
        Order second = Order.place("C-1", List.of(MUG, TEA));

        assertThat(first.status()).isEqualTo(new Pending());
        assertThat(first.id()).isNotNull().isNotEqualTo(second.id());
        assertThat(first.lines()).containsExactly(MUG, TEA);
    }

    @Test
    void totalSumsLines() {
        assertThat(Order.place("C-1", List.of(MUG, TEA)).total()).isEqualTo(Money.of("22.48", "GBP"));
    }

    @Test
    void linesAreDefensivelyCopiedAndUnmodifiable() {
        List<LineItem> input = new ArrayList<>(List.of(MUG));
        Order order = Order.place("C-1", input);

        input.add(TEA);

        assertThat(order.lines()).containsExactly(MUG);
        assertThatThrownBy(() -> order.lines().add(TEA)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void rejectsEmptyLines() {
        assertThatThrownBy(() -> Order.place("C-1", List.of())).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsNullLineElement() {
        assertThatThrownBy(() -> Order.place("C-1", Arrays.asList(MUG, null))).isInstanceOf(NullPointerException.class);
    }

    @Test
    void rejectsMixedCurrencies() {
        var euroLine = new LineItem(new Sku("TEA-42"), 1, Money.of("12.50", "EUR"));
        assertThatThrownBy(() -> Order.place("C-1", List.of(MUG, euroLine))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsDuplicateSkus() {
        var moreMugs = new LineItem(new Sku("MUG-01"), 5, Money.of("4.99", "GBP"));
        assertThatThrownBy(() -> Order.place("C-1", List.of(MUG, moreMugs))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsBlankOrNullCustomer() {
        assertThatThrownBy(() -> Order.place(" ", List.of(MUG))).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Order.place(null, List.of(MUG))).isInstanceOf(NullPointerException.class);
    }

    @Test
    void rejectsNullIdAndStatus() {
        var id = new OrderId(UUID.randomUUID());
        assertThatThrownBy(() -> new Order(null, "C-1", List.of(MUG), new Pending())).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Order(id, "C-1", List.of(MUG), null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new OrderId(null)).isInstanceOf(NullPointerException.class);
    }
}
