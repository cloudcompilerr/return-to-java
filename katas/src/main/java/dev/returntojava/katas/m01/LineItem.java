package dev.returntojava.katas.m01;

/** One order line: a SKU, a quantity and the unit price. See BRIEF.md FR-6. */
public record LineItem(Sku sku, int quantity, Money unitPrice) {

    // TODO M1: compact constructor — validate (FR-6)

    public Money total() {
        throw new UnsupportedOperationException("M1: not implemented yet");
    }
}
