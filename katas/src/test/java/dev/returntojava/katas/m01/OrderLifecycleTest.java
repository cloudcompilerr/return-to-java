package dev.returntojava.katas.m01;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.returntojava.katas.m01.OrderEvent.CancellationRequested;
import dev.returntojava.katas.m01.OrderEvent.DeliveryConfirmed;
import dev.returntojava.katas.m01.OrderEvent.PaymentCaptured;
import dev.returntojava.katas.m01.OrderEvent.ShipmentDispatched;
import dev.returntojava.katas.m01.OrderStatus.Cancelled;
import dev.returntojava.katas.m01.OrderStatus.Delivered;
import dev.returntojava.katas.m01.OrderStatus.Paid;
import dev.returntojava.katas.m01.OrderStatus.Pending;
import dev.returntojava.katas.m01.OrderStatus.Shipped;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class OrderLifecycleTest {

    static final Instant T0 = Instant.parse("2026-11-20T10:00:00Z");
    static final Instant T1 = T0.plusSeconds(60);
    static final Instant T2 = T0.plusSeconds(3_600);
    static final Instant T3 = T0.plusSeconds(86_400);

    static final OrderId ID = new OrderId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    static final List<LineItem> LINES = List.of(new LineItem(new Sku("MUG-01"), 1, Money.of("4.99", "GBP")));

    static Order orderIn(OrderStatus status) {
        return new Order(ID, "C-1", LINES, status);
    }

    @Test
    void happyPathPendingToDelivered() {
        Order pending = orderIn(new Pending());

        Order paid = pending.apply(new PaymentCaptured("PAY-9", T1));
        Order shipped = paid.apply(new ShipmentDispatched("TRK-7", T2));
        Order delivered = shipped.apply(new DeliveryConfirmed(T3));

        assertThat(paid.status()).isEqualTo(new Paid("PAY-9", T1));
        assertThat(shipped.status()).isEqualTo(new Shipped("TRK-7", T2));
        assertThat(delivered.status()).isEqualTo(new Delivered("TRK-7", T3));
        assertThat(delivered.id()).isEqualTo(ID);
        assertThat(delivered.lines()).isEqualTo(LINES);
    }

    @Test
    void applyNeverMutatesTheOriginal() {
        Order pending = orderIn(new Pending());
        pending.apply(new PaymentCaptured("PAY-9", T1));
        assertThat(pending.status()).isEqualTo(new Pending());
    }

    @Test
    void pendingOrPaidOrdersCanBeCancelled() {
        var cancel = new CancellationRequested("customer changed mind", T2);
        assertThat(orderIn(new Pending()).apply(cancel).status()).isEqualTo(new Cancelled("customer changed mind", T2));
        assertThat(orderIn(new Paid("PAY-9", T1)).apply(cancel).status()).isEqualTo(new Cancelled("customer changed mind", T2));
    }

    static Stream<Arguments> illegalTransitions() {
        OrderStatus[] states = {
                new Pending(), new Paid("PAY-9", T1), new Shipped("TRK-7", T2),
                new Delivered("TRK-7", T3), new Cancelled("fraud", T1)};
        OrderEvent[] events = {
                new PaymentCaptured("PAY-X", T3), new ShipmentDispatched("TRK-X", T3),
                new DeliveryConfirmed(T3), new CancellationRequested("late", T3)};
        // Legal: Pending+Payment, Pending+Cancel, Paid+Ship, Paid+Cancel, Shipped+Delivery.
        return Stream.of(states).flatMap(s -> Stream.of(events).map(e -> Arguments.of(s, e)))
                .filter(a -> !isLegal((OrderStatus) a.get()[0], (OrderEvent) a.get()[1]));
    }

    private static boolean isLegal(OrderStatus s, OrderEvent e) {
        return (s instanceof Pending && (e instanceof PaymentCaptured || e instanceof CancellationRequested))
                || (s instanceof Paid && (e instanceof ShipmentDispatched || e instanceof CancellationRequested))
                || (s instanceof Shipped && e instanceof DeliveryConfirmed);
    }

    @ParameterizedTest(name = "{0} + {1} is rejected")
    @MethodSource("illegalTransitions")
    void illegalTransitionsAreRejectedWithAUsefulMessage(OrderStatus status, OrderEvent event) {
        assertThatThrownBy(() -> orderIn(status).apply(event))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(status.getClass().getSimpleName())
                .hasMessageContaining(event.getClass().getSimpleName());
    }

    @Test
    void illegalTransitionCountIsFifteen() {
        assertThat(illegalTransitions()).hasSize(15);
    }

    @Test
    void terminalStates() {
        assertThat(new Pending().isTerminal()).isFalse();
        assertThat(new Paid("PAY-9", T1).isTerminal()).isFalse();
        assertThat(new Shipped("TRK-7", T2).isTerminal()).isFalse();
        assertThat(new Delivered("TRK-7", T3).isTerminal()).isTrue();
        assertThat(new Cancelled("fraud", T1).isTerminal()).isTrue();
    }

    @Test
    void trackingIdOnlyOnceShipped() {
        assertThat(orderIn(new Pending()).trackingId()).isEmpty();
        assertThat(orderIn(new Paid("PAY-9", T1)).trackingId()).isEmpty();
        assertThat(orderIn(new Shipped("TRK-7", T2)).trackingId()).contains("TRK-7");
        assertThat(orderIn(new Delivered("TRK-7", T3)).trackingId()).contains("TRK-7");
        assertThat(orderIn(new Cancelled("fraud", T1)).trackingId()).isEmpty();
    }

    @Test
    void eventsAndStatusesValidateTheirData() {
        assertThatThrownBy(() -> new PaymentCaptured(" ", T1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new ShipmentDispatched("TRK-7", null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new DeliveryConfirmed(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new CancellationRequested("", T1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Shipped(null, T2)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Cancelled("fraud", null)).isInstanceOf(NullPointerException.class);
    }
}
