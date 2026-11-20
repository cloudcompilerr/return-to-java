# M1 · Order domain model

**Day 1 · Timebox 90 min** · Module `katas/` · Package `dev.returntojava.katas.m01`

## 1. Business context

fulfil-lite is the order core of a mid-size online retailer. Last quarter, two incidents came from the old Java 8 model. First, `double` prices summed to £22.479999 on invoices. Second, a mutable `List` shared between the basket and the order let a shipped order change its own contents. You're rebuilding the domain kernel so that invalid orders can't be constructed and state changes are explicit, total and auditable. Everything in M5–M12 builds on these types.

## 2. Functional requirements

**Money**
- **FR-1** `Money(amount, currency)`: both non-null (NPE). Amount ≥ 0 (IAE). The currency must define a minor unit, so `Currency#getDefaultFractionDigits() ≥ 0`; `XXX` is rejected (IAE).
- **FR-2** Normalise the scale to the currency's minor units: `10 GBP` → `10.00`. Reject amounts that would need rounding, such as `10.001 GBP` or `100.5 JPY` (IAE). `Money.of("10","GBP")` must equal `new Money(new BigDecimal("10.000"), GBP)`, with equal hash codes.
- **FR-3** `Money.of(String, String)` parses its input. Malformed numbers and unknown currency codes are rejected with IAE.
- **FR-4** `plus` works only within one currency (IAE otherwise). `times(int)` rejects negative quantities (IAE); `times(0)` is zero. `zero(Currency)` is normalised.

**Line items**
- **FR-5** `Sku` must be non-null (NPE) and match `[A-Z0-9-]{3,32}` (IAE).
- **FR-6** `LineItem`: non-null parts (NPE), quantity ≥ 1 (IAE). `total()` = unit price × quantity.

**Order**
- **FR-7** `OrderId` wraps a non-null `UUID`. `newId()` returns a random one.
- **FR-8** `Order`: id, customerId and status are non-null (NPE). customerId must not be blank (IAE). Lines must be non-empty (IAE), contain no null elements (NPE), all share one currency (IAE) and contain no duplicate SKU (IAE). `lines()` is unmodifiable and unaffected by later changes to the caller's list.
- **FR-9** `Order.place(customerId, lines)` creates a `Pending` order with a fresh id. `total()` sums the line totals.
- **FR-10** `apply(OrderEvent)` returns a **new** `Order`; the receiver never changes. Legal transitions:

  | From | Event | To |
  |---|---|---|
  | Pending | PaymentCaptured(ref, at) | Paid(ref, at) |
  | Pending | CancellationRequested(reason, at) | Cancelled(reason, at) |
  | Paid | ShipmentDispatched(trk, at) | Shipped(trk, at) |
  | Paid | CancellationRequested(reason, at) | Cancelled(reason, at) |
  | Shipped | DeliveryConfirmed(at) | Delivered(*trk from Shipped*, at) |

- **FR-11** Every other (status, event) pair, 15 in total, throws `IllegalStateException`. The message names both the status and the event simple class names, e.g. `Cannot apply ShipmentDispatched to order in state Pending`.
- **FR-12** `isTerminal()` is true for Delivered and Cancelled only. `trackingId()` is present for Shipped and Delivered only.
- **FR-13** Status and event records validate their data: text fields non-null (NPE) and non-blank (IAE); instants non-null (NPE).

Exception convention throughout: null → `NullPointerException`; bad value → `IllegalArgumentException`; illegal state change → `IllegalStateException`.

## 3. Non-functional requirements

- **Immutability:** every type is deeply immutable and therefore safe to share across threads without locks. That property is the foundation for M3, M4 and M9.
- **Exactness:** no binary floating point anywhere in money arithmetic.
- **Allocation:** `apply` allocates one new `Order` and one new status. Lines are shared, not copied again. ASSUMPTION: about 2k orders/s at peak, about 5 events per order, so roughly 10k `apply` calls/s. That's trivial for the JVM, provided you don't re-validate by sorting or copying on every transition.

## 4. Constraints

- Records for every value type; no Lombok, no setters.
- Use **no `default` branch** in any `switch` over `OrderStatus` or `OrderEvent`. The compiler must prove exhaustiveness, so adding a sixth status breaks the build.
- No `instanceof` chains plus casts in `Order`. Use pattern matching (`switch` with record patterns, guards via `when`).
- No `double` or `float` anywhere in `m01`.
- Don't rename or remove anything in the provided skeleton signatures; the tests depend on them. You may add private helpers or package-private classes.
- Don't edit the tests. If you think one is wrong, say so in `review`.

## 5. What you'll practise

| Concept | Tag | Official doc |
|---|---|---|
| Records, compact canonical constructors | [FINAL in JDK 16] | https://openjdk.org/jeps/395 |
| Sealed interfaces | [FINAL in JDK 17] | https://openjdk.org/jeps/409 |
| Pattern matching for `switch`, guards (`when`), exhaustiveness | [FINAL in JDK 21] | https://openjdk.org/jeps/441 |
| Record patterns (deconstruction) | [FINAL in JDK 21] | https://openjdk.org/jeps/440 |
| Unnamed variables & patterns (`_`) | [FINAL in JDK 22] | https://openjdk.org/jeps/456 |
| Flexible constructor bodies (stretch only) | [FINAL in JDK 25] | https://openjdk.org/jeps/513 |
| `BigDecimal` scale, `RoundingMode.UNNECESSARY`, `equals` vs `compareTo` | — | https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/math/BigDecimal.html |
| `List.copyOf` (unmodifiable, null-hostile) | [FINAL in JDK 10] | https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/List.html#copyOf(java.util.Collection) |
| `Optional` as a return type | [FINAL in JDK 8] | https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/Optional.html |

## 6. Deliverables

Implement the bodies in `katas/src/main/java/dev/returntojava/katas/m01/`:
`Money`, `Sku`, `LineItem`, `OrderId`, `OrderStatus`, `OrderEvent`, `Order`.

**Definition of done:** `./mvnw verify` is green. The provided tests are `MoneyTest`, `LineItemTest`, `OrderTest` and `OrderLifecycleTest`: 57 tests, all currently failing. With the 3 smoke tests that makes 60.

Run just this mission's tests: `./mvnw -pl katas test -Dtest='dev.returntojava.katas.m01.*Test'`

## 7. Timebox

Target **90 min**. At 135 min, use `hint`, or `autopilot` if you're out of time.

## 8. MAANG bar: what a senior reviewer checks

1. **`BigDecimal` equality.** `new BigDecimal("10").equals(new BigDecimal("10.00"))` is **false**. How does your record keep `equals` and `hashCode` consistent? (FR-2 tests exactly this.)
2. **Defensive copying in a record.** A record's components are shallowly final. Where does the copy happen, and what does `List.copyOf` give you beyond a copy?
3. **Exhaustive, total state machine.** One `switch` that makes every legal transition visible at a glance, with no `default` hiding future states. Can a reader verify FR-10 in about 10 seconds?
4. **Validation in exactly one place.** Does every construction path, including `apply`, go through the canonical constructor? Is there any way to build an invalid `Order`?
5. **Exception discipline.** Each failure has the right exception type and a message an on-call engineer could act on.

**Classic traps:** `double` for money; `Collections.unmodifiableList(input)` (a view, not a copy); `default -> throw` in the switch; `Optional` fields or parameters; mutating `this` in `apply`; `BigDecimal.setScale` with silent `HALF_UP` rounding.

## 9. Stretch (optional)

- Add a `Money(String amount, String currencyCode)` convenience constructor that does its parsing *before* calling `this(...)`. Before JDK 25 that wasn't legal ([FINAL in JDK 25], JEP 513). Is it better than the static factory? Argue one side.
- Make `apply` reject events whose timestamp is earlier than the current status's timestamp. Which type should own "the timestamp of a status"?
