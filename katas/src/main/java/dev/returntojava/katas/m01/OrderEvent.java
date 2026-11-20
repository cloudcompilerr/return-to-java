package dev.returntojava.katas.m01;

import java.time.Instant;

/** Something that happened to an order. See BRIEF.md FR-10 and FR-13. */
public sealed interface OrderEvent {

    Instant at();

    record PaymentCaptured(String paymentRef, Instant at) implements OrderEvent {
        // TODO M1: validate (FR-13)
    }

    record ShipmentDispatched(String trackingId, Instant at) implements OrderEvent {
        // TODO M1: validate (FR-13)
    }

    record DeliveryConfirmed(Instant at) implements OrderEvent {
        // TODO M1: validate (FR-13)
    }

    record CancellationRequested(String reason, Instant at) implements OrderEvent {
        // TODO M1: validate (FR-13)
    }
}
