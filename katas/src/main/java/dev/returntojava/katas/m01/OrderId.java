package dev.returntojava.katas.m01;

import java.util.UUID;

/** Order identity. See BRIEF.md FR-7. */
public record OrderId(UUID value) {

    // TODO M1: compact constructor — validate (FR-7)

    public static OrderId newId() {
        throw new UnsupportedOperationException("M1: not implemented yet");
    }
}
