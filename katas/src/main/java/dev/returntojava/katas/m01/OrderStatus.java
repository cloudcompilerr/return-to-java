package dev.returntojava.katas.m01;

import java.time.Instant;

/**
 * Lifecycle state of an order. The set of states is closed: adding one must
 * break compilation everywhere it is switched on. See BRIEF.md FR-11 to FR-13.
 */
public sealed interface OrderStatus {

    record Pending() implements OrderStatus {}

    record Paid(String paymentRef, Instant paidAt) implements OrderStatus {
        // TODO M1: validate (FR-13)
    }

    record Shipped(String trackingId, Instant shippedAt) implements OrderStatus {
        // TODO M1: validate (FR-13)
    }

    record Delivered(String trackingId, Instant deliveredAt) implements OrderStatus {
        // TODO M1: validate (FR-13)
    }

    record Cancelled(String reason, Instant cancelledAt) implements OrderStatus {
        // TODO M1: validate (FR-13)
    }

    /** True for states no event can leave (FR-12). */
    default boolean isTerminal() {
        throw new UnsupportedOperationException("M1: not implemented yet");
    }
}
