package dev.returntojava.katas.m01;

import java.util.List;
import java.util.Optional;

/**
 * Immutable order aggregate. State changes only through {@link #apply(OrderEvent)},
 * which returns a new Order. See BRIEF.md FR-8 to FR-12.
 */
public record Order(OrderId id, String customerId, List<LineItem> lines, OrderStatus status) {

    // TODO M1: compact constructor — validate and defensively copy (FR-8)

    /** Creates a new Pending order with a fresh id (FR-9). */
    public static Order place(String customerId, List<LineItem> lines) {
        throw new UnsupportedOperationException("M1: not implemented yet");
    }

    /** Sum of line totals (FR-9). */
    public Money total() {
        throw new UnsupportedOperationException("M1: not implemented yet");
    }

    /** Applies an event and returns the resulting order; never mutates this one (FR-10, FR-11). */
    public Order apply(OrderEvent event) {
        throw new UnsupportedOperationException("M1: not implemented yet");
    }

    /** Present only once the order has been shipped (FR-12). */
    public Optional<String> trackingId() {
        throw new UnsupportedOperationException("M1: not implemented yet");
    }
}
